# Todo List Backend API

## Dependencies

``` springdoc-openapi-starter-webmvc-ui``` dependency is used for Swagger UI.

``` spring-boot-starter-data-jpa:3.1.3``` dependency is used for JPA.

``` Lombok ``` dependency is for handling data in a easy manner.

## Running the Service with Docker Compose:

To run the Spring Boot service along with an H2 database instance using Docker Compose, follow these steps:

### Build the Service:

First, build the Spring Boot service using Gradle. Open a terminal/command prompt in the project's root directory and run:

`./gradlew build`

This command will compile the code, run **tests**, and package the application.

### Build Docker Images:

After successfully building the Spring Boot service, we can create Docker images for the application and the H2 database. Run the following command to build the Docker images:

``` docker-compose build ```

This command will use the **docker-compose.yml** file in the project to build the necessary Docker images.

### Start the Containers:

Once the Docker images are built, we can start the containers by running the following command:

```docker-compose up```

Docker Compose will start the H2 database instance and the Spring Boot application in separate containers. We can access the Spring Boot service locally at http://localhost:8080

## OpenAPI
- Json documentation: http://localhost:8080/v3/api-docs
- Swagger UI: http://localhost:8080/swagger-ui/index.html


With these steps, we'll have the Spring Boot service and H2 database running in Docker containers. Users can easily start and interact with the service without worrying about configuring the environment manually.