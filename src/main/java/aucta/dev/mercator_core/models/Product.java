package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.ColorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Products")
@Audited
public class Product extends AbstractEntity implements Serializable {

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PRICE")
    private Double price;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @ElementCollection
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "sizes")
    private List<String> sizes;

    @Column(name = "DISCOUNT")
    private Integer discount;

    @Column(name = "DELIVERY_PRICE")
    private Double deliveryPrice;

    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;

    @Column(name = "AVERAGE_RATING")
    private Double averageRating;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserProductHistory> userAccesses = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "COLOR")
    @ElementCollection(targetClass = ColorType.class)
    @Enumerated(EnumType.STRING)
    private List<ColorType> colors;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_FAVORITES",
            joinColumns = @JoinColumn(name = "PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "USER_ID")
    )
    List<User> users;

    @ManyToMany(fetch = FetchType.LAZY,  cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "CART_PRODUCTS",
            joinColumns = @JoinColumn(name = "PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "CART_ID")
    )
    List<Cart> carts;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;
}
