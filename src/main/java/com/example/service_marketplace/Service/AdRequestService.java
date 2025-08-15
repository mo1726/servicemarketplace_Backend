package com.example.service_marketplace.Service;

import com.example.service_marketplace.Dto.AdRequestDto;
import com.example.service_marketplace.Entity.AdRequest;

import java.time.LocalDate;
import java.util.List;

public interface AdRequestService {
    AdRequest createAdRequest(AdRequestDto dto);

    List<AdRequest> listAll(String status);          // status can be null to list all
    List<AdRequest> myRequests(Long providerId);


    void approve(Long id);
    AdRequest activate(Long id);
    AdRequest reject(Long id, String reason);

    /** Cron job: set service.adActive=false when adEndDate < today */
    void expireOverdueAds();
}
