# Product Service

## Description
Manages product information and stock.

## Features
- CRUD for Products
- Support for Product Images (`imagenUrl`)
- Stores data in MongoDB
- Sends retry jobs to Kafka in case of failure.

## Port
- Default: `8081`

## Endpoints
- `GET /productos`: List all products
- `GET /productos/{id}`: Get product by ID
- `POST /productos`: Create a new product
- `PUT /productos/{id}`: Update an existing product
- `POST /productos/retry`: Endpoint for Broker Service to retry product creation.
- `PUT /productos/{id}/stock/reduce?quantity=X`: Reduce product stock.

## Infrastructure Repo
- [Main Infrastructure & Orchestration](https://github.com/UniModelo-Projects/Vacaciones_Microservices_Infrastructure)
