package com.example.service_marketplace.Repository;

import com.example.service_marketplace.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
