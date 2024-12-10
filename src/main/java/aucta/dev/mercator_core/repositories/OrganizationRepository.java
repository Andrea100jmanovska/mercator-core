package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.enums.OrganizationType;
import aucta.dev.mercator_core.models.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String>, JpaSpecificationExecutor<Organization> {

    Organization findByName(String name);

    Organization findByNameEn(String nameEn);

    Page<Organization> findAll(Specification<Organization> specification, Pageable pageable);

    Organization findFirstByType(OrganizationType type);

}