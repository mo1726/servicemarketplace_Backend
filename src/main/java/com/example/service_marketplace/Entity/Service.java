package com.example.service_marketplace.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 1000)
    private String description;

    private Double price;
    private String location;
    private String imageUrl;

    private Boolean adActive = false;
    private LocalDate adStartDate;
    private LocalDate adEndDate;
    private Integer adPriority;



    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnoreProperties({"services", "bookings", "password"}) // ✅ break recursion
    private User provider;

    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"services"}) // ✅ avoid looping back
    private Category category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"service"}) // ✅ avoid loop from Booking → Service
    private List<Booking> bookings;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"service"}) // ✅ avoid loop from Review → Service
    private List<Review> reviews;

    @Transient
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
