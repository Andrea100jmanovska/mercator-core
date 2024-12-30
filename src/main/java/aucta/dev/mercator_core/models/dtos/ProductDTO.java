package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.enums.OrderStatus;
import aucta.dev.mercator_core.models.Category;
import aucta.dev.mercator_core.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ProductDTO {
    private String id;

    private String name;

    private Category category;

    private String description;

    private Boolean isFavorited;

    private Boolean isInCart;

    private Double price;

    private Date dateCreated;

    private Double averageRating;

    private Integer quantity;

    private Integer discount;

    private Double deliveryPrice;

    private Long orderId;

    private LocalDateTime orderDate;

    private OrderStatus orderStatus;

    private String orderFirstName;

    private String orderLastName;

    private String orderEmail;

    private Double totalPrice;

    private List<CommentDTO> comments = new ArrayList<>();

    private List<ImageDTO> images = new ArrayList<>();

    private User user;

}
