# 2-Factor-Authentication

A learning project focused on implementing a secure authentication system using **Two-Factor Authentication (2FA)**. The project covers user registration, login, OTP verification, password reset, email verification, and authentication-related security concepts.

## Project Overview

This project is built to understand and implement back-end security concepts through a structured authentication system.

The authentication flow includes:

* User registration
* Email/OTP verification
* User login
* Two-Factor Authentication
* OTP generation and verification
* OTP resend functionality
* Forgot password
* Password reset using secure tokens
* Account verification
* Global exception handling
* User authentication and authorization

## Tech Stack

* **Java**
* **Spring Boot**
* **Spring Security**
* **Spring Data JPA**
* **Hibernate**
* **JSP**
* **MySQL**
* **Maven**
* **JavaMail / Email Service**
* **HTML / CSS**

## Project Structure

auth-system
│
├── config
│
├── controller
│   ├── AuthController
│   ├── OtpController
│   └── PasswordController
│
├── dto
│   ├── ForgotPasswordRequest
│   ├── LoginRequest
│   ├── OtpRequest
│   ├── RegisterRequest
│   └── ResetPasswordRequest
│
├── entity
│   ├── User
│   ├── OtpVerification
│   └── PasswordResetToken
│
├── repository
│   ├── UserRepository
│   ├── OtpVerificationRepository
│   └── PasswordResetTokenRepository
│
├── service
│   ├── AuthService
│   ├── OtpService
│   ├── MailService
│   └── PasswordResetService
│
├── serviceImpl
│   ├── AuthServiceImpl
│   ├── OtpServiceImpl
│   ├── MailServiceImpl
│   └── PasswordResetServiceImpl
|
├── security
│   ├── CustomUserDetails
│   ├── CustomUserDetailsService
│   └── SecurityConfig
│
├── exception
│   ├── ResourceNotFoundException
│   ├── UserAlreadyExistsException
│   ├── InvalidOtpException
│   ├── InvalidTokenException
│   ├── AccountNotVerifiedException
│   └── GlobalExceptionHandler
│
├── util
│   ├── OtpGenerator
│   ├── TokenGenerator
│   └── EmailUtil
│
├── validator
│   ├── PasswordMatches
│   └── PasswordMatchesValidator
│
└── AuthApplication


## Database Design

The project uses three main entities:

### User

Stores user account information.

+----------------+
|     USERS      |
+----------------+
| id             |
| name           |
| email          |
| password       |
| enabled        |
| email_verified |
+----------------+

### OTP Verification

Stores OTP information associated with a user.

+----------------------+
| OTP_VERIFICATION     |
+----------------------+
| id                   |
| otp                  |
| purpose              |
| expiry_time          |
| verified             |
| user_id (FK)         |
+----------------------+

### Password Reset Token

Stores tokens used for password reset functionality.

+---------------------------+
| PASSWORD_RESET_TOKEN      |
+---------------------------+
| id                        |
| token                     |
| expiry_time               |
| used                      |
| user_id                   |
+---------------------------+

### Entity Relationship

                +----------------+
                |     USERS      |
                +----------------+
                | id             |
                | name           |
                | email          |
                | password       |
                | enabled        |
                | email_verified |
                +----------------+
                     |        |
          One-To-Many|        |One-To-Many
                     |        |
      +--------------+        +----------------+
      |                                       |
+----------------------+          +---------------------------+
| OTP_VERIFICATION     |          | PASSWORD_RESET_TOKEN     |
+----------------------+          +---------------------------+
| id                   |          | id                        |
| otp                  |          | token                     |
| purpose              |          | expiry_time               |
| expiry_time          |          | used                      |
| verified             |          | user_id                   |
| user_id (FK)         |          +---------------------------+
+----------------------+

## Controllers

### AuthController

Handles authentication and user registration.

```text
GET  /login
POST /login

GET  /register
POST /register

### OtpController

Handles OTP verification and OTP resend functionality.

```text
GET  /verify-otp
POST /verify-otp

POST /resend-otp
```

### PasswordController

Handles forgot-password and password-reset functionality.

```text
GET  /forgot-password
POST /forgot-password

GET  /reset-password
POST /reset-password
```

## Services

The service layer contains the main business logic of the application.

### AuthService

Responsible for:

* User registration
* User login
* Authentication-related business logic
* Account verification handling

### OtpService

Responsible for:

* OTP generation
* OTP verification
* OTP expiry handling
* OTP resend functionality

### MailService

Responsible for:

* Sending verification emails
* Sending OTPs
* Sending password reset emails

### PasswordResetService

Responsible for:

* Password reset token generation
* Password reset validation
* Token expiry handling
* Password update

## Utility Classes

### OtpGenerator

Generates OTPs used during authentication and verification.

### TokenGenerator

Generates tokens used for password reset and other verification-related operations.

### EmailUtil

Provides email-related utility functionality.

## Exception Handling

The project contains custom exceptions for handling different authentication scenarios:

* `ResourceNotFoundException`
* `UserAlreadyExistsException`
* `InvalidOtpException`
* `InvalidTokenException`
* `AccountNotVerifiedException`

`GlobalExceptionHandler` handles exceptions centrally and provides appropriate responses/views.

## JSP Views

The application uses JSP for the front-end authentication pages.

```text
register.jsp
login.jsp
verify-otp.jsp
forgot-password.jsp
reset-password.jsp
dashboard.jsp
error.jsp
```

## Authentication Flow

### Registration Flow

```text
User
  |
  v
Register
  |
  v
Create Account
  |
  v
Generate OTP
  |
  v
Send OTP via Email
  |
  v
Verify OTP
  |
  v
Account Verified
```

### Login Flow

```text
User
  |
  v
Login
  |
  v
Validate Credentials
  |
  v
OTP Verification
  |
  v
Verify OTP
  |
  v
Dashboard
```

### Password Reset Flow

```text
Forgot Password
       |
       v
Enter Email
       |
       v
Generate Reset Token
       |
       v
Send Reset Link/Token
       |
       v
Validate Token
       |
       v
Reset Password
```

## Security Concepts Covered

This project is primarily developed for learning and practicing:

* Two-Factor Authentication
* OTP-based verification
* Password security
* Account verification
* Token-based password reset
* Token expiry
* Authentication and authorization
* Input validation
* Exception handling
* Secure user management
* Database relationships

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/abhaypalke/2-Factor-Authentication.git
```

### 2. Open the project

Open the project in an IDE such as:

* IntelliJ IDEA
* Eclipse
* Spring Tool Suite

### 3. Configure the database

Create a MySQL database and configure the database credentials in the application's configuration file.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_system
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. Configure email

Configure your email service credentials required for sending OTPs and password-reset emails.

**Do not commit real email passwords, API keys, database passwords, or other secrets to GitHub.**

### 5. Run the application

Run:

```text
AuthApplication
```

Then open the application in your browser.

## Learning Purpose

This project is created as a **learning project** to strengthen my understanding of back-end development and application security, particularly around authentication, OTP verification, password recovery, user privacy, and data integrity.

## Future Improvements

Possible improvements include:

* Rate limiting for OTP requests
* OTP attempt limits
* Account lockout after repeated failed login attempts
* Refresh-token based authentication
* Improved password policies
* Audit logging
* CSRF protection
* More comprehensive input validation
* Unit and integration testing

## Author

**Abhay Palke**

GitHub: `https://github.com/abhaypalke`
