package com.example.service_marketplace.Repository;

import com.example.service_marketplace.Entity.Service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Long> {

    List<Service> findByProviderId(Long id);

}
