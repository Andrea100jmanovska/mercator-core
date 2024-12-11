package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.CategoryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "Products")
@Audited
public class Product extends AbstractEntity implements Serializable {

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "CATEGORY")
    private CategoryType category;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "DISCOUNT")
    private Integer discount;

    @Column(name = "DELIVERY_PRICE")
    private Double deliveryPrice;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    @Lob
    @Column(name = "IMAGE")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
