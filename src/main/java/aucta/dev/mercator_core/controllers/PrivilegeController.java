package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Privilege;
import aucta.dev.mercator_core.services.PrivilegeService;
import aucta.dev.mercator_core.validators.PrivilegeValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/privileges")
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    PrivilegeValidator validator;

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAllPrivileges(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection,
            @RequestParam(value = "searchParams") String searchParams
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(privilegeService.all(filterMap, PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/all", method = RequestMethod.GET)
    public ResponseEntity getAllPrivilegesWithoutPaging() {
        return ResponseEntity.ok(privilegeService.all());
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getPrivilege(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(privilegeService.get(id));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createPrivilege(@RequestBody Privilege privilege) throws BadRequestError {
        validator.validateCreate(privilege);
        return ResponseEntity.ok(privilegeService.save(privilege));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updatePrivilege(@RequestBody Privilege privilege) throws BadRequestError {
        validator.validateUpdate(privilege);
        return ResponseEntity.ok(privilegeService.save(privilege));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deletePrivilege(@PathVariable(value = "id") String id) throws BadRequestError {
        validator.validateDelete(id);
        return ResponseEntity.ok(privilegeService.remove(id));
    }

}