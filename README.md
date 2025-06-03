# Terminus

Simple library for handling Spring Boot RestController-based automatic endpoint version 
redirection by header `X-Terminus-Version`.

## Overview

Terminus is a Java-based web application built with **Spring Boot**,
supporting **Spring MVC** for traditional web services and **WebFlux**.
It includes OpenAPI documentation powered by **SpringDoc**.

## Technical Stack

- **Java**: 21
- **Spring Boot**: 3.5.0
- **SpringDoc OpenAPI UI**: 2.6.0
- **Frameworks**: WebMVC, WebFlux

## Prerequisites

- **JDK**: 21 or later
- **Maven**: 3.6+ (or use the included Maven wrapper)

## Usage

### Current RestController

```java
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LatestController {

   @RequestMapping(value = "/endpoint", method = RequestMethod.GET)
   public String endpoint() {
      return "latest version endpoint";
   }
}
```

### Versioned Endpoint Controllers

#### Call Endpoint for Version 1.1
```java
import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

@Terminus(versions = {"1.1"})
public class V11Controller {

   @TerminusMapping(value = "/endpoint", method = RequestMethod.GET)
   public String endpoint() {
      return "version '1.1' endpoint";
   }
}
```

```java
RestClient restClient = RestClient.create();

String response = restClient.get()
    .uri("http://localhost:8080/endpoint")
    .header("X-Terminus-Version", "1.1")
    .retrieve()
    .body(String.class);

```

#### Call Endpoint for Version 1.2 - 1.3 ('1.2' for header value)
```java
import com.firmys.terminus.annotations.Terminus;
import com.firmys.terminus.annotations.TerminusMapping;

@Terminus(versions = {"1.2", "1.3"})
public class V12Controller {

   @TerminusMapping(value = "/endpoint", method = RequestMethod.GET)
   public String endpoint() {
      return "version '1.2' or `1.3` endpoint";
   }
}
```

```java
RestClient restClient = RestClient.create();

String response = restClient.get()
    .uri("http://localhost:8080/endpoint")
    .header("X-Terminus-Version", "1.2")
    .retrieve()
    .body(String.class);

```

## Quick Start

1. **Clone the repository**:
   ```bash
   git clone git@github.com:0megaPhil/terminus.git
   ```

2. **Build the project**:
   ```bash
   ./mvnw clean install
   ```

## API Documentation

Access the following endpoints when the application is running:
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

## Features

- Dual support for **Spring MVC** and **reactive WebFlux**
- Comprehensive **OpenAPI documentation**
- Modern **Spring Boot 3.x** enterprise-grade features
- **Maven wrapper** for consistent builds across environments

## Project Structure

```
terminus/
├── src/                    # Application source code
├── pom.xml                # Maven configuration
├── .gitignore             # Git ignore patterns
├── mvnw                   # Maven wrapper (Unix/macOS)
└── mvnw.cmd               # Maven wrapper (Windows)
```

## Contributing

We welcome contributions! To contribute:
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add your feature"`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a pull request with a clear description

## License

**MIT License**

Copyright (c) 2024 Firmys

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
