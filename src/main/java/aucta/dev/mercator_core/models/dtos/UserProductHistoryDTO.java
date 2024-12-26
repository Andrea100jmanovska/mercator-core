package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.UserProductHistory;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserProductHistoryDTO {

    private Long id;

    private String userId;

    private String productId;

    private Date dateAccessed;

    private Product product;

    public UserProductHistoryDTO(UserProductHistory userProductHistory, User currentUser) {
        this.id = userProductHistory.getId();
        this.userId = userProductHistory.getUser().getId();
        this.productId = userProductHistory.getProduct().getId();
        this.dateAccessed = userProductHistory.getDateAccessed();
        //this.product = userProductHistory.getProduct();
    }
}
