# Utility Billing System - end-to-end API test.
# Usage:  powershell -ExecutionPolicy Bypass -File test-api.ps1 [baseUrl]
param([string]$Base = "http://localhost:8080")

$ErrorActionPreference = "Stop"
$pass = 0; $fail = 0
$run = (Get-Date).Ticks.ToString().Substring(10)   # unique-ish suffix per run

function Req($method, $path, $token, $body) {
    $headers = @{}
    if ($token) { $headers["Authorization"] = "Bearer $token" }
    $p = @{ Method = $method; Uri = "$Base$path"; Headers = $headers; ContentType = "application/json" }
    if ($null -ne $body) { $p["Body"] = ($body | ConvertTo-Json -Depth 8) }
    try {
        $r = Invoke-RestMethod @p
        return @{ ok = $true; status = 200; data = $r }
    } catch {
        $code = if ($_.Exception.Response) { [int]$_.Exception.Response.StatusCode } else { -1 }
        return @{ ok = $false; status = $code; err = $_.ErrorDetails.Message }
    }
}
function Check($name, $cond) {
    if ($cond) { $script:pass++; Write-Host ("  PASS  " + $name) -ForegroundColor Green }
    else       { $script:fail++; Write-Host ("  FAIL  " + $name) -ForegroundColor Red }
}

Write-Host "`n=== AUTH ===" -ForegroundColor Cyan
$login = Req POST "/api/auth/login" $null @{ email = "admin@utility.rw"; password = "Admin@123" }
Check "admin password login returns token" ($login.ok -and $login.data.token)
$admin = $login.data.token

$bad = Req POST "/api/auth/login" $null @{ email = "admin@utility.rw"; password = "wrong" }
Check "wrong password -> 401" ($bad.status -eq 401)

$noauth = Req GET "/api/customers" $null $null
Check "no token -> 401" ($noauth.status -eq 401)

Write-Host "`n=== TARIFFS (ADMIN) ===" -ForegroundColor Cyan
$tw = Req POST "/api/tariffs" $admin @{ meterType="WATER"; consumptionRate=500; fixedServiceCharge=1000; vatRate=18; penaltyRate=5; effectiveFrom="2026-01-01" }
Check "create WATER tariff" ($tw.ok -and $tw.data.id)
$te = Req POST "/api/tariffs" $admin @{ meterType="ELECTRICITY"; consumptionRate=200; fixedServiceCharge=800; vatRate=18; penaltyRate=5; effectiveFrom="2026-01-01" }
Check "create ELECTRICITY tariff" ($te.ok)
$tl = Req GET "/api/tariffs" $admin $null
Check "list tariffs" ($tl.ok -and $tl.data.Count -ge 2)

Write-Host "`n=== CUSTOMERS ===" -ForegroundColor Cyan
$natId = "NID-$run"
$cust = Req POST "/api/customers" $admin @{ fullNames="Alice Uwase"; nationalId=$natId; email="rutagandasalim@gmail.com"; phoneNumber="+250788111222"; address="Kigali" }
Check "create customer" ($cust.ok -and $cust.data.id)
$custId = $cust.data.id
$dup = Req POST "/api/customers" $admin @{ fullNames="Dup"; nationalId=$natId; email="x@y.z" }
Check "duplicate nationalId -> 409" ($dup.status -eq 409)
$cl = Req GET "/api/customers" $admin $null
Check "list customers" ($cl.ok)

Write-Host "`n=== METERS ===" -ForegroundColor Cyan
$meterNo = "MTR-$run"
$meter = Req POST "/api/meters" $admin @{ meterNumber=$meterNo; meterType="WATER"; installationDate="2026-01-15"; customerId=$custId }
Check "create meter" ($meter.ok -and $meter.data.id)
$meterId = $meter.data.id
$dupm = Req POST "/api/meters" $admin @{ meterNumber=$meterNo; meterType="WATER"; customerId=$custId }
Check "duplicate meterNumber -> 409" ($dupm.status -eq 409)

Write-Host "`n=== READINGS (rules) ===" -ForegroundColor Cyan
$rd = Req POST "/api/readings" $admin @{ meterId=$meterId; previousReading=0; currentReading=120; readingDate="2026-03-31" }
Check "capture reading (current>previous)" ($rd.ok -and $rd.data.id)
$readingId = $rd.data.id
Check "consumption computed = 120" ($rd.data.consumption -eq 120)
$rbad = Req POST "/api/readings" $admin @{ meterId=$meterId; previousReading=120; currentReading=100; readingDate="2026-04-30" }
Check "current<=previous -> 400" ($rbad.status -eq 400)
$rdupmonth = Req POST "/api/readings" $admin @{ meterId=$meterId; currentReading=200; readingDate="2026-03-15" }
Check "second reading same month -> 400" ($rdupmonth.status -eq 400)

Write-Host "`n=== BILLS + trigger notification ===" -ForegroundColor Cyan
$bill = Req POST "/api/bills/generate" $admin @{ meterReadingId=$readingId }
Check "generate bill" ($bill.ok -and $bill.data.billReference)
$billRef = $bill.data.billReference
$billId = $bill.data.id
# energy = 120*500=60000; service=1000; tax=(61000*18%)=10980; total=71980
Check "bill total computed (71980)" ($bill.data.totalAmount -eq 71980)
Check "bill outstanding = total" ($bill.data.outstandingBalance -eq 71980)
$dupbill = Req POST "/api/bills/generate" $admin @{ meterReadingId=$readingId }
Check "duplicate bill for reading -> 400" ($dupbill.status -eq 400)
$notif = Req GET "/api/notifications" $admin $null
Check "BILL_GENERATED notification created by trigger" (@($notif.data | Where-Object { $_.type -eq "BILL_GENERATED" -and $_.billId -eq $billId }).Count -ge 1)

Write-Host "`n=== APPROVE + PAYMENTS ===" -ForegroundColor Cyan
$appr = Req PATCH "/api/bills/$billId/approve" $admin $null
Check "approve bill -> APPROVED" ($appr.ok -and $appr.data.status -eq "APPROVED")
$p1 = Req POST "/api/payments" $admin @{ billReference=$billRef; amountPaid=30000; paymentMethod="MOBILE_MONEY"; reference="MoMo-1" }
Check "partial payment -> PARTIALLY_PAID" ($p1.ok -and $p1.data.billStatus -eq "PARTIALLY_PAID")
Check "outstanding after partial = 41980" ($p1.data.billOutstandingBalance -eq 41980)
$over = Req POST "/api/payments" $admin @{ billReference=$billRef; amountPaid=999999; paymentMethod="CASH" }
Check "overpayment -> 400" ($over.status -eq 400)
$p2 = Req POST "/api/payments" $admin @{ billReference=$billRef; amountPaid=41980; paymentMethod="CASH"; reference="Cash-1" }
Check "full payment -> PAID" ($p2.ok -and $p2.data.billStatus -eq "PAID")
Check "outstanding after full = 0" ($p2.data.billOutstandingBalance -eq 0)
$notif2 = Req GET "/api/notifications" $admin $null
Check "PAYMENT_COMPLETED notification created by trigger" (@($notif2.data | Where-Object { $_.type -eq "PAYMENT_COMPLETED" -and $_.billId -eq $billId }).Count -ge 1)

Write-Host "`n=== INACTIVE CUSTOMER cannot be billed ===" -ForegroundColor Cyan
$c2 = Req POST "/api/customers" $admin @{ fullNames="Bob Inactive"; nationalId="NID2-$run"; email="bob@example.com" }
$c2id = $c2.data.id
$null = Req PATCH "/api/customers/$c2id/status?status=INACTIVE" $admin $null
$m2 = Req POST "/api/meters" $admin @{ meterNumber="MTR2-$run"; meterType="ELECTRICITY"; customerId=$c2id }
$r2 = Req POST "/api/readings" $admin @{ meterId=$m2.data.id; previousReading=0; currentReading=50; readingDate="2026-03-20" }
$b2 = Req POST "/api/bills/generate" $admin @{ meterReadingId=$r2.data.id }
Check "bill for inactive customer -> 400" ($b2.status -eq 400)

Write-Host "`n=== ROLE GATES + CUSTOMER self-service ===" -ForegroundColor Cyan
if (-not $env:PGPASSWORD) { $env:PGPASSWORD = "postgres" }   # set PGPASSWORD before running
$psql = "C:\Program Files\PostgreSQL\18\bin\psql.exe"
function OtpCode($mail, $purpose) {
    (& $psql -U postgres -h localhost -d utility_billing -tA -c "SELECT code FROM otp_tokens WHERE email='$mail' AND purpose='$purpose' AND used=false ORDER BY id DESC LIMIT 1").Trim()
}
$custEmail = "cust$run@example.com"
$su = Req POST "/api/auth/signup" $null @{ fullNames="Carol Customer"; email=$custEmail; password="Pass@123"; roles=@("ROLE_CUSTOMER") }
Check "signup customer user" ($su.ok)
Check "login blocked before verification -> 400" ((Req POST "/api/auth/login" $null @{ email=$custEmail; password="Pass@123" }).status -eq 400)
$vcode = OtpCode $custEmail "ACCOUNT_VERIFICATION"
$ver = Req POST "/api/auth/verify" $null @{ email=$custEmail; code=$vcode }
Check "verify customer email -> 200" ($ver.ok)
$null = Req POST "/api/customers" $admin @{ fullNames="Carol Customer"; nationalId="NID3-$run"; email=$custEmail; phoneNumber="+250788000111" }  # auto-links to user
$clog = Req POST "/api/auth/login" $null @{ email=$custEmail; password="Pass@123" }
Check "customer login after verify" ($clog.ok -and $clog.data.token)
$ctok = $clog.data.token
$me = Req GET "/api/bills/me" $ctok $null
Check "customer GET /bills/me -> 200" ($me.ok)
$pme = Req GET "/api/payments/me" $ctok $null
Check "customer GET /payments/me -> 200" ($pme.ok)
$forbidden = Req GET "/api/users" $ctok $null
Check "customer GET /users -> 403 (role gate)" ($forbidden.status -eq 403)
$opGate = Req POST "/api/tariffs" $ctok @{ meterType="WATER"; consumptionRate=1; fixedServiceCharge=1; vatRate=1; penaltyRate=1; effectiveFrom="2026-01-01" }
Check "customer create tariff -> 403" ($opGate.status -eq 403)

Write-Host "`n=== STAFF LOGINS (email + password) ===" -ForegroundColor Cyan
Check "operator login" ((Req POST "/api/auth/login" $null @{ email="operator@utility.rw"; password="Operator@123" }).ok)
Check "finance login"  ((Req POST "/api/auth/login" $null @{ email="finance@utility.rw";  password="Finance@123"  }).ok)
Check "OTP login endpoint removed -> 404" ((Req POST "/api/auth/otp/request" $null @{ email=$custEmail }).status -eq 404)

Write-Host "`n=== RESEND CODE (expired-before-use) ===" -ForegroundColor Cyan
$re = Req POST "/api/auth/signup" $null @{ fullNames="Resend Test"; email="resend$run@example.com"; password="Pass@123"; roles=@("customer") }
# force-expire the verification code, then prove resend yields a working one
& $psql -U postgres -h localhost -d utility_billing -c "UPDATE otp_tokens SET expires_at = now() - interval '1 minute' WHERE email='resend$run@example.com'" | Out-Null
$expCode = OtpCode "resend$run@example.com" "ACCOUNT_VERIFICATION"
Check "verify with expired code -> 400" ((Req POST "/api/auth/verify" $null @{ email="resend$run@example.com"; code=$expCode }).status -eq 400)
Check "resend new code -> 200" ((Req POST "/api/auth/code/resend" $null @{ email="resend$run@example.com"; purpose="ACCOUNT_VERIFICATION" }).ok)
$freshCode = OtpCode "resend$run@example.com" "ACCOUNT_VERIFICATION"
Check "old expired code now invalidated -> 400" ((Req POST "/api/auth/verify" $null @{ email="resend$run@example.com"; code=$expCode }).status -eq 400)
Check "verify with fresh code -> 200" ((Req POST "/api/auth/verify" $null @{ email="resend$run@example.com"; code=$freshCode }).ok)

Write-Host "`n=== USERS (ADMIN) ===" -ForegroundColor Cyan
$ul = Req GET "/api/users" $admin $null
Check "admin list users" ($ul.ok -and $ul.data.Count -ge 1)

$resultColor = "Red"; if ($fail -eq 0) { $resultColor = "Green" }
Write-Host "`n========================================" -ForegroundColor Yellow
Write-Host ("RESULT: $pass passed, $fail failed") -ForegroundColor $resultColor
Write-Host "========================================" -ForegroundColor Yellow
