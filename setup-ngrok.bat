@echo off
echo ngrok setup uchun qo'llanma
echo.
echo 1. ngrok.com/download dan yuklab oling
echo 2. ngrok account yarating: https://dashboard.ngrok.com/signup
echo 3. Authtoken oling: https://dashboard.ngrok.com/get-started/your-authtoken
echo 4. Token'ni o'rnating: ngrok config add-authtoken YOUR_TOKEN
echo 5. Backend'ni ishga tushiring: mvnw spring-boot:run
echo 6. Yangi terminal'da ngrok'ni ishga tushiring: ngrok http 8080
echo.
echo ngrok ishga tushgandan keyin sizga URL beradi (masalan: https://abc123.ngrok-free.app)
echo.
echo O'sha URL'ni:
echo - Google Console'da redirect URI sifatida qo'shing
echo - application.properties'da app.frontend.url'ga yozing (frontend uchun)
echo.
pause
