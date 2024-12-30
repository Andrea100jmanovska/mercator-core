package aucta.dev.mercator_core.repositories.specifications;

import aucta.dev.mercator_core.models.OrderedProduct;
import aucta.dev.mercator_core.models.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderedProductSpecification implements Specification<OrderedProduct> {

    private List<SearchCriteria> list;

    public OrderedProductSpecification() {
        this.list = new ArrayList<>();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<OrderedProduct> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        return new SpecificationHelper<OrderedProduct>().resolvePredicate(list, root, query, builder);
    }
}