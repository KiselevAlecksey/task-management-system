
<p style="text-align: center;">Authentication and task manager microservices simple example</p>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#credits">Credits</a> •
  <a href="#license">License</a>
</p>
## Key Features

![Static Badge](https://img.shields.io/badge/RU_Guide-White?style=plastic)

* User Authentication
    - User registration, JWT token refresh
    - Ability to add users with roles ADMIN, USER, GUEST
* Task Manager
    - Create tasks, manage their status, assign executors
* User Manager
    - Basic CRUD operations

## Requirements

- Java 21 or higher
- Maven 3.6+

## How to Use

To clone and run this application, you need to install [Git](https://git-scm.com/), [Docker](https://www.docker.com/), and [Docker Compose](https://docs.docker.com/compose/install/) on your computer. You should also have an SSH key pair for cloning via SSH (recommended). Follow the instructions here: [SSH](https://github.com/KiselevAlecksey/gitInfo?tab=readme-ov-file#проверка-наличия-ssh-ключа)

Follow these steps in the command line:

```bash
# Clone this repository
$ git clone git@github.com:KiselevAlecksey/task-management-system.git

# Navigate to the repository directory
$ cd your_repository

# Run Docker Compose to build and start the applications
$ docker-compose up --build
```

Prerequisites

1. Make sure you're in the root directory of your project where the `docker-compose.yml` file is located.

After starting, you can check the container status using the command:

`docker ps`

2. Ensure all containers are running. You should see the following containers:

- tms-auth-container
- task-manager-server
- postgres-tms
- postgres-auth

Health Check:

3. Verify that services are working correctly by visiting the following URLs in your web browser:

- For auth-server: http://localhost:9090/actuator/health
- For task-manager-server: http://localhost:8080/actuator/health

4. Stopping Containers:

To stop and remove containers, run:

`docker-compose down`

## Adding Data

Once the services are successfully started, you can use [Postman](https://www.postman.com/) to make API requests.

- After starting the containers, you can connect to databases to add data. Use any tool for working with PostgreSQL, such as [pgAdmin](https://www.pgadmin.org/) or the command line.

- Passwords are specified in `docker-compose.yml`.

- Swagger UI is available at http://localhost:8080/swagger-ui/index.html#/ and http://localhost:9090/swagger-ui/index.html#/.

- You can also use [Swagger.io](https://editor-next.swagger.io/) to view the Postman JSON package.

Note

If you encounter issues while starting containers, check the logs using the command:

`docker-compose logs`

This will help diagnose problems and find solutions.