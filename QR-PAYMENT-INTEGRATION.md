# Customer QR Payment Flow

1. A customer creates a pig or semen order.
2. SwineCore opens a printable voucher containing a dynamic QR in the top-right corner.
3. The QR contains a customer payment URL with the order reference, type, total amount, farm, item details, and an HMAC signature.
4. The customer scans the QR, signs in if needed, reviews the verified order, and confirms payment.
5. SwineCore checks both the QR signature and order ownership before marking the order paid.
6. The order appears in the customer's account history only after payment succeeds.

Pig and semen vouchers include the supplied authorized signature as a transparent PNG.

## Required configuration

Set these values for the deployed environment:

```properties
app.base-url=https://your-real-domain.example
app.qr-secret=${SWINECORE_QR_SECRET}
```

`SWINECORE_QR_SECRET` should be a long random secret and must be the same on every application instance. The base URL must be reachable by the customer's phone; `localhost` only works for development on the same device.

This flow records an in-system payment confirmation. Connect `processPayment` to a real payment provider callback before using it for real-money settlement.
