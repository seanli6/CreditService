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

  b. Go to the Authorization tab, select Basic Auth. Input Username: rbcfolks Password: password
  
  ![image](https://user-images.githubusercontent.com/70720442/204917685-a2e60fb4-0157-414b-9a3a-278c44f7c1a6.png)


  c. Goto the Body tab. Input "file" under the Key, select the "File" as the type
  
      click the Select Files and select the sample.csv comes with source code
  
  ![image](https://user-images.githubusercontent.com/70720442/204917851-1e8af210-fdf7-419a-b8fb-cb8e25e8c1f8.png)

  ![image](https://user-images.githubusercontent.com/70720442/204918261-1a50c6dc-3299-4eeb-b0ce-b62dfcf20e0b.png)

  
