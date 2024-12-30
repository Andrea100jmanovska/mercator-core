package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.enums.OrderStatus;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.models.dtos.*;
import aucta.dev.mercator_core.repositories.OrderedProductRepository;
import aucta.dev.mercator_core.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class OrderedProductService {

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public OrderedProductResponseDTO placeOrder(List<OrderProductDTO> orderProductDTOs, Double totalAmount) {
        User user = userService.getCurrentUser();
        List<Product> products = new ArrayList<>();

        for (OrderProductDTO dto : orderProductDTOs) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Product not found"));
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

        OrderedProduct savedOrder = orderedProductRepository.save(orderedProduct);
        return convertToDTO(savedOrder);
    }

    private OrderedProductResponseDTO convertToDTO(OrderedProduct order) {
        OrderedProductResponseDTO dto = new OrderedProductResponseDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        List<ProductResponseDTO> productDTOs = order.getProducts().stream()
                .map(this::convertProductToDTO)
                .collect(Collectors.toList());

        dto.setProducts(productDTOs);
        return dto;
    }

    private ProductResponseDTO convertProductToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setDiscount(product.getDiscount());
        dto.setDeliveryPrice(product.getDeliveryPrice());
        dto.setTotalPrice(product.getTotalPrice());
        dto.setAverageRating(product.getAverageRating());
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
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
                dto.setOrderDate(order.getOrderDate());
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
        dtos.sort(Comparator.comparing(ProductDTO::getOrderDate).reversed());

        Collections.reverse(dtos);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        List<ProductDTO> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Transactional
    public Page<ProductDTO> getAllOrdersPageable(Map<String, String> params, Pageable pageable) throws Exception {

        List<OrderedProduct> orderedProducts = orderedProductRepository.findAll();

        List<ProductDTO> dtos = new ArrayList<>();

        for (OrderedProduct order : orderedProducts) {
            for (Product product : order.getProducts()) {
                ProductDTO dto = new ProductDTO();
                BeanUtils.copyProperties(product, dto);
                dto.setOrderId(order.getId());
                dto.setOrderStatus(order.getStatus());
                dto.setOrderDate(order.getOrderDate());
                dto.setOrderEmail(order.getUser().getEmail());
                dto.setOrderFirstName(order.getUser().getFirstName());
                dto.setOrderLastName(order.getUser().getLastName());
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
        dtos.sort(Comparator.comparing(ProductDTO::getOrderDate).reversed());

        Collections.reverse(dtos);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        List<ProductDTO> pagedDtos = dtos.subList(start, end);

        return new PageImpl<>(pagedDtos, pageable, dtos.size());
    }

    @Transactional
    public OrderedProductResponseDTO getByDTOId(Long id) throws Exception {
        OrderedProduct orderedProduct = orderedProductRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        OrderedProductResponseDTO dto = new OrderedProductResponseDTO();
        BeanUtils.copyProperties(orderedProduct, dto);

        return dto;
    }

    public OrderedProductResponseDTO update(OrderedProduct orderedProduct) {

        OrderedProduct existingOrder = orderedProductRepository.findById(orderedProduct.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        existingOrder.setStatus(orderedProduct.getStatus());
        orderedProductRepository.save(existingOrder);

        OrderedProductResponseDTO dto = new OrderedProductResponseDTO();
        BeanUtils.copyProperties(existingOrder, dto);
        return dto;
    }

}
