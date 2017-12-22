package model

import java.sql.Timestamp

case class Location(id: Long,
//                    pageId:Long,
                    city: String,
                    country: String,
                    latitude: String,
                    longitude:String,
                    street:String,
                    zip:String,
                    createdAt: Timestamp)
//{
//  "name": "Coworking \"Chasopys\"",
//  "phone": "(095)2068621",
//  "location": {
//  "city": "Kyiv",
//  "country": "Ukraine",
//  "latitude": 50.439681515743,
//  "longitude": 30.515079930406,
//  "street": "Tolstoho Lva 3",
//  "zip": "01004"
//},
//  "hours": {
//  "mon_1_open": "08:00",
//  "mon_1_close": "20:00",
//  "tue_1_open": "08:00",
//  "tue_1_close": "20:00",
//  "wed_1_open": "08:00",
//  "wed_1_close": "20:00",
//  "thu_1_open": "08:00",
//  "thu_1_close": "20:00",
//  "fri_1_open": "08:00",
//  "fri_1_close": "20:00",
//  "sat_1_open": "08:00",
//  "sat_1_close": "20:00",
//  "sun_1_open": "08:00",
//  "sun_1_close": "20:00"
//},
//  "price_range": "$$",
//  "id": "631243810243875"
//}