package com.foodya.foodya_backend.restaurant.dto;

import com.foodya.foodya_backend.restaurant.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

  private UUID id;
  private String name;
  private UUID restaurantId;

  /**
   * Convert Category entity to CategoryResponse DTO
   */
  public static CategoryResponse fromEntity(Category category) {
    if (category == null) {
      return null;
    }

    return CategoryResponse.builder()
        .id(category.getId())
        .name(category.getName())
        .restaurantId(category.getRestaurantId())

        .build();
  }

  /**
   * Convert list of Category entities to list of CategoryResponse DTOs
   */
  public static List<CategoryResponse> fromEntityList(List<Category> categories) {
    return categories.stream()
        .map(CategoryResponse::fromEntity)
        .collect(Collectors.toList());
  }
}
