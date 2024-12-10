package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.OrganizationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "ORGANIZATION")
@Audited
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "organization")
public class Organization extends AbstractEntity implements Serializable{


    @Column(name = "NAME")
    private String name;

    @Column(name = "NAME_EN")
    private String nameEn;

    @Column(name = "ACTIVE")
    private Integer active;

    @Column(name = "ORGANIZATION_TYPE")
    private OrganizationType type;


    public Organization toOrganizationDTO() {
        Organization organization = new Organization();
        organization.setId(this.getId());
        organization.setNameEn(this.getNameEn());
        organization.setName(this.getName());
        organization.setType(this.getType());
        return organization;
    }

}