package com.example.service_marketplace.Entity;

import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private User provider;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
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
