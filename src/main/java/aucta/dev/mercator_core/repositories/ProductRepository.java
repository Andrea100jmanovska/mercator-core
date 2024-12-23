package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product> {
    @EntityGraph(attributePaths = {"images"})
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Optional<Product> findById(String productId);
}
