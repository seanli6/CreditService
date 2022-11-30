# Development


## Run Credit Service

### Required Software

1. Maven
2. Docker & Docker-Compose

## How To Run
1. Run docker-compose
   `docker-compose -up`


2. Wait till `Credit-service` container is up and Ready

   * Log example : Started CreditServiceApplication in 7.574 seconds (JVM running for 8.76)
    
    
3. Open http://localhost:8080/swagger-ui/ on your browser. (Password: password Username: rbcfolks) 


## How to Use

### 1. Check the interface design document from http://localhost:8080/swagger-ui/ 
   (Password: password Username: rbcfolks)

### 2. How to use loadAndParseCsv

  **a. Go to postname or soapui. Create a POST request with URL:  localhost:8080/credits**

![image](https://user-images.githubusercontent.com/70720442/204915760-01456697-dd14-4bf2-878b-99c009f33da0.png)

  **b. Go to the Authorization tab, select Basic Auth. Input Username: rbcfolks Password: password**
  
  ![image](https://user-images.githubusercontent.com/70720442/204917685-a2e60fb4-0157-414b-9a3a-278c44f7c1a6.png)


  **c. Go to the Body tab. Input "file" under the Key, select the "File" as the type. Click the "Select Files" button and select the sample.csv that comes along with the source code**
  
  ![image](https://user-images.githubusercontent.com/70720442/204931328-63cea712-6e85-489b-86f1-153557e9d2c6.png)


  ![image](https://user-images.githubusercontent.com/70720442/204918261-1a50c6dc-3299-4eeb-b0ce-b62dfcf20e0b.png)

  **d. Click Send and check the result:**
  
![image](https://user-images.githubusercontent.com/70720442/204919032-67ef6708-ed2c-4057-8f08-ffa2c7a355eb.png)


### 3. How to use search

   **a. In your browser, open http://localhost:8080/swagger-ui/ (Username: rbcfolks Password: password)**
   
   
   **b. Click credit-controller and click /credits/search**
   
   ![image](https://user-images.githubusercontent.com/70720442/204919363-840d1a9c-f518-48bb-b30e-055efa61dfd0.png)
   
   
   **c. click "Try it out"**
   
   ![image](https://user-images.githubusercontent.com/70720442/204919578-1c76ecc4-9d8f-4da1-b9fe-7e80f050a9c5.png)

   
   **d. Input search criterias**
   
      homeOwnership [Rent, Home Mortgage, Own Home]
      maxCreditScore
      minCreditScore
      page
      size
      term[Long Term, Short Term]
           

   **c. Click "Execute" and check the search result:**
   
   ![image](https://user-images.githubusercontent.com/70720442/204920188-c608aaab-bd62-4901-90fa-7ae6861d3976.png)


   ![image](https://user-images.githubusercontent.com/70720442/204920233-f2a2d906-3d1b-40b7-98c6-17b2e15b8d08.png)

   


