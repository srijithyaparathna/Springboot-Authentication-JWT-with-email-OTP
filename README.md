# Spring Boot Authentication with JWT and Email OTP (SendGrid)

This project demonstrates how to implement JWT-based authentication with email OTP (One-Time Password) verification in a Spring Boot application. The authentication flow includes:

1. User registration via email.
2. Sending an OTP to the user's email for verification via SendGrid.
3. Generating and using JWT tokens for user authentication.
4. Protecting endpoints with JWT authentication.

## Features

- User registration with email.
- Email OTP verification.
- JWT token-based authentication for secured endpoints.
- User role-based access (Admin and User roles).
- Spring Boot and MySQL integration for user data storage.
- SendGrid integration for email OTP.

## Prerequisites

Make sure you have the following installed:

- JDK 11 or higher
- Maven or Gradle
- MySQL Database
- Spring Boot
- SendGrid account for sending emails

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/your-username/Springboot-Authentication-JWT-with-email-OTP.git
cd Springboot-Authentication-JWT-with-email-OTP
