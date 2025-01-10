package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.services.ProductService;
import aucta.dev.mercator_core.validators.ProductValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductValidator productValidator;

    @Autowired
    private ProductRepository productRepository;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection,
            @RequestParam(value = "searchParams") String searchParams
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(productService.getAll(filterMap, PageRequest.of(page, size, sort)));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/mercator")
    public ResponseEntity getAllPublic(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection,
            @RequestParam(value = "searchParams") String searchParams
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(productService.getAllPublic(filterMap, PageRequest.of(page, size, sort)));
    }

    @GetMapping("/category")
    public ResponseEntity getProductsByCategory(@RequestParam(value = "page") Integer page,
                                               @RequestParam(value = "size") Integer size,
                                               @RequestParam(value = "orderBy") String orderBy,
                                               @RequestParam(value = "orderDirection") String orderDirection,
                                               @RequestParam(value = "searchParams") String searchParams,
                                                @RequestParam Long categoryId
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(productService.getProductsByCategory(filterMap, PageRequest.of(page, size, sort), categoryId));

    }


    @Secured({"ROLE_ADMINISTRATION", "ROLE_MERCATOR_AGENT", "ROLE_CLIENT"})
    @RequestMapping(path = "/all",method = RequestMethod.GET)
    public ResponseEntity getAllProductsWithoutPaging() throws IOException {
        return ResponseEntity.ok(productService.getAll());
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProductDTO> getDTOProduct(@PathVariable(value = "id") String id) throws Exception {
        return ResponseEntity.ok(productService.getByDTOId(id));
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createProduct(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("discount") Integer discount,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("category") CategoryType categoryType,
            @RequestParam("deliveryPrice") Double deliveryPrice,
            @RequestParam("images") List<MultipartFile> images
    ) throws BadRequestError, IOException {

            Product product = productService.createProduct(name, description, price, discount, quantity, categoryType, deliveryPrice, images);
            productValidator.createProductValidation(product);
            return ResponseEntity.ok(productRepository.save(product));

    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<ProductDTO> update(
            @RequestParam("id") String id,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") Double price,
            @RequestParam("discount") Integer discount,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("category") CategoryType categoryType,
            @RequestParam("deliveryPrice") Double deliveryPrice,
            @RequestParam(value = "images", required = false) List<MultipartFile> images
    ) throws Exception {

        Product product = productService.getById(id);
        productValidator.updateProductValidation(product);

        Product updatedProduct = productService.update(product, name, description, price, discount, quantity, categoryType, deliveryPrice, images);
        ProductDTO dto = productService.convertToDto(updatedProduct);

        return ResponseEntity.ok(dto);
    }


    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable(value = "id") String id) throws BadRequestError {
        productValidator.validateProductDelete(id);
        return ResponseEntity.ok(productService.delete(id));
    }

}
