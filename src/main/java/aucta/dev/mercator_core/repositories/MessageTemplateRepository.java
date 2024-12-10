package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, String>, JpaSpecificationExecutor<MessageTemplate> {

    Page<MessageTemplate> findAll(Specification<MessageTemplate> specification, Pageable pageable);
 }