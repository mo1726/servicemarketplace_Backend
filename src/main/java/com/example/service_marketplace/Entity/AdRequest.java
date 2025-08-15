package com.example.service_marketplace.Entity;




import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Table(name = "ad_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    @Enumerated(EnumType.STRING)
    private AdStatus status;

    private String plan;
    private Integer priority;
    private Double amount;

    private String paymentMethod; // ✅ Added this field

    private String notes;


    private LocalDate requestedAt;
    private LocalDate approvedAt;
    private LocalDate startDate;
    private LocalDate endDate;
}
