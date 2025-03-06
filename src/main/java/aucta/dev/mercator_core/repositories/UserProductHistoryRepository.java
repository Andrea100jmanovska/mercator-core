package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.UserProductHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProductHistoryRepository extends JpaRepository<UserProductHistory, String>, JpaSpecificationExecutor<UserProductHistory> {

    boolean existsByUserAndProduct(User user, Product product);
}
