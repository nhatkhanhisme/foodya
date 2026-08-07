package com.foodya.foodya_backend.catalog.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString(exclude = "menuItems")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID", updatable = false, nullable = false)
  private UUID id;

  // ========== BASIC INFORMATION ==========

  @Column(nullable = false, unique = true, length = 200)
  private String name;

  @Column(nullable = false, length = 500)
  private String address;

  // V5 migration renamed column phone_number → phone
  @Column(name = "phone", nullable = false, unique = true, length = 20)
  private String phoneNumber;

  @Column(length = 100)
  private String email;
  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false, length = 100)
  private String cuisine; // Vietnamese, Italian, Japanese, Korean, Thai, American, Chinese, Cafe

  // ========== MEDIA ==========

  @Column(length = 500)
  private String imageUrl;

  @Column(length = 500)
  private String coverImageUrl;

  // ========== RATING & REVIEWS ==========

  // V5 migration renamed column rating → rating_avg (BR-16: simple arithmetic mean)
  @Column(name = "rating_avg", nullable = false)
  @Builder.Default
  private Double rating = 0.0;

  @Column(nullable = false)
  @Builder.Default
  private Integer totalReviews = 0;

  // ========== STATUS ==========

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private RestaurantStatus status = RestaurantStatus.PENDING;

  // Only populated when status = REJECTED (UC-A02); owner can fix and resubmit (BR-31)
  @Column(length = 500)
  private String rejectionReason;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isOpen = true;

  @Column(nullable = false)
  @Builder.Default
  private Boolean isFeatured = false;

  // ========== OPERATING HOURS ==========

  @Column(length = 10)
  private String openingTime; // Format: "HH:mm" - e.g., "09:00"

  @Column(length = 10)
  private String closingTime; // Format: "HH: mm" - e.g., "22:00"

  @Column(length = 200)
  private String openingHours; // e.g. "Mon-Fri: 09:00-22:00, Sat-Sun: 08:00-23:00"

  // ========== DELIVERY INFORMATION ==========

  @Column(nullable = false)
  @Builder.Default
  private Long deliveryFee = 0L;

  @Column(nullable = false)
  @Builder.Default
  private Long minimumOrder = 0L;

  @Column(nullable = false)
  @Builder.Default
  private Long freeDeliveryThreshold = 0L;

  private Integer estimatedDeliveryTime;

  @Column(nullable = false)
  @Builder.Default
  private Double maxDeliveryDistance = 10.0;

  // ========== LOCATION (for future geo-search) ==========

  private Double latitude;
  private Double longitude;

  // ========== STATISTICS ==========

  @Column(nullable = false)
  @Builder.Default
  private Integer totalOrders = 0;

  @Column(nullable = false)
  @Builder.Default
  private Integer orderCount = 0; // used for popularity sort

  @Column(nullable = false)
  @Builder.Default
  private Long averageOrderValue = 0L;

  // ========== PROMO & FEATURES ==========

  @Column(length = 200)
  private String promotionText;

  @Column(nullable = false)
  @Builder.Default
  private Boolean hasPromotion = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean acceptsCash = true;

  @Column(nullable = false)
  @Builder.Default
  private Boolean acceptsCard = true;

  // ========== TIMESTAMPS ==========

  @CreationTimestamp
  @Column(updatable = false, nullable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private Instant updatedAt;

  private Instant deletedAt;

  // ========== RELATIONSHIPS ==========

  @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<MenuItem> menuItems = new ArrayList<>();

  @Column(columnDefinition = "UUID", nullable = false)
  private UUID ownerId;

  // ========== HELPER METHODS ==========

  public void addMenuItem(MenuItem menuItem) {
    menuItems.add(menuItem);
    menuItem.setRestaurant(this);
  }

  public void removeMenuItem(MenuItem menuItem) {
    menuItems.remove(menuItem);
    menuItem.setRestaurant(null);
  }

  public void updateRating(Double newRating, Integer reviewCount) {
    this.rating = newRating;
    this.totalReviews = reviewCount;
  }

  public void incrementOrderCount() {
    this.totalOrders++;
    this.orderCount++;
  }

  public void updateAverageOrderValue(Long totalRevenue) {
    if (this.totalOrders > 0) {
      this.averageOrderValue = totalRevenue / this.totalOrders;
    }
  }

  public boolean isCurrentlyOpen() {
    return this.status == RestaurantStatus.APPROVED && Boolean.TRUE.equals(this.isOpen);
  }

  public boolean isFreeDelivery(Long orderValue) {
    return orderValue >= this.freeDeliveryThreshold;
  }

  public Long calculateDeliveryFee(Long orderValue) {
    if (isFreeDelivery(orderValue)) {
      return 0L;
    }
    return this.deliveryFee;
  }
}
