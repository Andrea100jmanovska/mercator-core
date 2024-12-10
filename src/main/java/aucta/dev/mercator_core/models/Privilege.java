package aucta.dev.mercator_core.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "APP_PRIVILEGES")
@Audited
public class Privilege extends AbstractEntity implements Serializable {

    @Column(name = "NAME")
    private String name;

}