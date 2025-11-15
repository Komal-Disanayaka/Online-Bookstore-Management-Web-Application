 # BookNest Online BookStore

> A Spring Boot + Thymeleaf sample online bookstore application — BookNest Online BookStore is a small bookstore management system used for demo, learning and quick prototypes.

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Setup & Configuration](#setup--configuration)
- [Build & Run](#build--run)
- [Important Files & Folders](#important-files--folders)
- [Key Endpoints](#key-endpoints)
- [Admin / Default User](#admin--default-user)
- [Development Tips](#development-tips)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

BookNest Online BookStore is a Spring Boot web application using Thymeleaf templates for server-side rendering. It demonstrates a simple online bookstore with user registration/login, browsing books, a shopping cart, checkout flow, order management, feedback and admin management screens.

## Features

- Public-facing home / dashboard with book listings
- User registration & login
- Add-to-cart, cart management, checkout
- Order storage and order history for users
- Submit feedback and inquiries
- Administrative pages for managing orders, inquiries and feedback
- File-based book cover uploads (stored in `uploads/books`)
- Thymeleaf templates and basic responsive UI

## Tech Stack

- Java 21
- Spring Boot 3.2.x
- Spring MVC (Web)
- Spring Data JPA
- Spring Security
- Thymeleaf (templating)
- MySQL (runtime dependency in `pom.xml`) — configurable via `application.properties`
- Maven build

## Prerequisites

- Java 21 (JDK)
- Maven 3.6+
- MySQL (or another compatible JDBC database) for production usage. You can run with an embedded DB for quick tests if you configure it accordingly.

## Setup & Configuration

1. Clone the repository:

```powershell
git clone <repo-url> "Online_BookStore_System"
cd "Online_BookStore_System"
```

2. Create an `uploads` directory used for book images (if not present):

```powershell
mkdir uploads\books
```

3. Configure database properties in `src/main/resources/application.properties` (example MySQL config):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booknest_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
server.port=8080
```

For quick testing you can switch to an in-memory DB (H2) by adding the dependency and changing the datasource URL.

4. (Optional) Update other properties such as shipping threshold or email settings in `application.properties`.

## Build & Run

Build the project with Maven:

```powershell
mvn clean package
```

Run using Spring Boot Maven plugin (development):

```powershell
mvn spring-boot:run
```

Or run the packaged jar (after `mvn package`):

```powershell
java -jar target\online-bookstore-system-1.0.0.jar
```

By default the app runs on `http://localhost:8080`.

## Important Files & Folders

- `pom.xml` — Maven build file and dependencies (Java 21, Spring Boot 3.2.x)
- `src/main/java/com/bookstore` — Java source (controllers, models, services, repositories, config)
  - `controller/` — web controllers (e.g., `BookController`, `CartController`, `UserController`, `OrderController`, `FeedbackController`, `OrderInquiryController`)
  - `config/` — security and data initializer (`DataInitializer` creates a default admin user)
- `src/main/resources/templates/` — Thymeleaf HTML templates (UI pages)
- `src/main/resources/application.properties` — runtime configuration
- `uploads/books/` — location for book uploads (images)

## Key Endpoints (routes)

Public / User-facing (examples):

- GET `/` or `/dashboard` — Public book dashboard
- GET `/book/{id}` — View book details (if implemented)
- GET `/cart` — View shopping cart
- POST `/cart/add` — Add item to cart (form action)
- GET `/checkout` — Checkout page
- GET `/orders` — User orders list
- GET `/profile` — User profile
- GET `/login` — Login page
- GET `/register` — Registration page

Admin (prefix `/admin`):

- GET `/admin/dashboard` — Admin dashboard
- GET `/admin/orders` — Manage orders
- GET `/admin/inquiries` — Manage inquiries
- GET `/admin/feedbacks` — Manage feedback

Controllers to reference: `BookController`, `CartController`, `DashboardController`, `FeedbackController`, `OrderController`, `OrderInquiryController`, `UserController`.

## Admin / Default User

On application startup, a default admin user is created by `DataInitializer` if it doesn't exist:

- Username: `admin`
- Password: `admin123`

Change these credentials in `src/main/java/com/bookstore/config/DataInitializer.java` or create a new admin user via the UI or repository.

## Development Tips

- Use your IDE (IntelliJ IDEA, Eclipse) to run `OnlineBookStoreSystemApplication` directly for faster iteration.
- If you change Thymeleaf templates, refresh the browser. For hot-reload during development, use Spring Boot DevTools (already included in `pom.xml`).
- Keep the `uploads` folder writable by the running process to allow book image uploads.

## Troubleshooting

- Database connection errors: verify `spring.datasource.*` values and that the database is reachable.
- Port conflicts: change `server.port` in `application.properties`.
- Missing images: confirm images were uploaded into `uploads/books` and the path in templates is correct.

## Contributing

If you want to contribute:

1. Fork the repo
2. Create a feature branch
3. Submit a pull request with a clear description of changes

## License

This project is licensed under the MIT License.


