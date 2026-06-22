package com.foodya.foodya_backend.restaurant.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID  id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    private String imageUrl;

    @Column(nullable = false)
    private String category; // Appetizer, Main Course, Dessert, Beverage, etc.

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    private Integer preparationTime; // in minutes

    // Nutritional information (optional)
    private Integer calories;
    private Boolean isVegetarian;
    private Boolean isVegan;
    private Boolean isGlutenFree;
    private Boolean isSpicy;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderCount = 0; // Track popularity

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationship with Restaurant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    // Helper method
    public void incrementOrderCount() {
        this.orderCount++;
    }
}
