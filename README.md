# Revolut Account transfer Service 


RESTful API for money transfers between accounts.

## Dependencies

- **Java 11**

Alternatively project can be build and run with **Docker**.

## Tech stack

- [Liquibase](https://www.liquibase.org/)
- [Sparkjava](http://sparkjava.com/)
- [H2](https://www.h2database.com)
- [jOOQ](https://www.jooq.org/)
- [REST-assured](http://rest-assured.io/)

## Maven wrapper

Service is using [maven wrapper](https://github.com/takari/maven-wrapper), so no need setup maven 
execution environment.

## Important notes

- Database is running completely in memory. After the application is stopped, all the 
data will be lost.
- Data consistency is guaranteed by append-only approach for storing the data and usage of idempotency keys.
- Pessimistic locking used for update accounts balance.   


## Database

Scripts to populate [database schema](https://github.com/majidbha/transfer-challange/tree/master/http-client.http)
and at startup apply changes.


## Running tests

To run __unit__ and __integration tests__ execute the following command:

```shell script
./mvnw clean verify
``` 

## Running service locally

Build an executable jar:

```shell script
./mvnw clean package
```

Then run it:

```shell script
java -jar ./target/transfer-jar-with-dependencies.jar
```

By default server is running on port `4000`. It can be changed using `server.port` system property, e.g.:

```shell script
java -Dserver.port=4000 -jar ./target/transfer-jar-with-dependencies.jar
```

## Building and running inside docker

**First run may take some time. Provided image is not optimized for any kind of workload 
and can only be used for testing.**

First build an image:

```shell script
docker build -t transfer:latest .
```

Start the container using the following command:

```shell script
docker run --rm -it -p 4000:4000 transfer:latest
```

## Endpoints

All rest apis exist in [http-client](https://github.com/majidbha/transfer-challange/http-client.http)

### Create a new account

Request: `POST /api/v1/accounts`

Request body: 

```
{"currency":"USD"}

```

The output of the command should be similar to the following:

```shell script
HTTP/1.1 201 Created
Date: Tue, 28 Jan 2020 16:00:52 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.4.18.v20190429)

{
  "accountNumber": "d00035af-46e6-4ba4-bf90-f6718bd49ab2",
  "currency": "USD"
}
```

`accountNumber`  in the response is an ID of a newly created account.

### Get account information

Request: `GET /api/v1/accounts/:account_number`

Request body: `No`


The output of the command should be similar to the following:

```shell script
{
  "accountStatusType": "ACTIVE",
  "accountNumber": "d00035af-46e6-4ba4-bf90-f6718bd49ab2",
  "createDatetime": [2020,1,28,19,30,52,662261000],
  "balance": 0.00,
  "currency": "USD"
}
```

Newly created accounts always have a `0` balance.

### Top-up account balance

Request: `POST /api/v1/accounts/deposit`

Request body: JSON document with the following properties
- `transactionId` - id of the deposit transaction. It is used for idempotency. If not set in request created by server.
- `accountNumber` - account number for top-up
- `amount` - amount to credit in currency of account .


The output of the command should be similar to the following:

```shell script

HTTP/1.1 201 Created
Date: Tue, 28 Jan 2020 16:24:25 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.4.18.v20190429)

{
  "transactionId": "5f273e85-74fc-4eab-b5cf-1e2991c505bc"
}
```

### Withdraw account balance 
 
Request: `POST /api/v1/accounts/withdraw` 

Request body: JSON document with the following properties
- `transactionId` - id of the deposit transaction. It is used for idempotency. If not set in request created by server.
- `accountNumber` - account number for top-up
- `amount` - amount to credit in currency of account .


The output of the command should be similar to the following:

```shell script
HTTP/1.1 201 Created
Date: Tue, 28 Jan 2020 17:12:01 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.4.18.v20190429)

{
  "transactionId": "2"
}
```

### Transfer money between accounts

Request: `POST /api/v1/transfers`

Request body: JSON document with the following properties
- `transactionId` - id of the transfer. It is used for idempotency. It should be a **new random UUID** every time.
- `source_account_id` - id of the source account. It will be debited.
- `target_account_id` - id of the target account. It will be credited.
- `amount` - amount to transfer in **cents**.



The output of the command should be similar to the following:

```shell script
HTTP/1.1 201 Created
Date: Tue, 28 Jan 2020 17:31:26 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.4.18.v20190429)

{
  "transactionId": "3"
}

```

After a successful transfer, new balances can be observed by querying the account transaction list.

### Get account transactions

Request: `GET /api/v1//accounts/:account_number/transaction`


The output of the command should be similar to the following:

```

HTTP/1.1 201 Created
Date: Tue, 28 Jan 2020 17:57:47 GMT
Content-Type: application/json
Transfer-Encoding: chunked
Server: Jetty(9.4.18.v20190429)

{
  "accountTransactionDTOList": [
    {
      "createDatetime": [2020,1,28,20,50,1,365024000],
      "financialAccount": 2,
      "amount": 10.00,
      "balance": 10.00,
      "transactionType": "DEPOSIT",
      "transfer": 1,
      "transactionId": "3"
    }
  ]
}  
    

```