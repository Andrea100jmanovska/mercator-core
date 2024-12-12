package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.services.ProductService;
import aucta.dev.mercator_core.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection,
            @RequestParam(value = "searchParams") String searchParams
    ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(productService.all(filterMap, PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_ASTA_ADRIA_AGENT"})
    @RequestMapping(path = "/all",method = RequestMethod.GET)
    public ResponseEntity getAllProductsWithoutPaging() throws IOException {
        return ResponseEntity.ok(productService.getAll());
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getProduct(@PathVariable(value = "id") String id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> createProduct(
                                                @RequestParam("name") String name,
                                                @RequestParam("description") String description,
                                                @RequestParam("price") Double price,
                                                @RequestParam("discount") Integer discount,
                                                @RequestParam("quantity") Integer quantity,
                                                @RequestParam("category") CategoryType category,
                                                @RequestParam("deliveryPrice") Double deliveryPrice,
                                                @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setDiscount(discount);
            product.setQuantity(quantity);
            product.setCategory(category);
            product.setDeliveryPrice(deliveryPrice);
            product.setTotalPrice((1-(product.getDiscount()/100.00)) * product.getPrice() + product.getDeliveryPrice());
            product.setUser(userService.getCurrentUser());
            if (image != null) {
                byte[] imageBytes = image.getBytes();
                product.setImage(imageBytes);
            }

            productService.createProduct(product);
            return ResponseEntity.ok("Product successfully created");

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error processing the image");
        }
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity update(@RequestBody Product product) throws BadRequestError {
       // validator.validateUpdate(bank);
        return ResponseEntity.ok(productService.update(product));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(value = "id") String id) throws BadRequestError {
        //validator.validateDelete(id);
        return ResponseEntity.ok(productService.delete(id));
    }

}
