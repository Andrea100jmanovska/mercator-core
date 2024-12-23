package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.models.dtos.CommentDTO;
import aucta.dev.mercator_core.models.dtos.ImageDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.CommentRepository;
import aucta.dev.mercator_core.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductRepository productRepository;

    public Comment create(Comment comment, String productId) {
        User user = userService.getCurrentUser();
        comment.setUser(user);
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            comment.setProduct(product.get());
        }
        comment.setCreatedAt(LocalDateTime.now());
        comment.setRating(comment.getRating());
        return commentRepository.save(comment);
    }

    public List<CommentDTO> getCommentsByProduct(String productId) throws Exception {

      Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new Exception("Product not found!");
        }
        Product product = optionalProduct.get();

        List<Comment> comments = commentRepository.findAllByProductIdOrderByCreatedAtDesc(productId);
        double averageRating = comments.stream()
                .mapToDouble(Comment::getRating)
                .average()
                .orElse(0.0);
        product.setAverageRating(averageRating);
        productRepository.save(product);


        return comments.stream()
                .map(comment -> {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(comment.getId());
                    dto.setContent(comment.getContent());
                    dto.setCreatedAt(comment.getCreatedAt());
                    dto.setProductId(comment.getProduct().getId());
                    dto.setRating(comment.getRating());
                    if (comment.getUser() != null) {
                        dto.setUserId(comment.getUser().getId());
                        dto.setUserName(comment.getUser().getDisplayName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<Comment> getAll() {
        return commentRepository.findAll();
    }
}
