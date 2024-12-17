package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Image;
import aucta.dev.mercator_core.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {
    void deleteByProduct(Product product);

}
