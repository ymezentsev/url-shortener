# Url Shortener

This project is a REST API service that allows users to create and use short URLs. There is also an option to edit a
short link for easier readability. Users have the ability to view their statistics. Each short link has limited time
life.

There is also an administrator part at the project. The administrator can additionally see complete information of all
links in the system (by whom and when the link was created), can filter links by a specific user, edit and delete links
created by other users.
The administrator also has the opportunity to see information of all users, he can change their rights ('USER' or '
ADMIN') and delete users.

To view web-version of the application (when app running) follow the
link [Url-Shortener](http://localhost:8080/V2/urls)

To view and test the application in Postman, you need to import the collection of requests from the "
url-shotener.postman_collection.json" file.

Generated OpenApi documentation avaliable (when app running) [Swagger-ui](http://localhost:8080/swagger-ui/index.html)
and [OAS](http://localhost:8080/v3/api-docs)

## Getting Started

To run this project, you need to have the following software installed on your computer:

- JDK 17
- Docker

### Running the Project

Follow these steps to get your application up and running:

1. Ensure Docker is running.

2. Run the UrlShortener application.

## Tech stack

- Spring Boot
- Spring Data
- Spring MVC
- Spring Security
- JWT
- PostgreSQL
- FlyWay
- Swagger/OpenApi 3.0
- JUnit 5
- Docker
- Docker-compose
- Testcontainers
- Thymeleaf
- Bootstrap

