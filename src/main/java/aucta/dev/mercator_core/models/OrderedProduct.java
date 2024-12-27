package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "ORDERED_PRODUCT")
@Audited
public class OrderedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "ORDERED_PRODUCT_ITEM",
            joinColumns = @JoinColumn(name = "ORDERED_PRODUCT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID")
    )
    private List<Product> products = new ArrayList<>();
}
