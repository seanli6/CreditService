# Development


## Run Credit Service

### Required Software

1. Maven
2. Docker & Docker-Compose

## How To
1. Build application using
   `mvn clean install Dmaven.test.skip=true`
   
2. Run docker-compose
   `docker-compose -up`


3. Wait till the READY of `Credit-service` container
    
    
4. Open http://localhost:8080/swagger-ui/ (Username: rbcfolks Password: password)


## How to Use

### 1. Check the interface design document from http://localhost:8080/swagger-ui/ (Username: rbcfolks Password: password)

### 2. How to use loadAndParseCsv

  a. Go to postname or soapui. Create a POST request with URL:

![image](https://user-images.githubusercontent.com/70720442/204915760-01456697-dd14-4bf2-878b-99c009f33da0.png)

