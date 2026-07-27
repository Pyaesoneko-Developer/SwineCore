# Start SwineCore

Open PowerShell in this folder. Confirm that this folder contains `pom.xml`,
`src`, and `target`, then run:

```powershell
mvn spring-boot:run
```

Alternatively, run the packaged application:

```powershell
java -jar target/swinecore-1.0.0.jar
```

The application requires MySQL using the connection settings in
`src/main/resources/application.properties`.

For same-network QR payments, keep the computer and phone on the same Wi-Fi.
If automatic IP detection selects the wrong adapter, run:

```powershell
$env:SWINECORE_PAYMENT_BASE_URL='http://YOUR-PC-IP:8080'
mvn spring-boot:run
```
# Database startup

SwineCore defaults to MySQL at `localhost:3306/swinecore` with username `root` and password `root`.
You can override these without editing the project:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/swinecore?createDatabaseIfNotExist=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
mvn spring-boot:run
```
