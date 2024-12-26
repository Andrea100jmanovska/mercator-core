package aucta.dev.mercator_core.repositories.specifications;

import aucta.dev.mercator_core.models.UserProductHistory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserProductHistorySpecification implements Specification<UserProductHistory> {
    private List<SearchCriteria> list;

    public UserProductHistorySpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<UserProductHistory> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return new SpecificationHelper<UserProductHistory>().resolvePredicate(list, root, query, builder);
    }
}
