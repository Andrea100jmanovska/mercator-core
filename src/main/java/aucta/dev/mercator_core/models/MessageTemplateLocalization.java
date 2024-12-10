package aucta.dev.mercator_core.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "MESSAGE_TEMPLATE_LOCALIZATION")
@Audited
public class MessageTemplateLocalization extends AbstractEntity implements Serializable {

    @Column(name = "CONTENT", length = 2048)
    private String content;

    @Column(name = "LANGUAGE")
    private String language;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "MESSAGE_TEMPLATE_ID")
    private MessageTemplate messageTemplate;
}
