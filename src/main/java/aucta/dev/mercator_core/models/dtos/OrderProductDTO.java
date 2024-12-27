package aucta.dev.mercator_core.models.dtos;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderProductDTO {

    private String productId;
    private Integer quantity;
    private Double price;
    private Double deliveryPrice;
}
