package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.*;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.*;
import aucta.dev.mercator_core.models.dtos.*;
import aucta.dev.mercator_core.repositories.MailTemplateRepository;
import aucta.dev.mercator_core.repositories.OrderedProductRepository;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.repositories.UserRepository;
import aucta.dev.mercator_core.repositories.specifications.OrderedProductSpecification;
import aucta.dev.mercator_core.repositories.specifications.ProductSpecification;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import aucta.dev.mercator_core.utils.BasicSystemSettingsProps;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    MailMessageService mailMessageService;

    @Autowired
    SystemSettingsService systemSettingsService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MailTemplateRepository mailTemplateRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

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
        OrderedProductSpecification orderedProductSpecification = new OrderedProductSpecification();

        // Add base specification for current user
        User currentUser = userService.getCurrentUser();
        orderedProductSpecification.add(new SearchCriteria("user", currentUser, SearchOperation.EQUAL));

        // Handle other search parameters
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

                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                } else if (entry.getKey().equals("id")) {
                    Long idValue = Long.parseLong(entry.getValue());
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), idValue, SearchOperation.EQUAL));
                } else {
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
            }
        }

        List<OrderedProduct> orderedProducts = orderedProductRepository.findAll(orderedProductSpecification);

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
        OrderedProductSpecification orderedProductSpecification = new OrderedProductSpecification();
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

                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), from.getTime(), SearchOperation.JAVA_UTIL_DATE_GREATER_THAN_EQUAL));
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), to.getTime(), SearchOperation.JAVA_UTIL_DATE_LESS_THAN_EQUAL));
                }else if (entry.getKey().equals("id")) {
                    Long idValue = Long.parseLong(entry.getValue());
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), idValue, SearchOperation.EQUAL));
                } else {
                    orderedProductSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
                }
            }
        }

        //Page<Product> productsPage = productRepository.findAll(productSpecification, pageable);
        List<OrderedProduct> orderedProducts = orderedProductRepository.findAll(orderedProductSpecification);

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

        UserDTO userDto = new UserDTO();
        BeanUtils.copyProperties(orderedProduct.getUser(), userDto);
        dto.setUserDTO(userDto);

        return dto;
    }

    public OrderedProductResponseDTO update(OrderedProduct orderedProduct, String email) {

        OrderedProduct existingOrder = orderedProductRepository.findById(orderedProduct.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        User user = userRepository.findFirstByEmail(email);
        OrderStatus oldStatus = existingOrder.getStatus();
        existingOrder.setStatus(orderedProduct.getStatus());
        orderedProductRepository.save(existingOrder);
        OrderStatus newStatus = existingOrder.getStatus();
        MailMessage mailMessage = new MailMessage();
        mailMessage.setSender(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
        mailMessage.setReceivers(List.of(user.getEmail()));
        mailMessage.setStatus(MailMessageStatus.PENDING);
        HashMap<String, String> params = new HashMap<>();
        params.put("subject", "Order Status Changed");
        params.put("user", user.getFirstName() + " " + user.getLastName());
        params.put("orderId", existingOrder.getId().toString());
        params.put("oldStatus", oldStatus.name());
        params.put("newStatus", newStatus.name());
        //params.put("link",  BASE_URL + "/activate-user?token=" + encodedToken);

        MailTemplate mailTemplate = mailTemplateRepository.findFirstByTemplateType(MailTemplateType.MAIL_TEMPLATE_ORDER_STATUS_CHANGE);
        String mailContent = mailTemplate.getContent();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            mailContent = mailContent.replace(placeholder, entry.getValue());
        }

        if (sendEmailMessage(email, params.get("subject"), mailContent)) {
            mailMessage.setStatus(MailMessageStatus.SUCCESS);
        } else {
            mailMessage.setStatus(MailMessageStatus.FAILED);
        }
        mailMessageService.save(mailMessage, MailTemplateType.MAIL_TEMPLATE_ORDER_STATUS_CHANGE, params);

        OrderedProductResponseDTO dto = new OrderedProductResponseDTO();
        BeanUtils.copyProperties(existingOrder, dto);
        return dto;
    }

    public boolean sendEmailMessage(String to, String subject, String text) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(systemSettingsService.getSystemSettingsPropByKey(BasicSystemSettingsProps.SMTP_USERNAME).getValue());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

}
