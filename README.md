# Speech-Language Clinical Management System

A comprehensive clinical management system for speech-language pathology assessments with supervisor review and doctor portal capabilities.

## Architecture

- **Backend**: Spring Boot 3 with PostgreSQL
- **Frontend**: Vanilla JavaScript with modern CSS
- **Database**: PostgreSQL 14
- **Containerization**: Docker & Docker Compose

## Quick Start

### Option 1: Run with Docker Compose (Recommended)

```bash
# Start all services (Database, Backend, Frontend)
docker-compose up --build

# Services will be available at:
# - Frontend: http://localhost:3000
# - Backend API: http://localhost:8080/api
# - PostgreSQL: localhost:5432
```

### Option 2: Run Locally

#### Prerequisites
- Java 21+
- Node.js 18+
- PostgreSQL 14+

#### Backend Setup
```bash
cd backend

# Start PostgreSQL (if not running)
# Update application.properties with your database credentials

# Build and run tests
./mvnw test

# Run the application
./mvnw spring-boot:run

# Backend runs on: http://localhost:8080
```

#### Frontend Setup
```bash
cd frontend

# Install dependencies (none required, pure vanilla JS)
# But Node.js server needed to serve files

# Start the frontend server
node server.js

# Frontend runs on: http://localhost:3000
```

## Features

### Dashboard
- Real-time statistics on active cases, pending reviews, and follow-ups
- Clinical workflow visualization

### Patient Management
- Patient registration and longitudinal case management
- Store patient demographics and history

### Clinical Assessments
- Hearing Screening
- Speech Sampling
- Articulation Assessment
- Fluency Analysis
- Voice Quality Assessment
- Clinical Interpretation

### Supervisor Review
- Multi-stage review workflow (Draft → Submitted → Under Review → Finalized)
- Approval and correction actions
- Case modification capabilities

### Doctor Portal
- View pending case recommendations
- Assign doctors to cases
- Track doctor discussions

## API Endpoints

The backend provides RESTful APIs at `http://localhost:8080/api` including:

- `/patients` - Patient management
- `/cases` - Clinical case management
- `/encounters` - Patient encounters
- `/assessments/*` - Various clinical assessments
- `/clinical-interpretations` - Interpretation results
- `/supervisor-review` - Supervisor actions
- `/doctor/*` - Doctor portal features

## Database Schema

The application uses the PostgreSQL schema defined in:
- `backend/src/main/resources/db/migration/V1__initial_schema.sql`
- `backend/src/main/resources/db/migration/V2__seed_reference_data.sql`
- `backend/src/main/resources/db/migration/V3__clinical_workflow_history.sql`

Schema includes:
- Patients, Cases, Encounters
- Hearing, Speech, Articulation, Fluency, Voice assessments
- Clinical Interpretations with versioning
- Supervisor Reviews
- Doctor Recommendations
- Comprehensive audit trails

## Testing

```bash
cd backend

# Run all tests
./mvnw test

# Run with coverage
./mvnw test -Darguments="-Dsonar.java.coverage.plugin=jacoco"

# Skip tests during build
./mvnw clean package -DskipTests
```

## Environment Variables

### Backend (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/speech_clinical_dev
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=validate
spring.profiles.active=postgres
```

### Docker Compose
- `POSTGRES_USER` - Database user
- `POSTGRES_PASSWORD` - Database password
- `POSTGRES_DB` - Database name
- `PORT` - Frontend port (default: 3000)

## CORS Configuration

The backend allows requests from:
- `http://localhost:3000`
- `http://localhost:5173`
- `http://127.0.0.1:3000`
- `http://127.0.0.1:5173`

## Development Workflow

1. **Backend Development**
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

2. **Frontend Development**
   ```bash
   cd frontend
   node server.js
   ```

3. **Database Access**
   ```bash
   # Connect to PostgreSQL
   psql -h localhost -U postgres -d speech_clinical_dev
   ```

## Troubleshooting

### Backend won't connect to database
- Ensure PostgreSQL is running and accessible
- Check database credentials in `application.properties`
- Verify database exists: `createdb speech_clinical_dev`

### Frontend can't reach backend API
- Ensure backend is running on port 8080
- Check CORS configuration in `CorsConfig.java`
- Open browser console to see specific errors

### Docker build fails
- Clean up Docker: `docker system prune`
- Rebuild: `docker-compose up --build`
- Check logs: `docker-compose logs -f backend`

## Project Structure

```
Speech-hear-module/
├── backend/                  # Spring Boot application
│   ├── src/main/
│   │   ├── java/            # Java source code
│   │   └── resources/       # Configuration & migrations
│   ├── src/test/            # Unit and integration tests
│   ├── pom.xml              # Maven configuration
│   └── Dockerfile           # Docker image definition
├── frontend/                # Vue.js frontend
│   ├── index.html           # Main HTML file
│   ├── app.js               # Application logic
│   ├── styles.css           # Styles
│   ├── api.js               # API client
│   ├── server.js            # Node.js server
│   └── package.json         # Node.js dependencies
├── docker-compose.yml       # Multi-container setup
└── README.md                # This file
```

## License

MIT

## Support

For issues or questions, please check the logs:

```bash
# Backend logs
docker-compose logs backend

# Frontend logs
docker-compose logs frontend

# Database logs
docker-compose logs db
```
