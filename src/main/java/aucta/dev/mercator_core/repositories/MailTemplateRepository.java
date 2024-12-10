package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.models.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface MailTemplateRepository extends JpaRepository<MailTemplate, String> {
    List<MailTemplate> findAll();
    MailTemplate findFirstByTemplateType(MailTemplateType templateType);
    MailTemplate findByTemplateType(MailTemplateType templateType);
}
