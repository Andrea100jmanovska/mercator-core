package aucta.dev.mercator_core.models.requests;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.models.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CreateProductRequest implements Serializable {
    private String name;
    private CategoryType category;
    private String description;
    private Double price;
    private Integer quantity;
    private Integer discount;
    private Double deliveryPrice;
    private Double totalPrice;
    private User user;
}