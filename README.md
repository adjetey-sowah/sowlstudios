# Sowl Studios - Photography Booking System

## Overview
Sowl Studios is a professional photography booking system designed specifically for graduation photography services. The system allows clients to book photography sessions and administrators to manage bookings efficiently.

## Features

### Client Features
- **Photography Package Booking**
  - Basic Package ($250)
  - Premium Package ($500)
  - Deluxe Package ($700)
- **Automated Notifications**
  - SMS confirmation upon booking
  - Status update notifications
- **Booking Details Capture**
  - Personal information
  - School/University details
  - Graduation date
  - Package preference
  - Preferred location
  - Additional requests

### Admin Features
- **Booking Management**
  - View all bookings
  - Filter bookings by status
  - Update booking status
  - Delete bookings
- **Dashboard Statistics**
  - Total bookings overview
  - Daily/Weekly/Monthly booking stats
  - Package distribution stats
  - Total sales calculation
- **Secure Authentication**
  - JWT-based authentication
  - Role-based access control

## Technical Stack

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Database**: JPA/Hibernate
- **Security**: Spring Security with JWT
- **Documentation**: OpenAPI/Swagger

### Key Components
- **Notification Services**
  - SMS Integration (MNotify)
  - Email Service
- **Caching**: Spring Cache
- **Async Operations**: Spring Async
- **CORS Configuration**
- **Global Exception Handling**

## API Endpoints

### Public Endpoints
```
POST /api/v1/auth/login           - User authentication
POST /api/v1/bookings            - Create new booking
```

### Admin Endpoints
```
GET    /api/v1/admin/bookings             - Get all bookings
GET    /api/v1/admin/bookings/{id}        - Get booking by ID
PUT    /api/v1/admin/bookings/{id}/status - Update booking status
DELETE /api/v1/admin/bookings/{id}        - Delete booking
GET    /api/v1/admin/stats                - Get booking statistics
GET    /api/v1/admin/bookings/stats/sales - Get total sales
```

## Data Models

### Booking
- ID
- First Name
- Last Name
- Phone Number
- School/University
- Graduation Date
- Package Preference
- Amount (Based on package)
- Preferred Location
- Additional Requests
- Status (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- Created At
- Updated At
- Email Sent Status
- SMS Sent Status

### Admin
- ID
- Username
- Password (Encrypted)
- Role

## Configuration

### Required Properties
```yaml
mnotify:
  api:
    key: your-api-key
    url: mnotify-api-url
  sender:
    id: your-sender-id
  enabled: true

app:
  cors:
    allowed-origins: your-frontend-url
  admin:
    default:
      phone: admin-phone-number
  email:
    from: from-email
    admin: admin-email
    enabled: true

jwt:
  secret: your-jwt-secret
  expiration: token-expiration-time
```

## Security Features
- Password encryption using BCrypt
- JWT token-based authentication
- CORS configuration
- Request validation
- Role-based access control

## Caching Strategy
- Booking data caching
- Statistics caching
- Sales data caching

## Async Operations
- SMS notifications
- Email notifications
- Booking confirmations

## Error Handling
- Global exception handling
- Custom booking exceptions
- API response standardization

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- MySQL/PostgreSQL

### Installation
1. Clone the repository
2. Configure application.yaml with your settings
3. Run `mvn clean install`
4. Start the application with `mvn spring-boot:run`

### Testing
- Unit tests available
- Integration tests for core functionality
- Test configuration in application-test.yml

## Contributing
Please read CONTRIBUTING.md for details on our code of conduct and the process for submitting pull requests.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
