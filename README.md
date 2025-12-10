# Transport Management System (TMS) – Backend Assignment  
**Author:** Somasekhar Kamma  
**Tech Stack:** Spring Boot 3.2+, Java 17, PostgreSQL, Spring Data JPA

---

## 🚀 Overview  
This project implements the full backend for a **Transport Management System (TMS)** as required by CargoPro.ai.  
The system includes:

- Load management  
- Transporter & truck capacity management  
- Bidding workflow  
- Best-bid ranking  
- Truck allocation and booking  
- Concurrency-safe operations using optimistic locking  

All business rules and API requirements from the assignment have been fully implemented.

---

## 🧱 Architecture  
controller → dto → entity → enums → exception → repository → service

## Database schema diagram (ER diagram)
![diagram](https://raw.githubusercontent.com/sekharchowdary774/tms-cargopro.ai/main/tms.drawio.png)
****

## 🛠️ Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/sekharchowdary774/tms-cargopro.ai.git
cd tms-backend

2️⃣ Configure PostgreSQL

Create database:
      create database tms;
Update application.properties:
     spring.datasource.url=jdbc:postgresql://localhost:5432/tms
     spring.datasource.username=postgres
     spring.datasource.password=yourpassword

     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
    spring.jpa.properties.hibernate.format_sql=true
3️⃣ Run Application
      mvn spring-boot:run

Core Entities:

Load (with @Version for concurrency)

Transporter + AvailableTrucks

Bid (PENDING / ACCEPTED / REJECTED)

Booking (CONFIRMED / CANCELLED)

Load APIs (5)

1.POST /load → Create load (status = POSTED)
2.GET /load?shipperId=&status=&page=0&size=10 → List with pagination
3.GET /load/{loadId} → Get load with active bids
4.PATCH /load/{loadId}/cancel → Cancel load (validate status)
5.GET /load/{loadId}/best-bids → Get sorted bid suggestions

Transporter APIs (3)

1.POST /transporter → Register transporter with truck capacity
2.GET /transporter/{transporterId} → Get details
3.PUT /transporter/{transporterId}/trucks → Update available trucks

Bid APIs (4)

1.POST /bid → Submit bid (validate capacity & load status)
2.GET /bid?loadId=&transporterId=&status= → Filter bids
3.GET /bid/{bidId} → Get bid details
4.PATCH /bid/{bidId}/reject → Reject bid

Booking APIs (3)

1.POST /booking → Accept bid & create booking (deduct trucks, handle concurrency)
2.GET /booking/{bookingId} → Get booking details
3.PATCH /booking/{bookingId}/cancel → Cancel booking (restore trucks, update load status)

Business Rules Implemented
✔ Rule 1 — Capacity Validation

Bids allowed only if trucksOffered ≤ availableTrucks
On booking: trucks deducted
On cancel: trucks restored

✔ Rule 2 — Load Status Transitions

    POSTED → OPEN_FOR_BIDS → BOOKED
                        ↘ CANCELLED
   Restrictions:

      Cannot bid on CANCELLED or BOOKED loads
      Cannot cancel a BOOKED load
✔ Rule 3 — Multi-Truck Allocation

Partial bookings allowed
Load becomes BOOKED only when all trucks are allocated

✔ Rule 4 — Concurrency Protection

Load entity includes @Version
Simultaneous bookings → one succeeds, one fails

✔ Rule 5 — Best-Bid Calculation
   score = (1 / proposedRate) * 0.7 + (rating / 5) * 0.3
   Sorted in descending order.
Postman Collection

All APIs are fully tested using Postman.

Import file:

## 🧪 Postman Collection  
Download here: **[tms.postman_collection.json](./tms.postman_collection.json


  


    
