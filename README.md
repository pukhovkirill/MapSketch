# MapSketch
**Full-stack web application for creating, editing, and managing geospatial objects on a map**

## Overview

**MapSketch** is a full-stack application that allows users to interact with a map interface to create, edit, delete, and view geospatial objects. It consists of a Spring Boot backend and a JavaScript frontend using OpenLayers for dynamic map rendering and editing.

## Tech Stack

| Layer     | Technology                         |
| --------- | ---------------------------------- |
| Frontend  | Pure JavaScript, OpenLayers        |
| Backend   | Java 21, Spring Boot, MyBatis      |
| API       | REST, JSON                         |
| Database  | PostgreSQL                         |
| Dev Tools | Docker                             |

### Backend (Java + Spring Boot)

* RESTful API for GeoObject management (CRUD)
* MyBatis-based repository
* Modular and extensible architecture

### Frontend (JavaScript + OpenLayers)

* Interactive OpenLayers map
* Add/edit/delete geospatial objects
* Sidebar list of all added objects

## REST API Overview

| Method | Endpoint               | Description            |
| ------ | ---------------------- | ---------------------- |
| GET    | `/api/geoobjects`      | Fetch all GeoObjects   |
| POST   | `/api/geoobjects`      | Create new GeoObject   |
| PUT    | `/api/geoobjects/{id}` | Update existing object |
| DELETE | `/api/geoobjects/{id}` | Delete GeoObject by ID |

## Deploy

```shell
$ git clone https://github.com/pukhovkirill/MapSketch.git
$ cd MapSketch
$ chmod +x start.sh && sudo ./start.sh
```

## License

Copyright (c) 2025 Pukhov Kirill \
Distributed under the MIT License. See the [LICENSE](LICENSE) file for details.
