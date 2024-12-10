package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.MailMessageStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "MAIL_MESSAGE")
public class MailMessage extends AbstractEntity implements Serializable {


    @ManyToOne
    @JoinColumn(name = "CREATEDBY", updatable = false)
    @JsonIgnoreProperties(value = {"organization", "password", "groups", "dateCreated", "dateModified", "createdBy", "modifiedBy"}, allowSetters = true)
    private User createdBy;

    @ManyToOne
    @JoinColumn(name = "MODIFIEDBY")
    @JsonIgnoreProperties(value = {"organization", "password", "groups", "dateCreated", "dateModified", "createdBy", "modifiedBy"}, allowSetters = true)
    private User modifiedBy;

    @Column(name = "SENDER")
    private String sender;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="MAIL_MESSAGE_RECEIVERS")
    @Fetch(FetchMode.SUBSELECT)
    List<String> receivers;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "CONTENT", length = 2048)
    private String content;

    @Column(name = "REQUEUED")
    private Integer requeued;

    @Column(name = "DATE_SENT")
    private Date dateSent;

    @Column(name ="FAILURE_REASON")
    private String failureReason;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private MailMessageStatus status;

    @Column(name="TICKET_REF_ID")
    private String ticketRefId;
}
