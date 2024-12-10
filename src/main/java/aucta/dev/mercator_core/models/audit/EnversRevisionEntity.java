package aucta.dev.mercator_core.models.audit;

import aucta.dev.mercator_core.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(name = "AUDIT_LOG")
@RevisionEntity(EnversListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class EnversRevisionEntity extends DefaultRevisionEntity {

    /**
     * rev number.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hseq")
    @SequenceGenerator(name = "hseq", sequenceName = "HIBERNATE_SEQUENCE")
    @Column(name = "id")
    private int id;
    /**
     * user who made revision.
     */
    @ManyToOne
    @JoinColumn(name = "USER_AUDITED")
    private User user;
}