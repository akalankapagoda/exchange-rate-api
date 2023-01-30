# Exchange Rate API

Exposes a simple API for retrieving exchange rates and converting currency.

## Prerequisites

- Java 17+
- Maven 2.8+

## How to Build

Navigate to the project root directory and run the following command.

`mvn clean install`

## How to Run

- Update the src/main/resources/application.properties file to adjust configurations if required.
- Navigate to the project root directory and run the following command.
  `mvn spring-boot:run`

## How to Verify the Deployment

- Navigate to <base url>/api/exchangeRate/hello
- This should return an info message with an HTTP SC 200

## API Documentation

This provides a Swagger UI and OpenAPI implementation for documentation.

Please see [here](https://aka-exchange-rate-api.herokuapp.com/api/swagger-ui/index.html) for the deployed documentation.


