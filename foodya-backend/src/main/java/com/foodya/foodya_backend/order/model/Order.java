package com.foodya.foodya_backend.order.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
  private UUID id;

  User userId;
  
}
