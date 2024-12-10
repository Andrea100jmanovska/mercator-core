package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.MailTemplateType;
import aucta.dev.mercator_core.models.MailTemplate;
import aucta.dev.mercator_core.repositories.MailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailTemplateService implements AbstractService<MailTemplate, String> {

    @Autowired
    MailTemplateRepository mailTemplateRepository;

    @Autowired
    UserService userService;

    public List<MailTemplate> all(){
        return mailTemplateRepository.findAll();
    }

    @Override
    public Page<MailTemplate> all(Pageable pageable){
        return mailTemplateRepository.findAll(pageable);
    }

    @Override
    public MailTemplate get(String id){
        return mailTemplateRepository.getById(id);
    }

    public MailTemplate getByMailTemplateType(MailTemplateType mailTemplateType){
        return mailTemplateRepository.findByTemplateType(mailTemplateType);
    }

    @Override
    public MailTemplate save(MailTemplate mailTemplate,  UserDetails userDetails){
        if(mailTemplate != null && mailTemplate.getId() != null && userDetails != null) {
            mailTemplate.setModifiedBy(userService.getUserByUsername(userDetails.getUsername()));
        }
        return mailTemplateRepository.save(mailTemplate);
    }

    @Override
    public MailTemplate save(MailTemplate mailTemplate){
        return mailTemplateRepository.save(mailTemplate);
    }


    @Override
    public Boolean remove(String id) {
        mailTemplateRepository.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    public Boolean remove(String id, UserDetails creator) {
        MailTemplate mailTemplate = mailTemplateRepository.getById(id);
        mailTemplate.setModifiedBy(userService.getUserByUsername(creator.getUsername()));
        mailTemplate.setIsDeleted(Boolean.TRUE);
        mailTemplateRepository.deleteById(id);
        return null;
    }


    public MailTemplate getByType(MailTemplateType mailTemplateType){
        return mailTemplateRepository.findFirstByTemplateType(mailTemplateType);
    }
}