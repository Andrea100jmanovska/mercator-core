package aucta.dev.mercator_core.repositories.specifications;

import aucta.dev.mercator_core.models.Group;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class GroupSpecification implements Specification<Group> {
    private List<SearchCriteria> list;

    public GroupSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<Group> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return new SpecificationHelper<Group>().resolvePredicate(list, root, query, builder);
    }
}
