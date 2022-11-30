package com.itservice.creditservice.repository;


import com.itservice.creditservice.collection.Credit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

public interface CreditRepository extends MongoRepository<Credit, String>{
	
}