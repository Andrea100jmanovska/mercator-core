package aucta.dev.mercator_core.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@Entity
@Table(name = "IMAGE")
@Audited
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Lob
    @Column(name = "IMAGE_DATA")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private Product product;
}

