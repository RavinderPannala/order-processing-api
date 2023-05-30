# Order Processing API

1. order-processing-api will consume order information from kafka topic using reactive kafka connector and process the received record
2. This API using the conversion of Dto to Collection and vice-versa using @mapStruct API dependency
3. This API using reactiveOperations framework instead of repository enablers for each mongo db collection.
4. This API is demonstrating CRUD operations of Order information.

Technologies Used :

1. Spring boot
2. Java8
3. Reactive Kafka
4. Reactive Mongo
5. Spring Webflux