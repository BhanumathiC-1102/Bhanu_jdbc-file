# Hospital Appointment Booking

A web application for a hospital front desk to register doctors and patients, book appointments, and cancel or reschedule them. Built with Java servlets, JDBC, MySQL and a single-page HTML/CSS/JavaScript frontend, organised in the MVC pattern.

## Architecture (MVC)

| Layer | Package / file | Job |
|-------|----------------|-----|
| Model | `com.hospital.model` | `Doctor`, `Patient`, `Appointment` data classes |
| Data access | `com.hospital.dao` | All JDBC code: `DoctorDAO`, `PatientDAO`, `AppointmentDAO` |
| Controller | `com.hospital.controller` | Servlets that read the request, call a DAO, and return JSON |
| View | `src/main/webapp/index.html` | The screens: sign-in, booking desk, appointment list, doctors, patients |
| Utilities | `com.hospital.util` | `DBConnection` (JDBC settings), `DataAccessException` |

Request flow: `index.html` calls `fetch()` on `/api/...` -> a servlet validates the input -> a DAO runs a `PreparedStatement` on MySQL -> the servlet sends JSON back -> `index.html` redraws the screen.

## Features

- Front-desk sign-in (session based)
- Add, list and remove doctors; search doctors by specialization
- Add, list and remove patients
- Booking desk: pick a patient, doctor and date, then choose from a grid of 30-minute slots (09:00 to 16:30). Taken and past slots are crossed out
- Double-booking is blocked in the database by a UNIQUE key, not just in the screen
- Cancel an appointment (the slot opens up again) or reschedule it
- Search and filter the appointment list

## Requirements

- JDK 11 or newer
- Maven 3.6 or newer
- MySQL 8

## Run it

1. Create the database and sample data:
   ```
   mysql -u root -p < database/schema.sql
   ```
2. Put your MySQL password in `src/main/resources/db.properties`.
3. Start the app with the embedded Jetty server:
   ```
   mvn jetty:run
   ```
4. Open http://localhost:8080 and sign in with `admin` / `admin123`.

To deploy on Tomcat instead, run `mvn package` and copy `target/hospital.war` into Tomcat's `webapps` folder (Tomcat 9 or older, because the project uses the `javax.servlet` API).

## REST endpoints

| Method | URL | Purpose |
|--------|-----|---------|
| GET / POST / DELETE | `/api/login` | Check session / sign in / sign out |
| GET / POST / DELETE | `/api/doctors` | List (`?spec=`), add, remove (`?id=`) |
| GET / POST / DELETE | `/api/patients` | List, add, remove (`?id=`) |
| GET / POST / PUT / DELETE | `/api/appointments` | List, book, reschedule (`?id=&date=&time=`), cancel (`?id=`) |

## Notes

- The sign-in uses fixed demo credentials in `LoginServlet`. A real system would store hashed passwords in a users table.
- Change `db.properties` before publishing if you edited it with your real password. Do not commit real credentials.
