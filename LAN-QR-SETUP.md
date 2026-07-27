# Same-network QR payment setup

SwineCore now generates customer-payment QR URLs using an automatically detected
LAN IPv4 address instead of `localhost`. The computer and scanning phone must use
the same Wi-Fi/LAN.

## Run

```powershell
java -jar target/swinecore-1.0.0.jar
```

Allow inbound TCP port `8080` in Windows Firewall when prompted.

## Manual IP override

If the computer has VPNs or multiple network adapters, set the correct address
before starting the application:

```powershell
$env:SWINECORE_PAYMENT_BASE_URL='http://192.168.1.20:8080'
java -jar target/swinecore-1.0.0.jar
```

Find the computer's Wi-Fi IPv4 address with `ipconfig`. Do not use `localhost` in
the override because `localhost` on the scanning phone means the phone itself.

The scanning device must authenticate as the customer before confirming payment;
the existing order-ownership and signed-QR protections remain enabled.
