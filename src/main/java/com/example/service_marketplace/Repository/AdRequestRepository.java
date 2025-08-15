package com.example.service_marketplace.Repository;


import com.example.service_marketplace.Entity.AdRequest;
import com.example.service_marketplace.Entity.AdStatus;
import com.example.service_marketplace.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdRequestRepository extends JpaRepository<AdRequest, Long> {
    List<AdRequest> findByProvider(User provider);
    List<AdRequest> findByStatus(AdStatus status);
}
