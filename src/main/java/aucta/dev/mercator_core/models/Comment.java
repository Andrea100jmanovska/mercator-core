package aucta.dev.mercator_core.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "COMMENTS")
@Audited
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

}