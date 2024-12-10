package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group> {
    Group findByName(String name);
    Group findByCode(String code);
    Page<Group> findAll(Specification<Group> specification, Pageable pageable);

}