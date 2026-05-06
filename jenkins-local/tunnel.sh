#!/usr/bin/env bash
# tunnel.sh — start a Cloudflare quick tunnel exposing the local Jenkins on
# port 8080, write the public URL into .env (JENKINS_URL=...), and reload the
# Jenkins container so JCasC picks it up.
#
# Use cases:
# - Make Telegram alert links clickable for everyone in the group
# - Share an Allure report with a colleague without deploying anywhere
#
# Usage:
#   ./tunnel.sh start    # boot tunnel, update .env, restart Jenkins
#   ./tunnel.sh stop     # kill the tunnel and revert .env to localhost
#   ./tunnel.sh status   # show current state (running PID + URL)
#
# Notes:
# - "Quick" tunnels get a fresh *.trycloudflare.com URL on every restart.
#   For a stable URL, set up a named tunnel via Cloudflare dashboard
#   (free, requires a domain on Cloudflare) and replace this script's
#   `tunnel --url` invocation with `tunnel run <name>`.
# - State (PID + URL) is kept in .tunnel-state to allow `stop`/`status`.

set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$DIR/.env"
STATE_FILE="$DIR/.tunnel-state"
LOG_FILE="$DIR/.tunnel.log"
LOCAL_URL="http://localhost:8080"

require_cloudflared() {
    if ! command -v cloudflared >/dev/null 2>&1; then
        echo "cloudflared not found. Install: brew install cloudflared" >&2
        exit 1
    fi
}

require_env() {
    if [ ! -f "$ENV_FILE" ]; then
        echo ".env not found at $ENV_FILE — copy .env.example first" >&2
        exit 1
    fi
}

start_tunnel() {
    require_cloudflared
    require_env

    if [ -f "$STATE_FILE" ]; then
        # shellcheck disable=SC1090
        source "$STATE_FILE"
        if [ -n "${TUNNEL_PID:-}" ] && kill -0 "$TUNNEL_PID" 2>/dev/null; then
            echo "Tunnel already running: PID=$TUNNEL_PID URL=$TUNNEL_URL"
            return 0
        fi
    fi

    echo "Starting cloudflared quick tunnel → $LOCAL_URL ..."
    cloudflared tunnel --url "$LOCAL_URL" > "$LOG_FILE" 2>&1 &
    local pid=$!

    # Wait up to ~25s for the URL to appear in cloudflared's log
    local url=""
    for _ in $(seq 1 25); do
        url=$(grep -oE 'https://[a-z0-9-]+\.trycloudflare\.com' "$LOG_FILE" 2>/dev/null | head -1 || true)
        if [ -n "$url" ]; then break; fi
        sleep 1
    done

    if [ -z "$url" ]; then
        echo "Tunnel did not produce a URL within 25s. Check $LOG_FILE" >&2
        kill "$pid" 2>/dev/null || true
        exit 1
    fi

    cat > "$STATE_FILE" <<EOF
TUNNEL_PID=$pid
TUNNEL_URL=$url
TUNNEL_STARTED=$(date +%s)
EOF

    # Verify the tunnel actually proxies through to Jenkins (retry up to ~15s)
    local code="000"
    for _ in $(seq 1 15); do
        code=$(curl -sS -o /dev/null -w '%{http_code}' --max-time 3 "$url/login" 2>/dev/null || true)
        if [ "$code" = "200" ]; then break; fi
        sleep 1
    done
    echo "Tunnel reach test (login): HTTP $code"

    # Update JENKINS_URL in .env, then reload the Jenkins container
    sed -i.bak "s|^JENKINS_URL=.*|JENKINS_URL=$url/|" "$ENV_FILE"
    rm -f "$ENV_FILE.bak"
    echo "Updated JENKINS_URL → $url/"

    if command -v docker >/dev/null 2>&1 && [ -f "$DIR/docker-compose.yml" ]; then
        echo "Reloading Jenkins (docker compose up -d) ..."
        (cd "$DIR" && docker compose up -d) >/dev/null
    fi

    echo ""
    echo "✅ Tunnel ready"
    echo "   PID: $pid"
    echo "   URL: $url"
    echo "   Log: $LOG_FILE"
    echo ""
    echo "Telegram alerts will now include this URL in build/Allure/Console links."
    echo "Stop the tunnel with: ./tunnel.sh stop"
}

stop_tunnel() {
    if [ ! -f "$STATE_FILE" ]; then
        echo "No tunnel state file — nothing to stop."
        return 0
    fi
    # shellcheck disable=SC1090
    source "$STATE_FILE"

    if [ -n "${TUNNEL_PID:-}" ] && kill -0 "$TUNNEL_PID" 2>/dev/null; then
        kill "$TUNNEL_PID" 2>/dev/null || true
        echo "Stopped tunnel PID=$TUNNEL_PID"
    else
        echo "Tunnel PID=${TUNNEL_PID:-?} already gone."
    fi

    # Revert JENKINS_URL to localhost so future starts don't reuse a dead URL
    if [ -f "$ENV_FILE" ]; then
        sed -i.bak "s|^JENKINS_URL=.*|JENKINS_URL=$LOCAL_URL/|" "$ENV_FILE"
        rm -f "$ENV_FILE.bak"
        echo "Reverted JENKINS_URL → $LOCAL_URL/"
    fi

    rm -f "$STATE_FILE"

    if command -v docker >/dev/null 2>&1 && [ -f "$DIR/docker-compose.yml" ]; then
        (cd "$DIR" && docker compose up -d) >/dev/null 2>&1 || true
    fi
}

status_tunnel() {
    if [ ! -f "$STATE_FILE" ]; then
        echo "No tunnel running."
        return 0
    fi
    # shellcheck disable=SC1090
    source "$STATE_FILE"
    echo "PID:     ${TUNNEL_PID:-?}"
    echo "URL:     ${TUNNEL_URL:-?}"
    echo "Started: $(date -r "${TUNNEL_STARTED:-0}" 2>/dev/null || echo unknown)"
    if [ -n "${TUNNEL_PID:-}" ] && kill -0 "$TUNNEL_PID" 2>/dev/null; then
        echo "State:   running"
    else
        echo "State:   STALE (process dead — run ./tunnel.sh stop to cleanup)"
    fi
}

case "${1:-}" in
    start)  start_tunnel ;;
    stop)   stop_tunnel ;;
    status) status_tunnel ;;
    *)      echo "Usage: $0 {start|stop|status}" >&2; exit 1 ;;
esac
