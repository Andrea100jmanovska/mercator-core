package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.enums.ColorType;
import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.models.dtos.ImageDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.*;
import aucta.dev.mercator_core.repositories.specifications.ProductSpecification;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserProductHistoryService userProductHistoryService;

    @Transactional
    public Page<ProductDTO> getAll(Map<String, String> params, Pageable pageable) throws Exception {
        ProductSpecification productSpecification = new ProductSpecification();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!org.springframework.util.StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                if (entry.getKey().equals("dateCreated")) {
                    Calendar from = Calendar.getInstance();
                    from.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                    from.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    Calendar to = Calendar.getInstance();
                    to.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    to.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    to.set(Calendar.HOUR_OF_DAY, 0);
                    to.set(Calendar.MINUTE, 0);
                    to.set(Calendar.SECOND, 0);
                    to.set(Calendar.MILLISECOND, 0);
                    to.add(Calendar.HOUR, 23);
                    to.add(Calendar.MINUTE, 59);
                    to.add(Calendar.SECOND, 59);

                    productSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    productSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                } else {
                    productSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
            }
        }

        Cart cart = cartRepository.findByUserId(userService.getUser().getId()).orElse(null);
        Page<Product> productsPage = productRepository.findAll(productSpecification, pageable);

        List<Product> productsInCart = cart != null ? cart.getCartProducts() : new ArrayList<>();

        List<ProductDTO> dtos = productsPage.getContent().stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    dto.setIsFavorited(product.getUsers().contains(userService.getCurrentUser()));
                    dto.setIsInCart(productsInCart.contains(product));
                    dto.setAverageRating(product.getAverageRating());

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

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional
    public Page<ProductDTO> getAllPublic(Map<String, String> params, Pageable pageable) throws Exception {
        ProductSpecification productSpecification = new ProductSpecification();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!org.springframework.util.StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                if (entry.getKey().equals("dateCreated")) {
                    Calendar from = Calendar.getInstance();
                    from.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                    from.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    Calendar to = Calendar.getInstance();
                    to.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    to.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    to.set(Calendar.HOUR_OF_DAY, 0);
                    to.set(Calendar.MINUTE, 0);
                    to.set(Calendar.SECOND, 0);
                    to.set(Calendar.MILLISECOND, 0);
                    to.add(Calendar.HOUR, 23);
                    to.add(Calendar.MINUTE, 59);
                    to.add(Calendar.SECOND, 59);

                    productSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    productSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                } else {
                    productSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
            }
        }

        Page<Product> productsPage = productRepository.findAll(productSpecification, pageable);

        List<ProductDTO> dtos = productsPage.getContent().stream()
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

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional
    public Page<ProductDTO> getProductsByCategory(Map<String, String> params, Pageable pageable, Long categoryId) throws Exception {
        ProductSpecification productSpecification = new ProductSpecification();

        if (categoryId != null) {
            productSpecification.add(new SearchCriteria("category.id", categoryId, SearchOperation.EQUAL));
        }

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!org.springframework.util.StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue())) {
                if (entry.getKey().equals("dateCreated")) {
                    Calendar from = Calendar.getInstance();
                    from.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                    from.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    from.set(Calendar.HOUR_OF_DAY, 0);
                    from.set(Calendar.MINUTE, 0);
                    from.set(Calendar.SECOND, 0);
                    from.set(Calendar.MILLISECOND, 0);

                    Calendar to = Calendar.getInstance();
                    to.setTimeZone(TimeZone.getTimeZone("Europe/Skopje"));
                    to.setTime(formatter.parse(String.valueOf(entry.getValue())));
                    to.set(Calendar.HOUR_OF_DAY, 0);
                    to.set(Calendar.MINUTE, 0);
                    to.set(Calendar.SECOND, 0);
                    to.set(Calendar.MILLISECOND, 0);
                    to.add(Calendar.HOUR, 23);
                    to.add(Calendar.MINUTE, 59);
                    to.add(Calendar.SECOND, 59);

                    productSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    productSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                } else {
                    productSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
            }
        }

        Cart cart = cartRepository.findByUserId(userService.getUser().getId()).orElse(null);
        Page<Product> productsPage = productRepository.findAll(productSpecification, pageable);

        List<Product> productsInCart = cart != null ? cart.getCartProducts() : new ArrayList<>();

        List<ProductDTO> dtos = productsPage.getContent().stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    dto.setIsFavorited(product.getUsers().contains(userService.getCurrentUser()));
                    dto.setIsInCart(productsInCart.contains(product));
                    dto.setAverageRating(product.getAverageRating());

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

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }


    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional
    public Product createProduct( String name,
                                 String description,
                                 Double price,
                                 Integer discount,
                                 String size,
                                 Integer quantity,
                                 CategoryType categoryType,
                                 Double deliveryPrice,
                                 List<MultipartFile> images, List<ColorType> colors) throws BadRequestError, IOException {
        Category category = categoryRepository.findByCategoryType(categoryType)
                .orElseThrow(() -> new BadRequestError("Invalid category"));

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setQuantity(quantity);
        product.setSize(size);
        product.setCategory(category);
        product.setDeliveryPrice(deliveryPrice);
        product.setTotalPrice((1 - (product.getDiscount() / 100.00)) * product.getPrice() + product.getDeliveryPrice());
        product.setUser(userService.getCurrentUser());
        if (colors != null && !colors.isEmpty()) {
            product.setColors(colors);
        }

        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                byte[] imageBytes = image.getBytes();
                Image productImage = new Image();
                productImage.setImageData(imageBytes);
                productImage.setProduct(product);

                product.getImages().add(productImage);
            }
        }
        return product;
    }

    public Product getById(String id) throws BadRequestError {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null){
            throw new BadRequestError("Newsletter Post not found!");
        }
        return product;
    }

    @Transactional
    public ProductDTO getByDTOId(String id) throws Exception {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
       Cart cart = cartRepository.findByUserId(userService.getUser().getId()).orElse(null);
        List<Product> productsInCart = cart != null ? cart.getCartProducts() : new ArrayList<>();

        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        dto.setIsFavorited(product.getUsers().contains(userService.getCurrentUser()));
        dto.setIsInCart(productsInCart.contains(product));
        List<ImageDTO> imageDTOs = product.getImages().stream()
                .map(image -> {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(image.getId());
                    imageDTO.setImageData(image.getImageData());
                    return imageDTO;

                })
                .collect(Collectors.toList());

        dto.setImages(imageDTOs);
        userProductHistoryService.logProductHistory(product);
        return dto;
    }

    public ProductDTO convertToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);

        List<ImageDTO> imageDTOs = product.getImages().stream()
                .map(image -> {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(image.getId());
                    imageDTO.setImageData(image.getImageData());
                    return imageDTO;
                })
                .collect(Collectors.toList());

        dto.setImages(imageDTOs);
        return dto;
    }


    @Transactional
    public Product update(Product product,
                          String name,
                          String description,
                          Double price,
                          Integer discount,
                          Integer quantity,
                          String size,
                          CategoryType categoryType,
                          Double deliveryPrice,
                          List<MultipartFile> images, List<ColorType> colors) throws IOException, BadRequestError {

        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        Category category = categoryRepository.findByCategoryType(categoryType)
                .orElseThrow(() -> new BadRequestError("Invalid category"));

        existingProduct.setName(name);
        existingProduct.setDescription(description);
        existingProduct.setPrice(price);
        existingProduct.setDiscount(discount);
        existingProduct.setQuantity(quantity);
        existingProduct.setCategory(category);
        existingProduct.setSize(size);
        existingProduct.setDeliveryPrice(deliveryPrice);
        if (colors != null && !colors.isEmpty()) {
            existingProduct.setColors(colors);
        }
        existingProduct.setTotalPrice(
                (1 - (product.getDiscount() / 100.00)) * product.getPrice() + product.getDeliveryPrice()
        );


        if (images != null && !images.isEmpty()) {

            imageRepository.deleteByProduct(existingProduct);

            existingProduct.getImages().clear();
            existingProduct.setImages(new ArrayList<>());
        }

        if(images != null && !images.isEmpty()) {
            imageRepository.deleteByProduct(existingProduct);
            existingProduct.getImages().clear();
            List<Image> productImages = new ArrayList<>();
            for (MultipartFile imageFile : images) {
                Image image = new Image();
                image.setImageData(imageFile.getBytes());
                image.setProduct(existingProduct);
                productImages.add(image);
            }
            existingProduct.getImages().addAll(productImages);
        }

        return productRepository.save(existingProduct);
    }

    public Boolean delete(String id) {
        Product product = productRepository.getById(id);
        productRepository.delete(product);
        return true;
    }
}
