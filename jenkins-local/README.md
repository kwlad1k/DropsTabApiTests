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

## Sharing the Telegram links

The Build / Allure links in Telegram alerts point to `JENKINS_URL` from
`.env`. By default that's `http://localhost:8080` — works only on this
machine. To make the links clickable for the whole group:

- Expose Jenkins via [ngrok](https://ngrok.com): `ngrok http 8080`,
  copy the HTTPS URL into `JENKINS_URL` in `.env`, then `docker compose up -d`.
- Or [Cloudflare Tunnel](https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/),
  Tailscale Funnel, etc.

`localhost` links sent to the group will render but only resolve on your
own laptop — outsiders see "site can't be reached".

## Troubleshooting

- **`Set DROPSTAB_USER_TOKEN in .env`** on `up` — `.env` is missing or a
  required key is empty. Compose refuses to start without secrets.
- **Job runs but tests fail with 401** — token in `.env` expired (e.g. server
  rotated it). Refresh per the section above.
- **Allure tab is missing** — plugin failed to install. Rebuild:
  `docker compose build --no-cache && docker compose up -d`.
- **Port 8080 already in use** — change the host port in `docker-compose.yml`
  (`"8081:8080"` etc.) and update `JENKINS_URL` in `.env`.
