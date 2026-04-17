# GeoIP Database
Place the MaxMind **GeoLite2-Country.mmdb** file in this directory.
## How to obtain it
1. Create a free MaxMind account: https://www.maxmind.com/en/geolite2/signup
2. Download the **GeoLite2 Country** database (MaxMind DB binary, `.mmdb` format).
3. Extract and copy `GeoLite2-Country.mmdb` into this folder:
   `src/main/resources/geoip/GeoLite2-Country.mmdb`
4. Rebuild the application (`./mvnw clean package`).
## Behaviour without the database
If the file is missing, the application still works — every click will be
recorded with country = `"Unknown"` and a WARN log is emitted on startup.
## Automated refresh (optional)
MaxMind recommends updating the database weekly. You can automate this with
their `geoipupdate` tool: https://github.com/maxmind/geoipupdate
