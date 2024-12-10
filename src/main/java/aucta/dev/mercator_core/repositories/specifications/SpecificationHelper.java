package aucta.dev.mercator_core.repositories.specifications;

import aucta.dev.mercator_core.enums.SearchOperation;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpecificationHelper<T> {
    public Predicate resolvePredicate(List<SearchCriteria> list, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();

        //add add criteria to predicates
        for (SearchCriteria criteria : list) {
            String[] keys = criteria.getKey().split("\\.");
            Path<String> path = null;
            Path<Object> pathIn = null;
            Path<LocalDate> pathDate = null;
            Path<LocalDateTime> pathDateTime = null;
            Path<Date> pathJavaUtilDate = null;
                for (int i = 0; i < keys.length; i++) {
                    if (path != null) {
                        path = path.get(keys[i]);
                        pathIn = pathIn.get(keys[i]);
                        pathDate = pathDate.get(keys[i]);
                        pathDateTime = pathDateTime.get(keys[i]);
                        pathJavaUtilDate = pathJavaUtilDate.get(keys[i]);
                    } else {
                        path = root.get(keys[i]);
                        pathIn = root.get(keys[i]);
                        pathDate = root.get(keys[i]);
                        pathDateTime = root.get(keys[i]);
                        pathJavaUtilDate = root.get(keys[i]);
                    }
                }

            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                predicates.add(builder.greaterThan(
                        path, criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                predicates.add(builder.lessThan(
                        path, criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.DATE_GREATER_THAN_EQUAL)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        pathDate, (LocalDate) criteria.getValue()
                ));
            } else if (criteria.getOperation().equals(SearchOperation.DATE_LESS_THAN_EQUAL)) {
                predicates.add(builder.lessThanOrEqualTo(
                        pathDate, (LocalDate) criteria.getValue()
                ));
            } else if (criteria.getOperation().equals(SearchOperation.DATETIME_GREATER_THEN_OR_EQUAL_TO)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        pathDateTime, (LocalDateTime) criteria.getValue()
                ));
            } else if (criteria.getOperation().equals(SearchOperation.DATETIME_LESS_THEN_OR_EQUAL_TO)) {
                predicates.add(builder.lessThanOrEqualTo(
                        pathDateTime, (LocalDateTime) criteria.getValue()
                ));
            } else if (criteria.getOperation().equals(SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL)) {
                predicates.add(builder.lessThanOrEqualTo(pathJavaUtilDate, (Date) criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL)) {
                predicates.add(builder.greaterThanOrEqualTo(pathJavaUtilDate, (Date) criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.JAVA_UTIL_DATE_LESS_THAN)) {
                predicates.add(builder.lessThan(pathJavaUtilDate, (Date) criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.JAVA_UTIL_DATE_GREATER_THAN)) {
                predicates.add(builder.greaterThan(pathJavaUtilDate, (Date) criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                predicates.add(builder.greaterThanOrEqualTo(
                        path, criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                predicates.add(builder.lessThanOrEqualTo(
                        path, criteria.getValue().toString()));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(builder.notEqual(
                        path, criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(builder.equal(
                        path, criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                predicates.add(builder.like(
                        builder.lower(path),
                        "%" + criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                predicates.add(builder.like(
                        builder.lower(path),
                        criteria.getValue().toString().toLowerCase() + "%"));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                predicates.add(builder.like(
                        builder.lower(path),
                        "%" + criteria.getValue().toString().toLowerCase()));
            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                predicates.add(builder.in(pathIn).value(criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                predicates.add(builder.in(pathIn).value(criteria.getValue()).not());
            } else if (criteria.getOperation().equals(SearchOperation.NOT_NULL)) {
                predicates.add(builder.isNotNull(path));
            } else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                predicates.add(builder.isNull(path));
            }
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}