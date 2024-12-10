package aucta.dev.mercator_core.models;

import aucta.dev.mercator_core.enums.MessageTemplateFor;
import aucta.dev.mercator_core.enums.MessageTemplatePriority;
import aucta.dev.mercator_core.enums.MessageTemplateType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "MESSAGE_TEMPLATES")
@Audited
public class MessageTemplate extends AbstractEntity implements Serializable {

    @Column(name = "TEMPLATE_TYPE")
    @Enumerated(EnumType.STRING)
    private MessageTemplateType templateType;

    @Column(name = "TEMPLATE_PRIORITY")
    @Enumerated(EnumType.STRING)
    private MessageTemplatePriority templatePriority;

    @Column(name = "TEMPLATE_FOR")
    @Enumerated(EnumType.STRING)
    private MessageTemplateFor templateFor;

    @Column(name = "NAME")
    private String name;

    @OneToMany(mappedBy = "messageTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<MessageTemplateLocalization> messageTemplateLocalizations;


}