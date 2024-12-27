package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.OrderStatus;
import aucta.dev.mercator_core.models.Cart;
import aucta.dev.mercator_core.models.OrderedProduct;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.dtos.ImageDTO;
import aucta.dev.mercator_core.models.dtos.OrderProductDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.OrderedProductRepository;
import aucta.dev.mercator_core.repositories.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static aucta.dev.mercator_core.auth.AuthUtils.getCurrentUser;

@Service
public class OrderedProductService {

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    public OrderedProduct placeOrder(List<OrderProductDTO> orderProductDTOs, Double totalAmount) {
        User user = userService.getCurrentUser();
        List<Product> products = new ArrayList<>();

        for (OrderProductDTO dto : orderProductDTOs) {
            Product product = productRepository.findById(dto.getProductId()).orElse(null);
            product.setQuantity(dto.getQuantity());
            product.setPrice(dto.getPrice());
            product.setDeliveryPrice(dto.getDeliveryPrice());
            products.add(product);
        }

        OrderedProduct orderedProduct = new OrderedProduct();
        orderedProduct.setUser(user);
        orderedProduct.setOrderDate(LocalDateTime.now());
        orderedProduct.setStatus(OrderStatus.PENDING);
        orderedProduct.setTotalAmount(totalAmount);
        orderedProduct.setProducts(products);

        return orderedProductRepository.save(orderedProduct);
    }

    @Transactional
    public List<ProductDTO> getOrderedProductsByUser(User user) {
        List<OrderedProduct> orderedProducts = orderedProductRepository.findByUser(user);

        List<Product> orderedItems = new ArrayList<>();

        for (OrderedProduct order : orderedProducts) {
            orderedItems.addAll(order.getProducts());
        }

        List<ProductDTO> dtos = orderedItems.stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    dto.setImages(
                            product.getImages().stream()
                                    .map(image -> {
                                        ImageDTO imageDTO = new ImageDTO();
                                        imageDTO.setId(image.getId());
                                        imageDTO.setImageData(image.getImageData());
                                        return imageDTO;
                                    })
                                    .collect(Collectors.toList())
                    );

                    return dto;
                })
                .sorted(Comparator.comparing(ProductDTO::getDateCreated).reversed())
                .collect(Collectors.toList());

        return dtos;
    }

    @Transactional
    public Page<ProductDTO> getOrderedProductsByUserPageable(Map<String, String> params, Pageable pageable) throws Exception {

        List<OrderedProduct> orderedProducts = orderedProductRepository.findByUser(userService.getCurrentUser());

        List<ProductDTO> dtos = new ArrayList<>();

        for (OrderedProduct order : orderedProducts) {
            for (Product product : order.getProducts()) {
                ProductDTO dto = new ProductDTO();
                BeanUtils.copyProperties(product, dto);
                dto.setOrderId(order.getId());
                dto.setImages(
                        product.getImages().stream()
                                .map(image -> {
                                    ImageDTO imageDTO = new ImageDTO();
                                    imageDTO.setId(image.getId());
                                    imageDTO.setImageData(image.getImageData());
                                    return imageDTO;
                                })
                                .collect(Collectors.toList())
                );

                dtos.add(dto);
            }
        }

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        List<ProductDTO> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

}
