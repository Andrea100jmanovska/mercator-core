package aucta.dev.mercator_core.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseDTO {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Integer discount;
    private Double deliveryPrice;
    private Double totalPrice;
    private Double averageRating;
    private String categoryName;
}