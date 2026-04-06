# Product Service

## Description
Manages product information and stock.

## Features
- CRUD for Products
- Stores data in MongoDB
- Sends retry jobs to Kafka in case of failure.

## Port
- Default: `8081`

## Endpoints
- `GET /productos`: List all products
- `POST /productos`: Create a new product
- `POST /productos/retry`: Endpoint for Broker Service to retry product creation.

## Infrastructure Repo
- [Main Infrastructure & Orchestration](https://github.com/UniModelo-Projects/Vacaciones_Microservices_Infrastructure)
