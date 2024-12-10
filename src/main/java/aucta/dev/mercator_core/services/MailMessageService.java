package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.MailMessageStatus;
import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.models.MailMessage;
import aucta.dev.mercator_core.models.MailTemplate;
import aucta.dev.mercator_core.repositories.MailMessageRepository;
import aucta.dev.mercator_core.repositories.MailTemplateRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class MailMessageService {

    @Autowired
    private UserService userService;

    @Autowired
    private MailMessageRepository mailMessageRepository;

    @Autowired
    private MailTemplateRepository mailTemplateRepository;

    @Autowired
    private EntityManager entityManager;

    public MailMessage save(MailMessage mailMessage) {
        if (mailMessage.getId() == null) {
            mailMessage.setStatus(MailMessageStatus.PENDING);
            mailMessage.setRequeued(0);
            mailMessage.setCreatedBy(userService.getCurrentUser());
        } else {
            mailMessage.setModifiedBy(userService.getCurrentUser());
        }

        return mailMessageRepository.save(mailMessage);
    }

    public MailMessage save(MailMessage mailMessage, UserDetails creator) {
        if (mailMessage.getId() == null) {
            mailMessage.setStatus(MailMessageStatus.PENDING);
            mailMessage.setRequeued(0);
            mailMessage.setCreatedBy(userService.getCurrentUser());
        } else {
            mailMessage.setModifiedBy(userService.getCurrentUser());
        }
        return mailMessageRepository.save(mailMessage);
    }

    public MailMessage save(MailMessage mailMessage, MailTemplateType mailTemplateType, HashMap<String, String> params) {
        MailTemplate mailTemplate = mailTemplateRepository.findFirstByTemplateType(mailTemplateType);
        mailMessage = prepareMailWithTemplate(mailMessage, mailTemplateType, params);
        return mailMessageRepository.save(mailMessage);
    }

    public MailMessage prepareMailWithTemplate(MailMessage mailMessage, MailTemplateType mailTemplateType, HashMap<String, String> params) {
        MailTemplate mailTemplate = mailTemplateRepository.findFirstByTemplateType(mailTemplateType);
        String mailContent = mailTemplate.getContent();
        List<String> templateParams = mailTemplate.getMailTemplateParameters();
        if (params != null) {
            for (int i = 0; i < templateParams.size(); i++) {
                if(params.get(templateParams.get(i)) != null) {
                    mailContent = mailContent.replace("${" + templateParams.get(i) + "}", params.get(templateParams.get(i)));
                }else{
                    mailContent = mailContent.replace("${" + templateParams.get(i) + "}", "");
                }
            }
            mailMessage.setSubject(params.get("subject"));
        }
        mailMessage.setContent("<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "</head>" + mailContent);
        return mailMessage;
    }
}
