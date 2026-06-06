```powershell
$env:JWT_SECRET="YOUR_JWT_SECRET_HERE"; `
$env:MAIL_USERNAME="YOUR_GMAIL_ADDRESS"; `
$env:MAIL_PASSWORD="YOUR_GMAIL_APP_PASSWORD"; `
mvn spring-boot:run
```
> create secret
```powershell
python -c "import secrets, string; print(''.join(secrets.choice(string.ascii_letters + string.digits) for _ in range(128)))"
```