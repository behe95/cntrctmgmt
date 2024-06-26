package com.example.cntrctmgmt.repositories;

import com.example.cntrctmgmt.entities.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
}
