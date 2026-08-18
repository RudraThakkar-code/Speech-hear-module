# Hosting & Deployment Guide

## Quick Deployment Options

### **Option 1: Railway.app (Recommended for Beginners)**

**Easiest & Fastest - Takes ~5 minutes**

1. **Push to GitHub**
   ```bash
   cd Speech-hear-module
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/RudraThakkar-code/speech-hear-module.git
   git push -u origin main
   ```

2. **Deploy on Railway**
   - Go to https://railway.app
   - Click "New Project" → "Deploy from GitHub repo"
   - Select your repository
   - Add PostgreSQL plugin (click "Add Plugin")
   - Configure environment variables:
     ```
     SPRING_DATASOURCE_URL=postgresql://${{Postgres.PGHOST}}:5432/speech_clinical_prod
     SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
     SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
     SPRING_JPA_HIBERNATE_DDL_AUTO=validate
     ```
   - Deploy!

3. **Access your app**
   - Frontend: `https://your-app.up.railway.app`
   - API: `https://your-app-api.up.railway.app/api`

**Cost**: ~$5-20/month depending on usage

---

### **Option 2: DigitalOcean (Best Value)**

**More Control - Takes ~10 minutes**

1. **Create DigitalOcean Account**
   - Sign up at https://www.digitalocean.com
   - Add credit card

2. **Create Droplet** (Server)
   ```bash
   - Image: Docker
   - Size: $6/month (1GB RAM, 25GB SSD)
   - Region: Choose closest to you
   ```

3. **SSH into Droplet**
   ```bash
   ssh root@YOUR_DROPLET_IP
   ```

4. **Clone and Deploy**
   ```bash
   git clone https://github.com/YOUR_USERNAME/speech-hear-module.git
   cd speech-hear-module
   
   # Create .env file
   cat > .env << EOF
   POSTGRES_PASSWORD=your_secure_password_here
   SPRING_PROFILES_ACTIVE=postgres
   EOF
   
   # Start with Docker Compose
   docker-compose up -d
   ```

5. **Setup Domain (Optional)**
   - Point domain DNS to your Droplet IP
   - Install Let's Encrypt SSL: `sudo snap install certbot`

**Cost**: $6/month for server + ~$3-5/month for database (or use built-in PostgreSQL container)

---

### **Option 3: Heroku**

**Simple but Premium - Takes ~5 minutes**

1. **Install Heroku CLI**
   ```bash
   npm install -g heroku
   heroku login
   ```

2. **Create Heroku App**
   ```bash
   heroku create your-app-name
   heroku addons:create heroku-postgresql:hobby-dev
   ```

3. **Configure Environment**
   ```bash
   heroku config:set SPRING_JPA_HIBERNATE_DDL_AUTO=validate
   heroku config:set SPRING_PROFILES_ACTIVE=postgres
   ```

4. **Deploy**
   ```bash
   git push heroku main
   ```

**Cost**: Free tier available (limited), paid plans start at $7/month

---

### **Option 4: AWS (Most Powerful)**

**Professional Setup - Takes ~30 minutes**

1. **Create AWS Account** at https://aws.amazon.com

2. **Use CloudFormation** (Automated Setup)
   - Create stack from template
   - Configure:
     - ECS Fargate for containers
     - RDS for PostgreSQL
     - Application Load Balancer
     - Auto Scaling

3. **Deploy via AWS CLI**
   ```bash
   aws ecr get-login-password | docker login --username AWS --password-stdin YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com
   docker build -t speech-clinical-backend ./backend
   docker tag speech-clinical-backend:latest YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/speech-clinical-backend:latest
   docker push YOUR_ACCOUNT_ID.dkr.ecr.us-east-1.amazonaws.com/speech-clinical-backend:latest
   ```

**Cost**: Free tier available (12 months), then ~$20-50/month

---

### **Option 5: Google Cloud Run**

**Serverless & Fast - Takes ~10 minutes**

1. **Setup Google Cloud Project**
   ```bash
   gcloud projects create speech-clinical
   gcloud config set project speech-clinical
   gcloud auth login
   ```

2. **Enable Services**
   ```bash
   gcloud services enable run.googleapis.com
   gcloud services enable sql.googleapis.com
   gcloud services enable artifact-registry.googleapis.com
   ```

3. **Create PostgreSQL Instance**
   ```bash
   gcloud sql instances create speech-db --database-version=POSTGRES_14
   gcloud sql databases create speech_clinical_prod --instance=speech-db
   ```

4. **Deploy Backend**
   ```bash
   cd backend
   gcloud builds submit --tag gcr.io/speech-clinical/backend
   gcloud run deploy backend --image gcr.io/speech-clinical/backend --platform managed
   ```

5. **Deploy Frontend**
   ```bash
   cd frontend
   gcloud builds submit --tag gcr.io/speech-clinical/frontend
   gcloud run deploy frontend --image gcr.io/speech-clinical/frontend --platform managed
   ```

**Cost**: Free tier up to 2 million requests/month, then pay-per-use

---

## Deployment Checklist

- [ ] GitHub repository created and code pushed
- [ ] Environment variables configured
- [ ] Database credentials set (strong password)
- [ ] CORS origins updated for production domain
- [ ] SSL/HTTPS enabled
- [ ] Domain name configured (optional)
- [ ] Backup strategy in place
- [ ] Monitoring/Logging enabled
- [ ] Database migrations tested
- [ ] Load testing completed

## Environment Variables for Production

```properties
# Backend (Spring Boot)
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/speech_clinical_prod
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=strong_password_here
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_PROFILES_ACTIVE=postgres
SERVER_PORT=8080

# Frontend
REACT_APP_API_URL=https://your-api-domain.com/api
PORT=3000
NODE_ENV=production
```

## Docker Image Optimization

### Smaller Backend Image (Multi-stage build already in Dockerfile)
```dockerfile
# Reduced size: ~500MB → ~200MB
FROM eclipse-temurin:21-jre-alpine
```

### Frontend Optimization
```dockerfile
FROM node:18-alpine
RUN npm install -g serve
COPY . /app
WORKDIR /app
RUN npm install --omit=dev
EXPOSE 3000
CMD ["serve", "-s", "/app", "-l", "3000"]
```

## Monitoring & Logging

### Application Metrics
- Backend: Spring Boot Actuator
  ```
  https://your-api.com/actuator/health
  https://your-api.com/actuator/metrics
  ```

- Frontend: Error tracking (Sentry)
  ```bash
  npm install @sentry/react
  ```

### Database Monitoring
- Automated backups enabled
- Query performance monitoring
- Connection pool optimization

## Scaling Considerations

1. **Horizontal Scaling**
   - Run multiple backend instances
   - Load balancer distributes traffic
   - Sticky sessions for supervisor reviews

2. **Database Optimization**
   - Connection pooling (HikariCP configured)
   - Query optimization
   - Index on frequently accessed columns

3. **Caching**
   - Redis for session caching
   - CDN for static files (optional)

## Rollback Strategy

```bash
# Keep last 3 deployments
# Quick rollback if issues detected
docker tag image:latest image:previous
docker run image:previous
```

## Support & Troubleshooting

### Common Issues

**Backend won't connect to database**
```bash
# Check PostgreSQL connection
psql -h your-db-host -U postgres -d speech_clinical_prod

# Verify credentials in environment variables
```

**Frontend can't reach API**
- Check CORS configuration in `CorsConfig.java`
- Verify API URL in frontend environment
- Check firewall/security group rules

**Database migration failed**
```bash
# Manual migration (SSH into server)
docker exec speech_db psql -U postgres -d speech_clinical_prod -f migration.sql
```

---

## My Recommendation

**For quick launch**: Railway.app ($5/month, easiest)
**For best value**: DigitalOcean ($6/month, more control)
**For enterprise**: AWS or Google Cloud (scalable, pay-per-use)

Would you like me to help with setup for any specific platform?
