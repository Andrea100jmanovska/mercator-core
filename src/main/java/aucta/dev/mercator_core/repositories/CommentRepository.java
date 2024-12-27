package aucta.dev.mercator_core.repositories;

import aucta.dev.mercator_core.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    List<Comment> findAllByProductIdOrderByCreatedAtDesc(String productId);


}
