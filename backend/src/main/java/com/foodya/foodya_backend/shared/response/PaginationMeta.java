package com.foodya.foodya_backend.shared.response;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Pagination metadata for list responses")
public class PaginationMeta {

  @JsonProperty("page")
  @Schema(description = "Current page number (0-indexed)", example = "0")
  private Integer page;

  @JsonProperty("page_size")
  @Schema(description = "Number of elements per page", example = "10")
  private Integer pageSize;

  @JsonProperty("total_elements")
  @Schema(description = "Total number of elements", example = "100")
  private Long totalElements;

  @JsonProperty("total_pages")
  @Schema(description = "Total number of pages", example = "10")
  private Integer totalPages;

  @JsonProperty("has_next")
  @Schema(description = "Whether there are more pages after current", example = "true")
  private Boolean hasNext;

  @JsonProperty("has_previous")
  @Schema(description = "Whether there are more pages before current", example = "false")
  private Boolean hasPrevious;
  
  // Factory method
  public static PaginationMeta from(Page<?> page) {
    return PaginationMeta.builder()
        .page(page.getNumber())
        .pageSize(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .hasNext(page.hasNext())
        .hasPrevious(page.hasPrevious())
        .build();
  }
}
