N.B.: Get the apikey and database URL from jason/mia 

Maven builds a fat jar, just send to vm and run the file 

## Usage
Send updated code to ec2 instance:
`mvn clean package && scp -i <path-to-pem-file>/<filename>.pem target/iot-group1-1.0-SNAPSHOT.jar ubuntu@16.170.224.70:/home/ubuntu`

ssh to instance
`ssh ... ubuntu@16.170.224.70`

Run the file - ampersand gives the control of the terminal back to you, so you can run more commands with the program running
`java -jar iot-group1-1.0-SNAPSHOT.jar &` 

To test the endpoints:
PUT:
```
curl --location --request PUT 'localhost:8080/api' \
--header 'Content-Type: application/json' \
--data '{
    "desk_id": "1",
    "person_present": "true",
    "stuff_on_desk": "false"
}'
```

POST:
```
curl --location --request POST 'localhost:8080/api' \
--header 'Content-Type: application/json' \
--data '{
    "desk_id": "1",
    "person_present": "true",
    "stuff_on_desk": "false"
}'
```

