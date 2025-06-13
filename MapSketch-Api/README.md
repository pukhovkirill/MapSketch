# MapSketch-Api
Server-side API for creating, editing, and managing 
geo-objects in the MapSketch system

## Stack

- Java 21
- Maven
- Backend
    - Spring MVC
    - Spring Security
- Database
    - Postgresql
    - Flyway
    - MyBatis
- Tests
  - JUnit 5
  - Testcontainers

## Deploy

```shell
$ git clone https://github.com/pukhovkirill/MapSketch-Api.git
$ cd MapSketch-Api
$ chmod +x genenvv.sh && ./genenvv.sh
// before running need to create application-prod.yaml in /src/main/resources/
$ sudo docker-compose up
```

## License

Copyright (c) 2025 Pukhov Kirill \
Distributed under the MIT License. See the [LICENSE](LICENSE) file for details.