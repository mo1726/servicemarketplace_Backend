package com.example.service_marketplace.Repository;

import com.example.service_marketplace.Entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByProviderId(Long id);

    // Nightly expiry job
    List<Service> findByAdActiveTrueAndAdEndDateBefore(LocalDate date);

    // Only active ads, ordered by priority DESC then startDate DESC
    List<Service> findByAdActiveTrueAndAdStartDateLessThanEqualAndAdEndDateGreaterThanEqualOrderByAdPriorityDescAdStartDateDesc(
            LocalDate nowStartInclusive, LocalDate nowEndInclusive
    );

    // Browse: return all services, but order so that current ads come first,
    // then by priority DESC, then by start date DESC, then id DESC.
    @Query("""
           SELECT s FROM Service s
           ORDER BY
             CASE
               WHEN s.adActive = true
                 AND s.adStartDate <= :today
                 AND s.adEndDate >= :today
               THEN 0 ELSE 1
             END,
             s.adPriority DESC NULLS LAST,
             s.adStartDate DESC NULLS LAST,
             s.id DESC
           """)
    List<Service> findAllSortedForBrowse(@Param("today") LocalDate today);
}
