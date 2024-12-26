package aucta.dev.mercator_core.models.dtos;

import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.UserProductHistory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserProductHistoryDTO {

    private Long id;

    private String userId;

    private String productId;

    private Date dateAccessed;

    private ProductDTO product;

    public UserProductHistoryDTO() {
    }

    public UserProductHistoryDTO(UserProductHistory userProductHistory, User currentUser) {
        this.id = userProductHistory.getId();
        this.userId = userProductHistory.getUser().getId();
        this.productId = userProductHistory.getProduct().getId();
        this.dateAccessed = userProductHistory.getDateAccessed();
        Product product = userProductHistory.getProduct();
        this.product = mapProductToProductDTO(product, currentUser);
    }

    private ProductDTO mapProductToProductDTO(Product product, User currentUser) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtils.copyProperties(product, productDTO);

        productDTO.setImages(
                product.getImages().stream()
                        .map(image -> {
                            ImageDTO imageDTO = new ImageDTO();
                            imageDTO.setId(image.getId());
                            imageDTO.setImageData(image.getImageData());
                            return imageDTO;
                        })
                        .collect(Collectors.toList())
        );

        return productDTO;
    }
}
