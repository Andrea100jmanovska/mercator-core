package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.enums.OrderStatus;
import aucta.dev.mercator_core.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderedProductResponseDTO {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Double totalAmount;
    private List<ProductResponseDTO> products = new ArrayList<>();
    private UserDTO userDTO;
}