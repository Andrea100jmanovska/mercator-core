package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {

    private Long id;

    private String content;

    private LocalDateTime createdAt;

    private String productId;

    private String userId;

    private String userName;
}
