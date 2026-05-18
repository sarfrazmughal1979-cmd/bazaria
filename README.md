# Bazaria – Multi‑Vendor Marketplace Platform

**Bazaria** is a full‑stack multi‑vendor e‑commerce marketplace built with **Java / Spring Boot** (modular monolith which can be easily broken into microservices) and **React / TypeScript / Tailwind CSS**. It supports customers, vendors, and administrators with advanced features like CMS‑driven content, fraud detection, loyalty points, real‑time chat, multi‑carrier shipping, and analytics.

---

## 📸 Live Demo

- **Frontend:** [https://master.d3dfvuir5p0eax.amplifyapp.com](https://master.d3dfvuir5p0eax.amplifyapp.com)  
- **Backend API:** [https://d12345.cloudfront.net/api/v1/](https://d12345.cloudfront.net/api/v1/)

---

## ✨ Features

### 👥 Customer
- Browse products with search, category filters, and pagination
- Product detail with image gallery, variant selection, customer reviews
- Shopping cart (guest & logged‑in) with coupon support
- Checkout with multiple shipping carriers, loyalty point redemption, cash‑on‑delivery
- Order tracking and history
- Wishlist, loyalty points, support ticket system with messaging

### 🏪 Vendor
- Register as a vendor (requires admin approval)
- Create / manage products with variants and images
- View vendor‑specific orders and settlements
- Performance analytics dashboard

### 🛡️ Admin
- Dashboard with revenue, orders, customers, products
- Approve / reject vendor applications and product submissions
- Manage categories and CMS content (banners, pages, menus, announcements, FAQs)
- Product moderation queue with bulk approval
- Support ticket management and dispute resolution

### ⚙️ Advanced Modules
- **Fraud Detection** – dynamic rule engine (JSON‑based) with scoring, blocking, and flagging
- **CMS** – homepage sections, banners, pages, menus (fully database‑driven)
- **Loyalty & Points** – earn on purchases, redeem at checkout, tier system
- **Inventory** – stock reservation, confirmation, release, low‑stock alerts
- **Analytics** – daily aggregations, vendor performance, sales trends
- **Notifications** – email, SMS, push, in‑app (template engine with multi‑channel support)
- **Support** – tickets, live chat (WebSocket), disputes
- **Shipping** – multi‑carrier (TCS, Leopard, CallCourier) with rate calculation and tracking
- **Payment** – Stripe, JazzCash, cash on delivery (adapters ready, frontend pending for online)

---

## 🧱 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                     │
│                   Hosted on AWS Amplify                     │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTPS /api/*
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                  CloudFront Distribution                    │
│              (SSL termination, reverse proxy)               │
└────────────────────────┬────────────────────────────────────┘
                         │ HTTP :8080
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   EC2 Instance (Amazon Linux)               │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                 Spring Boot Application              │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐              │   │
│  │  │ IAM      │ │ Catalog  │ │ Order    │   ... 17     │   │
│  │  │ Module   │ │ Module   │ │ Module   │   modules    │   │
│  │  └──────────┘ └──────────┘ └──────────┘              │   │
│  └──────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │
│  │ PostgreSQL   │  │ Redis        │  │ Elasticsearch│       │
│  │ (RDS)        │  │(ElastiCache) │  │ (Cloud)      │       │
│  └──────────────┘  └──────────────┘  └──────────────┘       │  
└─────────────────────────────────────────────────────────────┘
```

The backend is a **modular monolith** – all modules run in the same JVM and share a common database. Inter‑module communication is done via **direct method calls** (when possible) and **internal REST** (for loose coupling). Events are stored in an **outbox** and asynchronously dispatched to listeners using Spring’s `ApplicationEventPublisher`.

---

## 🛠️ Tech Stack

| Layer | Technologies |
|-------|--------------|
| **Backend**          | Java 25, Spring Boot 4, Spring Security, JPA/Hibernate, Flyway, PostgreSQL, Redis, Elasticsearch |
| **Frontend**         | React 18, TypeScript, Vite, Tailwind CSS, Heroicons, react‑router‑dom, react‑hot‑toast |
| **Infrastructure**   | AWS EC2, CloudFront, Amplify, S3, RDS, ElastiCache, SES, SNS |
| **CI/CD**            | GitHub Actions, Gradle, self‑hosted runner |
| **Testing**          | JUnit 5, Spring Boot Test |

---

## 🚀 Getting Started (Local Development)

### Prerequisites

- Java 25 (or 21+)
- Gradle 9.1+ (wrapper included)
- PostgreSQL 16+ (or Docker)
- Redis 7+ (or Docker)
- Node.js 18+ and npm

### 1. Clone the repository

```bash
git clone https://github.com/your-username/bazaria.git
cd bazaria
```

### 2. Backend

#### a) Start databases with Docker (optional but recommended)

```bash
docker-compose -f docker-compose.yml up -d
```

This starts PostgreSQL, Redis, and MailHog (for email testing).

#### b) Configure environment variables

Copy `.env.example` to `.env` and adjust the values:

```bash
cp .env.example .env
```

At minimum, set `DB_HOST`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.

#### c) Run migrations

The application uses **Flyway**; migrations run automatically on startup.  
If you need to run them manually:

```bash
gradle flywayMigrate -i
```

#### d) Build and run

```bash
gradle :platform-app:bootJar
java -jar platform-app/build/libs/platform-app-*.jar
```

The backend will be available at `http://localhost:8080`.

### 3. Frontend

```bash
cd frontend   # or wherever the React app is located
npm install
npm run dev
```

The frontend runs on `http://localhost:3000` and proxies API calls to `http://localhost:8080`.

---

## 🌐 Environment Variables

| Variable            | Description         | Default |
|---------------------|---------------------|---------|
| `DB_HOST`           | PostgreSQL host     | `localhost` |
| `DB_PORT`           | PostgreSQL port     | `5432` |
| `DB_NAME`           | Database name       | `ecommerce` |
| `DB_USERNAME`       | Database user       | `postgres` |
| `DB_PASSWORD`       | Database password   | `postgres` |
| `REDIS_HOST`        | Redis host          | `localhost` |
| `REDIS_PORT`        | Redis port          | `6379` |
| `JWT_SECRET`        | JWT signing secret  | (must be set) |
| `AWS_ACCESS_KEY`    | AWS access key (for S3/SES) | (optional) |
| `AWS_SECRET_KEY`    | AWS secret key      | (optional) |
| `STRIPE_SECRET_KEY` | Stripe secret key   | (optional) |
| `INTERNAL_API_KEY`  | API key for internal endpoints | `Mughal12` |
| `VITE_API_BASE_URL` | Frontend API base URL (only needed if not using Amplify proxy) | `/api/v1` |

---

## 📚 API Summary

All endpoints are prefixed with `/api/v1`. Public endpoints require no authentication; others need a JWT `Bearer` token.

### Authentication

| Method | Path             | Description               | Auth |
|--------|------------------|---------------------------|------|
| POST   | `/auth/register` | Register new customer     | No   |
| POST   | `/auth/login`    | Login, returns JWT tokens | No   |
| POST   | `/auth/refresh`  | Refresh access token      | No   |
| GET    | `/users/me`      | Get current user profile  | Yes  |

### Products & Categories

| Method | Path                       | Description                                | Auth        |
|--------|----------------------------|--------------------------------------------|-------------|
| GET    | `/products/search`         | Search products (keyword, category, price) | No          |
| GET    | `/products/featured`       | Get featured products                      | No          |
| GET    | `/products/categories/all` | List all active categories                 | No          |
| GET    | `/products/{slug}`         | Product detail by slug                     | No          |
| POST   | `/products`                | Create product (vendor only)               | Yes (VENDOR)|
| PUT    | `/products/{id}`           | Update product (vendor)                    | Yes (VENDOR)|
| DELETE | `/products/{id}`           | Delete product                             | Yes (ADMIN) |

### Cart

| Method | Path                   | Description          | Auth   |
|--------|------------------------|----------------------|--------|
| GET    | `/cart`                | Get current cart     | No/Yes |
| POST   | `/cart/items`          | Add item to cart     | No/Yes |
| PUT    | `/cart/items/{itemId}` | Update item quantity | No/Yes |
| DELETE | `/cart/items/{itemId}` | Remove item          | No/Yes |
| POST   | `/cart/coupon`         | Apply coupon         | No/Yes |
| DELETE | `/cart/coupon`         | Remove coupon        | No/Yes |

### Orders

| Method | Path                   | Description                      | Auth     |
|--------|------------------------|--------------------------------- |----------|
| POST   | `/orders/placeOrder`   | Place order                      | Yes      |
| GET    | `/orders`              | List my orders                   | Yes      |
| GET    | `/orders/{id}`         | Order detail                     | Yes      |
| POST   | `/orders/{id}/cancel`  | Cancel order                     | Yes      |
| POST   | `/orders/{id}/confirm` | Confirm order (payment received) | Internal |

### Vendor

| Method | Path                  | Description        | Auth         |
|--------|-----------------------|--------------------|--------------|
| POST   | `/vendors/register`   | Apply as vendor    | Yes          |
| GET    | `/vendor/products`    | My products        | Yes (VENDOR) |
| GET    | `/vendor/orders`      | Vendor orders      | Yes (VENDOR) |
| GET    | `/vendor/settlements` | Settlement history | Yes (VENDOR) |

### Admin

| Method | Path                            | Description                 | Auth        |
|--------|---------------------------------|-----------------------------|-------------|
| GET    | `/admin/analytics/dashboard`    | Dashboard metrics           | Yes (ADMIN) |
| GET    | `/admin/vendors/pending`        | Pending vendor applications | Yes (ADMIN) |
| POST   | `/admin/vendors/{id}/approve`   | Approve / reject vendor     | Yes (ADMIN) |
| GET    | `/admin/products/pending`       | Pending products            | Yes (ADMIN) |
| PUT    | `/admin/products/{id}/approve`  | Approve product             | Yes (ADMIN) |
| PUT    | `/admin/products/{id}/reject`   | Reject product              | Yes (ADMIN) |
| GET    | `/admin/cms/banners`            | Manage banners              | Yes (ADMIN) |
| POST   | `/admin/cms/pages`              | Create CMS page             | Yes (ADMIN) |
| PUT    | `/admin/cms/pages/{id}`         | Update CMS page             | Yes (ADMIN) |

### Support & Disputes

| Method | Path                                      | Description              | Auth |
|--------|-------------------------------------------|--------------------------|------|
| POST   | `/customer/support/tickets`               | Create ticket            | Yes  |
| GET    | `/customer/support/tickets`               | My tickets               | Yes  |
| GET    | `/customer/support/tickets/{id}`          | Ticket detail + messages | Yes  |
| POST   | `/customer/support/tickets/{id}/messages` | Add message              | Yes  |
| POST   | `/customer/support/disputes`              | Open dispute             | Yes  |

### CMS (Public)

| Method | Path                      | Description                           | Auth |
|--------|---------------------------|---------------------------------------|------|
| GET | `/banners`                   | Active banners                        | No   |
| GET | `/public/menus/{location}`   | Menu items by location (e.g., FOOTER) | No   |
| GET | `/public/pages/{slug}`       | CMS page content                      | No   |
| GET | `/public/announcements`      | Active announcements                  | No   |
| GET | `/public/faqs/categories`    | FAQ categories                        | No   |
| GET | `/public/faqs/category/{id}` | FAQs by category                      | No   |

### Loyalty, Wishlist, Reviews, Search

| Method | Path                           | Description                    | Auth |
|--------|--------------------------------|--------------------------------|------|
| GET    | `/loyalty/account`             | Loyalty balance                | Yes  |
| GET    | `/loyalty/transactions`        | Points history                 | Yes  |
| POST   | `/wishlist`                    | Add to wishlist                | Yes  |
| DELETE | `/wishlist/{productId}`        | Remove from wishlist           | Yes  |
| POST   | `/reviews`                     | Submit review                  | Yes  |
| GET    | `/reviews/product/{productId}` | Product reviews                | No   |
| GET    | `/search`                      | Elasticsearch full‑text search | No   |

For a complete and interactive API reference, start the backend and visit `http://localhost:8080/swagger-ui.html`.

---

## 📦 Deployment

### Backend (EC2)

A GitHub Actions workflow (`.github/workflows/deploy.yml`) builds the Spring Boot JAR and deploys it to an EC2 instance:

1. Push to `development` branch triggers the workflow.
2. The workflow builds the JAR, uploads it as an artifact, and downloads it on a self‑hosted runner on EC2.
3. The runner moves the JAR to `/home/ec2-user/app.jar` and restarts the systemd service `ecommerce`.

### Frontend (Amplify)

The React app is connected to **AWS Amplify** for automatic builds and hosting:

- The `master` branch (or `development`) is monitored.
- On push, Amplify runs `npm install && npm run build` and deploys to a global CDN.
- API requests are proxied via a **rewrite rule** in Amplify that forwards `/api/*` to a **CloudFront distribution**, which in turn points to the EC2 instance.

### Infrastructure as Code (optional)

A `terraform/` folder contains scripts to provision the entire AWS environment (EC2, RDS, ElastiCache, CloudFront). Use:

```bash
cd terraform
terraform init
terraform apply
```

---

## 🧩 Module Overview

| Module                | Responsibility                                                |
|-----------------------|---------------------------------------------------------------|
| `iam-module`          | Users, roles, permissions, vendors, JWT auth                  |
| `catalog-module`      | Products, categories, brands, variants, reviews               |
| `inventory-module`    | Stock tracking, reservations, stock movements                 |
| `cart-module`         | Shopping cart (guest + user), coupons                         |
| `order-module`        | Order management, sub‑orders, returns                         |
| `payment-module`      | Payment gateways (Stripe, JazzCash), refunds                  |
| `shipping-module`     | Multi‑carrier shipping, rates, tracking                       |
| `promotion-module`    | Coupons, flash sales                                          |
| `settlement-module`   | Vendor settlements, commissions                               |
| `analytics-module`    | Dashboards, metrics, aggregations                             |
| `cms-module`          | Banners, pages, menus, homepage sections, FAQs, announcements |
| `support-module`      | Tickets, disputes, live chat                                  |
| `notification-module` | Email, SMS, push, in‑app notifications                        |
| `loyalty-module`      | Points earning, redemption, tiers                             |
| `fraud-module`        | Dynamic fraud rule engine                                     |
| `media-module`        | File upload to S3                                             |
| `search-module`       | Elasticsearch integration                                     |
| `shared-core`         | Base entities, security, event bus, caching                   |
| `shared-objects`      | Shared DTOs, domain events                                    |

All modules are independent, cleanly separated, and communicate through well‑defined service interfaces.

---

## 📄 License

This project is proprietary. All rights reserved.  
If you are interested in purchasing the source code or a license, please contact sarfraz.mughal1979@gmail.com.

---

## 🙋‍♂️ Contact

For questions, custom development, or acquisition inquiries, reach out to **Sarfraz Hussain** at **sarfraz.mughal1979@gmail.com**.

---

*Last updated: May 2025*
