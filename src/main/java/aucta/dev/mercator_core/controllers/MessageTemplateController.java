package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.services.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/messageTemplates")
public class MessageTemplateController {

    @Autowired
    MessageTemplateService messageTemplateService;

    @Secured({"ROLE_CLIENT"})
    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public ResponseEntity getAllMessageTemplatesWithoutPaging() throws IOException {
        return ResponseEntity.ok(messageTemplateService.getAll());
    }
}
