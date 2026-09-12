# 📰 PressPilot

> A smart Android application for managing newspaper customers, daily deliveries, monthly billing, and reports.

PressPilot is an Android-based newspaper management system designed to help newspaper agencies move from manual record keeping to a centralized digital workflow. The application provides separate Admin and Customer experiences and uses Firebase for authentication and cloud data storage.

## ✨ Features

### 🔐 Authentication & Roles
- User registration with email, mobile number, password, and role
- Firebase Authentication
- Role-based login for **Admin** and **Customer**
- Separate dashboards for Admin and Customer
- Logout functionality

### 👥 Customer Management
- Add new customers
- Store customer name, mobile number, address, newspaper type, and price per day
- View customer records
- Customer profile information

### 📰 Daily Delivery Management
- View customers for daily delivery
- Mark newspapers as **Delivered** or **Not Delivered**
- Store delivery date and delivery time
- Customers can view their delivery status/time

### 🧾 Monthly Billing
- Generate monthly bills from delivered-day records
- Calculate the bill using:
  `Delivered Days × Price Per Day`
- Store bills in Cloud Firestore
- View generated monthly bills

### 📊 Reports
- Total customer count
- Delivered and not-delivered delivery counts for the current month
- Monthly revenue calculated from generated bills

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Java | Android application development |
| Android Studio | Development environment |
| XML | Android UI layouts |
| Firebase Authentication | User authentication |
| Cloud Firestore | Cloud database |
| Material Components | UI components |
| Gradle | Build and dependency management |

## 🏗️ Project Structure

```text
PressPilot/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/example/presspilot/
│           │   ├── adapter/
│           │   ├── firebase/
│           │   ├── model/
│           │   ├── AddCustomerActivity.java
│           │   ├── BillingActivity.java
│           │   ├── CustomerBillActivity.java
│           │   ├── CustomerDashboardActivity.java
│           │   ├── CustomerDeliveryActivity.java
│           │   ├── CustomerListActivity.java
│           │   ├── CustomerProfileActivity.java
│           │   ├── DashboardActivity.java
│           │   ├── DeliveryActivity.java
│           │   ├── LoginActivity.java
│           │   ├── MainActivity.java
│           │   ├── RegisterActivity.java
│           │   └── ReportsActivity.java
│           │
│           └── res/
│               ├── layout/
│               ├── drawable/
│               ├── mipmap-*/
│               └── values/
│
├── gradle/
├── gradlew
├── gradlew.bat
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Android Studio
- JDK 11
- Android SDK
- A Firebase account/project

### Clone the Repository

```bash
git clone https://github.com/Achal0112/PressPilot.git
cd PressPilot
```

Open the project in Android Studio and allow Gradle to synchronize.

### Firebase Configuration

PressPilot uses Firebase Authentication and Cloud Firestore.

1. Create a Firebase project.
2. Register the Android app using the package name:
   `com.example.presspilot`
3. Enable **Email/Password Authentication**.
4. Create/configure a **Cloud Firestore** database.
5. Add the Firebase Android configuration file (`google-services.json`) to the `app/` directory.
6. Configure your Firestore collections/documents according to the application models and code.
7. Sync Gradle and run the application.

> **Security:** Never commit `local.properties`, Firebase service-account credentials, private keys, or other sensitive secrets to a public repository. Review Firebase Authentication and Firestore Security Rules before deploying the app.

## 📸 Screenshots

Add screenshots of the actual application here. A recommended folder structure is:

```text
screenshots/
├── login.png
├── register.png
├── admin-dashboard.png
├── customer-dashboard.png
├── customer-list.png
├── delivery.png
├── billing.png
└── reports.png
```

Then add them to this README, for example:

```markdown
![Admin Dashboard](screenshots/admin-dashboard.png)
```

## 📋 Main Firestore Collections

The application code uses these main Firestore collections:

- `users`
- `customers`
- `deliveries`
- `bills`

The `users` collection stores account information including email, mobile number, and role.

## 🧮 Billing Logic

Monthly bills are generated from successful delivery records.

```text
Total Amount = Delivered Days × Price Per Day
```

The application creates a monthly bill for each customer and stores it in the `bills` collection.

## 🎯 Project Objectives

- Digitize newspaper customer management
- Reduce manual record keeping
- Track daily newspaper deliveries
- Automate monthly bill calculation
- Provide useful monthly delivery and revenue reports
- Provide separate Admin and Customer workflows

## 🔮 Future Enhancements

- Online payment integration
- Subscription renewal notifications
- Push notifications
- PDF invoice/receipt generation
- Advanced analytics
- Delivery route optimization
- Admin web dashboard
- Export reports to PDF/Excel

## 👨‍💻 Developer

**Achal Deore**

Bachelor of Engineering — Information Technology

## 📄 License

This project was developed for educational and academic purposes.
