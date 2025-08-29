package com.deliverysystem.foodservice.repository;

import com.deliverysystem.foodservice.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {  

}