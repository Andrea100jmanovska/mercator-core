package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.MailMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface MailMessageRepository extends JpaRepository<MailMessage, String> {
}
