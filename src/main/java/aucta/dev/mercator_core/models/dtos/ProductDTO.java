package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private String id;

    private String name;

    private CategoryType category;

    private String description;

    private Double price;

    private Date dateCreated;

    private Integer quantity;

    private Integer discount;

    private Double deliveryPrice;

    private Double totalPrice;

    private List<ImageDTO> images = new ArrayList<>();

    private User user;
}
