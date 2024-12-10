package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.models.MessageTemplate;
import aucta.dev.mercator_core.repositories.MessageTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTemplateService {

    @Autowired
    private MessageTemplateRepository messageTemplateRepository;

    public List<MessageTemplate> getAll() {
        return messageTemplateRepository.findAll();
    }


}
