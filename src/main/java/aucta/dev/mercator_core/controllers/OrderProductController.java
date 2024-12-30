package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.enums.OrderStatus;
import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.OrderedProduct;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.dtos.OrderProductDTO;
import aucta.dev.mercator_core.models.dtos.OrderedProductResponseDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.OrderedProductRepository;
import aucta.dev.mercator_core.services.OrderedProductService;
import aucta.dev.mercator_core.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderProductController {

    @Autowired
    private OrderedProductService orderedProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderedProductRepository orderedProductRepository;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<OrderedProductResponseDTO> placeOrder(
            @RequestBody List<OrderProductDTO> products,
            @RequestParam Double totalAmount) {
        return ResponseEntity.ok(orderedProductService.placeOrder(products, totalAmount));
    }

    @GetMapping("/products")
    public ResponseEntity getUserOrderedProducts() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(orderedProductService.getOrderedProductsByUser(currentUser));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/productsPageable",method = RequestMethod.GET)
    public ResponseEntity getUserOrderedProductsPageable(
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
        return ResponseEntity.ok(orderedProductService.getOrderedProductsByUserPageable(filterMap, PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/allOrders",method = RequestMethod.GET)
    public ResponseEntity getAllOrdersPageable(
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
        return ResponseEntity.ok(orderedProductService.getAllOrdersPageable(filterMap, PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderedProductResponseDTO> getDTOProduct(@PathVariable(value = "id") Long id) throws Exception {
        return ResponseEntity.ok(orderedProductService.getByDTOId(id));
    }



}
