# Local Jenkins (Docker + JCasC)

Self-contained Jenkins instance for running the DropsTab API test suite
locally. Configuration is fully declarative — no clicking through the UI:

- **Dockerfile** — Jenkins LTS (JDK 17) with Gradle 8 baked in
- **plugins.txt** — plugins installed at image build time
- **casc.yaml** — admin user, credentials, and the seed pipeline job
- **docker-compose.yml** — wires the image to a persistent volume + env

## One-time setup

```bash
cd jenkins-local
cp .env.example .env
$EDITOR .env                       # paste real DropsTab + Telegram values
docker compose up -d --build       # ~5 min on first build
```

Open <http://localhost:8080>, log in (default `admin` / `admin` from `.env`).
The job `DropsTabApiTests` is already there.

## Running tests

1. UI → `DropsTabApiTests` → **Build with Parameters**
2. Pick a `TEST_SUITE` (`test` runs everything; the rest filter by `@Tag`)
3. **Build**
4. After the run: open the build → **Allure Report**

If `TELEGRAM_BOT_TOKEN` / `TELEGRAM_CHAT_ID` are set in `.env`, you also
get a Telegram message with the build status and links.

## Refreshing the bearer token

`successfulChangePasswordTest` rotates `user.token` server-side. Get a fresh
one and update the credential without restarting Jenkins:

```bash
TOKEN=$(curl -sS -X POST 'https://api.icodrops.com/portfolio/login' \
  -H 'Content-Type: application/json' \
  -d '{"email":"YOUR_EMAIL","password":"YOUR_PASSWORD"}' | jq -r .accessToken)

# Update in .env, then re-apply JCasC config (no restart needed):
sed -i '' "s|^DROPSTAB_USER_TOKEN=.*|DROPSTAB_USER_TOKEN=$TOKEN|" .env
docker compose up -d                 # picks up new env, JCasC re-reads creds
```

## Reset / wipe

```bash
docker compose down -v               # removes the dropstab_jenkins_home volume
```

## Sharing the Telegram links (clickable for the whole group)

`http://localhost:8080` only resolves on your own machine. To make Build /
Allure / Console links clickable for everyone in the Telegram group, expose
Jenkins through a tunnel.

### Quick path — Cloudflare quick tunnel (no account, ~30 sec)

```bash
brew install cloudflared      # one-time
cd jenkins-local
./tunnel.sh start             # starts cloudflared, updates JENKINS_URL, restarts Jenkins
./tunnel.sh status            # shows current URL + PID
./tunnel.sh stop              # kills tunnel, reverts JENKINS_URL to localhost
```

After `start`, every Telegram alert from then on will use the public
`*.trycloudflare.com` URL. The tunnel runs as a background process attached
to your shell — closing the terminal keeps it running, but a reboot kills
it (run `./tunnel.sh start` again afterwards).

**Caveat:** quick tunnels get a fresh random URL on every `./tunnel.sh start`.

### Stable path — named Cloudflare tunnel

If you want a permanent URL like `jenkins.example.com`:
1. Add a domain to a free Cloudflare account
2. `cloudflared tunnel login` (browser auth)
3. `cloudflared tunnel create dropstab-jenkins`
4. `cloudflared tunnel route dns dropstab-jenkins jenkins.example.com`
5. Replace `tunnel --url ...` in `tunnel.sh` with `tunnel run dropstab-jenkins`
6. Optionally set up as a launchd service for auto-start at boot

Alternatives: ngrok, Tailscale Funnel, ssh -R reverse-tunnel to a VPS.

## Troubleshooting

- **`Set DROPSTAB_USER_TOKEN in .env`** on `up` — `.env` is missing or a
  required key is empty. Compose refuses to start without secrets.
- **Job runs but tests fail with 401** — token in `.env` expired (e.g. server
  rotated it). Refresh per the section above.
- **Allure tab is missing** — plugin failed to install. Rebuild:
  `docker compose build --no-cache && docker compose up -d`.
- **Port 8080 already in use** — change the host port in `docker-compose.yml`
  (`"8081:8080"` etc.) and update `JENKINS_URL` in `.env`.
