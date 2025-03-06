package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    Optional<Category> findByCategoryType(CategoryType categoryType);

}

