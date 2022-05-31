# Tasks Planner ✨

<p align="center">
   <img src="docs/images/logo.png">
</p>

<p align="center">
   <img src="https://img.shields.io/badge/Dev-miliariadnane-blue?style"/>
   <img src="https://img.shields.io/badge/language-java-red?style"/>
   <img src="https://img.shields.io/badge/Framework-SpringBoot-green?style"/>
   <img src="https://img.shields.io/github/stars/miliariadnane/tasks-planner"/>
   <img src="https://img.shields.io/github/forks/miliariadnane/tasks-planner"/>
   <img src="https://img.shields.io/static/v1?label=%F0%9F%8C%9F&message=If%20Useful&style=style=flat&color=BC4E99"/>
   <a href="https://github.com/miliariadnane/tasks-planner/github/workflows/build.yml/badge.svg?branch=main"><img src="https://github.com/miliariadnane/tasks-planner/github/workflows/build.yml/badge.svg?branch=main"/></a>
   <a href="https://github.com/miliariadnane/tasks-planner/github/workflows/deploy.yml/badge.svg?branch=main"><img src="https://github.com/miliariadnane/tasks-planner/github/workflows/deploy.yml/badge.svg?branch=main"/></a>

</p>

# Overview

- This project is a web app developed with spring Boot (backend) & Angular (frontend).
- The idea behind this project is to create an app that allows to plan and manage the tasks of a developers teams.
- Once a team member has created the task, the application notifies team members that a new task was created using Discord Webhooks.
- The workflow is managed by Git Actions  CI/CD in order to automate deployment into AWS (AWS Elastic Beanstalk & AWS RDS).
- [new feature upcoming](#upcoming-features)

# Application live

> [Link to the application](http://tasksplanner-env.eba-pkikrhha.us-east-1.elasticbeanstalk.com/)

# Technologies
<!-- Spring -->
<a href="https://spring.io/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/springio/springio-icon.svg" alt="spring" width="40" height="40"/>
</a>
<!-- Hibernate -->
<a href="https://hibernate.org/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/hibernate/hibernate-ar21.svg" alt="hibernate" width="100" height="40"/>
</a>
<!-- Docker -->
<a href="https://www.docker.com/" target="_blank">
<img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original-wordmark.svg" alt="docker" width="40" height="40"/>
</a>
<!-- Angular -->
<a href="https://angular.io/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/angular/angular-icon.svg" alt="angular" width="40" height="40"/>
</a>
<!-- Junit5 -->
<a href="https://junit.org/junit5/" target="_blank">
<img src="https://junit.org/junit4/images/junit5-banner.png" alt="Junit5" width="110" height="35"/>
</a>
<!-- postgres -->
<a href="https://www.postgresql.org/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/postgresql/postgresql-horizontal.svg" alt="postgres" width="110" height="40"/>
</a>
<!-- aws -->
<a href="https://aws.amazon.com/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/amazon_aws/amazon_aws-ar21.svg" alt="aws" width="80" height="40"/>
</a>
<!-- discord -->
<a href="https://discord.com/" target="_blank">
<img src="https://www.vectorlogo.zone/logos/discordapp/discordapp-ar21.svg" alt="discord" width="80" height="40"/>
</a>
<br><br>

- Backend:
    - Spring Frameworks :
      - Spring Boot 
      - Spring Data Jpa 
      - Spring MVC
      - Spring Security / JWT (for new feature)
    - Hibernate 
    - Docker 
    - JIB (build docker image tool)
    - frontend-maven-plugin library (build angular frontend)
    - Junit5 (unit / integration tests)
    - Mockito 
  
- Frontend:
    - Angular 

- Database:
    - Postgres 

- CI/CD:
    - GitHub Actions

- Cloud:
    - AWS Elastic Beanstalk 
    - AWS RDS

- Webhooks:
    - Discord 
 
# Process Diagram 
<p align="center">
   <img src="docs/images/process-diagram.png">
</p>

# Upcoming features

- [x] Build & Deploy workflows using Git Actions.
- [x] Add users backend endpoints & UI.
- [x] Add security layer (Spring security + JWT).
- [x] Store user image in aws using s3 bucket.
- [x] Notify affected users when a task is created via discord.
- [ ] Complete unit and integration tests.
- [ ] Notify admin via discord about deployment progress.
- [ ] Add monitoring dashboard (Spring Actuator).
- [ ] Reset password and mailing service by aws.

# Some commands to run the application

### Build image and push it to dockerhub (configure jib on pom.xml)

```
mvn clean install jib:build -Djib.to.image=miliariadnane/tasks-planner:v4 -Djib.to.auth.username=miliariadnane -Djib.to.auth.password=yourpassword
```

### Create database via command line (create db first on AWS RDS)

1. connect to the database
```
docker run -it --rm postgres:alpine psql -h aa33y2ufc6m1bo.cllttiutrcg0.us-east-1.rds.amazonaws.com -U nanodev -d postgres
```

2. create database
```
create database tasksplanner;   
```
