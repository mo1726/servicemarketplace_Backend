package com.example.service_marketplace.Service.Impl;

import com.example.service_marketplace.Dto.AdRequestDto;
import com.example.service_marketplace.Entity.AdRequest;
import com.example.service_marketplace.Entity.AdStatus;
import com.example.service_marketplace.Entity.Service;
import com.example.service_marketplace.Entity.User;
import com.example.service_marketplace.Repository.AdRequestRepository;
import com.example.service_marketplace.Repository.ServiceRepository;
import com.example.service_marketplace.Repository.UserRepository;
import com.example.service_marketplace.Service.AdRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdRequestServiceImpl implements AdRequestService {

    private final AdRequestRepository adRepo;
    private final ServiceRepository serviceRepo;
    private final UserRepository userRepo;

    @Override
    @Transactional
    public AdRequest createAdRequest(AdRequestDto dto) {
        AdRequest ad = new AdRequest();
        ad.setService(serviceRepo.findById(dto.getServiceId())
                .orElseThrow(() -> notFound("Service not found")));
        ad.setProvider(userRepo.findById(dto.getProviderId())
                .orElseThrow(() -> notFound("Provider not found")));
        ad.setPlan(dto.getPlan());
        ad.setAmount(dto.getAmount());
        ad.setPaymentMethod(dto.getPaymentMethod());
        ad.setNotes(dto.getNotes());
        ad.setRequestedAt(LocalDate.now());
        ad.setStatus(AdStatus.PENDING);

        // Auto-calculate start/end/priority from plan
        LocalDate startDate = LocalDate.now();
        int durationDays;
        int priority;

        switch (dto.getPlan()) {
            case "14_DAYS" -> { durationDays = 14; priority = 2; }
            case "30_DAYS" -> { durationDays = 30; priority = 3; }
            default -> { durationDays = 7; priority = 1; }
        }

        ad.setStartDate(startDate);
        ad.setEndDate(startDate.plusDays(durationDays));
        ad.setPriority(priority);

        return adRepo.save(ad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequest> listAll(String status) {
        if (status == null || status.isBlank()) {
            return adRepo.findAll();
        }
        try {
            return adRepo.findByStatus(AdStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            throw bad("Unknown status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdRequest> myRequests(Long providerId) {
        User provider = userRepo.findById(providerId)
                .orElseThrow(() -> notFound("Provider not found"));
        return adRepo.findByProvider(provider);
    }

    @Override
    public void approve(Long id) {
        AdRequest adRequest = adRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad request not found"));

        // Automatically set start date and end date based on plan
        LocalDate startDate = LocalDate.now();
        LocalDate endDate;

        int priority;
        switch (adRequest.getPlan()) {
            case "7_DAYS":
                endDate = startDate.plusDays(7);
                priority = 1;
                break;
            case "14_DAYS":
                endDate = startDate.plusDays(14);
                priority = 2;
                break;
            case "30_DAYS":
                endDate = startDate.plusDays(30);
                priority = 3;
                break;
            default:
                throw new RuntimeException("Unknown plan: " + adRequest.getPlan());
        }

        adRequest.setStartDate(startDate);
        adRequest.setEndDate(endDate);
        adRequest.setPriority(priority);
        adRequest.setStatus(AdStatus.APPROVED);

        adRepo.save(adRequest);
    }


    @Override
    @Transactional
    public AdRequest activate(Long id) {
        AdRequest req = getRequestOr404(id);

        if (req.getStatus() != AdStatus.APPROVED) {
            throw bad("Only APPROVED requests can be activated");
        }
        if (req.getStartDate() == null || req.getEndDate() == null) {
            throw bad("Cannot activate without start/end dates");
        }

        Service svc = req.getService();
        svc.setAdActive(true);
        svc.setAdStartDate(req.getStartDate());
        svc.setAdEndDate(req.getEndDate());
        svc.setAdPriority(safePriority(req.getPriority()));
        serviceRepo.save(svc);

        req.setStatus(AdStatus.ACTIVATED);
        return adRepo.save(req);
    }

    @Override
    @Transactional
    public AdRequest reject(Long id, String reason) {
        AdRequest req = getRequestOr404(id);
        if (req.getStatus() == AdStatus.ACTIVATED) {
            throw bad("Cannot reject an activated request");
        }
        req.setStatus(AdStatus.REJECTED);
        if (reason != null && !reason.isBlank()) {
            req.setNotes(reason);
        }
        return adRepo.save(req);
    }

    @Override
    @Scheduled(cron = "0 15 3 * * *")
    @Transactional
    public void expireOverdueAds() {
        LocalDate today = LocalDate.now();
        List<Service> expired = serviceRepo.findByAdActiveTrueAndAdEndDateBefore(today);
        for (Service s : expired) {
            s.setAdActive(false);
            s.setAdPriority(null);
        }
        if (!expired.isEmpty()) {
            serviceRepo.saveAll(expired);
        }
    }

    // Helpers
    private AdRequest getRequestOr404(Long id) {
        return adRepo.findById(id).orElseThrow(() -> notFound("Ad request not found"));
    }

    private int safePriority(Integer p) {
        if (p == null) return 1;
        if (p < 1) return 1;
        if (p > 10) return 10;
        return p;
    }

    private ResponseStatusException bad(String msg) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private ResponseStatusException notFound(String msg) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
    }
}
