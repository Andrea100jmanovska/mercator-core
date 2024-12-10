package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.OrganizationType;
import aucta.dev.mercator_core.models.Organization;
import aucta.dev.mercator_core.repositories.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

    @Autowired
    private OrganizationRepository repository;

    public Organization get(String id) {
        return repository.getReferenceById(id);
    }

    public Organization save(Organization organization) {
        return repository.save(organization);
    }

    public Organization findByName(String name) {
        return repository.findByName(name);
    }

    public Organization findByNameEn(String nameEn) {
        return repository.findByNameEn(nameEn);
    }

    public Organization getDefaultOrganization() {
        return repository.findFirstByType(OrganizationType.CLIENT);
    }


}
