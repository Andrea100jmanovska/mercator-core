package aucta.dev.mercator_core.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    private static final String serialVersionUID = "43272422905946063";

    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATEDBY", updatable = false)
    @JsonIgnoreProperties(value = {"organization", "password", "groups", "dateCreated", "dateModified", "createdBy", "modifiedBy"}, allowSetters = true)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODIFIEDBY")
    @JsonIgnoreProperties(value = {"organization", "password", "groups", "dateCreated", "dateModified", "createdBy", "modifiedBy"}, allowSetters = true)
    private User modifiedBy;

    @Column(name = "DATE_CREATED", updatable = false)
    private Date dateCreated;

    @Column(name = "DATE_MODIFIED")
    private Date dateModified;

    @PrePersist
    private void prePersist() {

        if (id == null) {
            this.setId(UUID.randomUUID().toString());
        }
        if (this.getDateCreated() == null) {
            this.setDateCreated(new Date());
        }
        if (this.getDateModified() == null) {
            this.setDateModified(new Date());
        }
    }

    @PreUpdate
    private void preUpdate() {

        this.setDateModified(new Date());
    }

}
