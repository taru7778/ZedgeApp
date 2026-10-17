# Tarek build - config summary

| Account | Firebase project | Realtime DB URL |
|---|---|---|
| ZEDGE1 | zedge-1 | https://zedge-1-default-rtdb.firebaseio.com |
| ZEDGE2 | zedge2-34d95 | https://zedge2-34d95-default-rtdb.firebaseio.com |
| ZEDGE3 | zedge3-1b3dc | https://zedge3-1b3dc-default-rtdb.firebaseio.com |

R2 worker: https://tarek.henrydelacruz0t7.workers.dev

## Must check
1. Realtime Database **region**. The URLs above assume the default `us-central1` (`...firebaseio.com`).
   Open Firebase console -> Realtime Database of each project and compare the URL shown at the top.
   If it is `https://<project>-default-rtdb.asia-southeast1.firebasedatabase.app` (or another region),
   replace the URL in: `index.html` (configs.zedgeN.databaseURL), `zedgeN.yml` (3 places), `generator.yml` (2 places),
   `app/.../core/Core.kt`. `python3 check_config.py` lists every place.
2. Realtime Database must be **created** in each project (Build -> Realtime Database -> Create) with rules allowing read/write.
3. R2 worker: add the R2 bucket binding `MEDIA_BUCKET` in Worker -> Settings -> Bindings, then Deploy.
4. This build is a **3-account build** (ZEDGE1-3). `zedge4.yml` and every ZEDGE4 reference were removed from panel, workflows and app.
5. Telegram is pre-configured (bot token + chat id 8998798162 baked into zedge1-3.yml, greeting "হ্যালো তারেক ভাই"). Repository secrets `TELEGRAM_BOT_TOKEN` / `TELEGRAM_CHAT_ID` override it if you ever want to change the bot. Add the other GitHub secrets as described in SETUP.md. **Important:** Tarek must open the bot in Telegram and press Start once, otherwise the bot cannot message him.
