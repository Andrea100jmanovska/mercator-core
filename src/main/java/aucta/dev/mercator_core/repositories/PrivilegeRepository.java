package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Privilege;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, String>, JpaSpecificationExecutor<Privilege> {

    @Override
    Page<Privilege> findAll(Specification<Privilege> specification, Pageable pageable);

    Privilege findByName(String name);
}