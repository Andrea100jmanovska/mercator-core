package aucta.dev.mercator_core.validators;

import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Privilege;
import aucta.dev.mercator_core.repositories.PrivilegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrivilegeValidator {

    @Autowired
    PrivilegeRepository repository;

    public void validateCreate(Privilege privilege) throws BadRequestError {
        if (privilege.getName() == null || privilege.getName().isEmpty())
            throw new BadRequestError("name is required!");
    }

    public void validateUpdate(Privilege privilege) throws BadRequestError {
        if (repository.findById(privilege.getId()).orElse(null) == null)
            throw new BadRequestError("No such privilege!");
        if (privilege.getName() == null || privilege.getName().isEmpty())
            throw new BadRequestError("name is required!");
    }

    public void validateDelete(String privilegeId) throws BadRequestError {
        if (repository.findById(privilegeId).orElse(null) == null)
            throw new BadRequestError("No such privilege!");
    }
}
