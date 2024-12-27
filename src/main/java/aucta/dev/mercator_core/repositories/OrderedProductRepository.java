package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.OrderedProduct;
import aucta.dev.mercator_core.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrderedProductRepository extends JpaRepository<OrderedProduct, Long>, JpaSpecificationExecutor<OrderedProduct> {

    List<OrderedProduct> findByUser(User user);

}
