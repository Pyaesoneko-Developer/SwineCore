# Factory Feed Report API

The factory does not need a SwineCore user account or API key. It uses the separate `factory-feed-report.html` portal and connects directly to the public factory endpoints.

Configure production with:

```properties
No API-key configuration is required.
```

Endpoints:

- `GET /api/factory/catalog` — farms and buildings
- `GET /api/factory/stock-shortages` — active `STOCK_SHORTAGE_WARNING` inventory alerts
- `POST /api/factory/reports` — submit dispatch quantity and unit price
- `GET /api/factory/reports/status?invoiceNumber=...` — payment/reconciliation state
- `GET /api/factory/rejections?supplierName=...` — manager rejection descriptions

Flow: factory report → supervisor physical count → automatic expense on exact feed/quantity match → manager review on mismatch. A manager confirmation releases the expense; a manager rejection stores the reason in the existing shipment record. When the factory resubmits that invoice, the rejected record is deleted and replaced by the corrected report.

## Building-level feed stock

Managers do not manually add stock. Approved factory deliveries increase only the selected building and feed-type ledger. At 11:30 PM each day, SwineCore calculates consumption independently for every building and feed type:

`daily consumption = matching active pigs × feed rule kg per pig`

That daily amount is deducted from the matching feed stock. The API returns `STOCK_SHORTAGE_WARNING` when a feed type has two days or less remaining, including farm, building, feed type, net quantity, daily consumption, and remaining days.

No database table or column is added. Building-level inventory uses the existing `inventory.feed_type` value as an internal `BuildingId::FeedType` key and presents the separated values in the UI/API.
