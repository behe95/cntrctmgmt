package com.example.cntrctmgmt.repositories;

import com.example.cntrctmgmt.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
