package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(path = "/carts")
public class CartController {

    @Autowired
    private CartService cartService;


    @Secured({"ROLE_ADMINISTRATION","ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @PutMapping("/addToCart")
    public ResponseEntity addToCart(@RequestParam(value = "productId") String productId) throws Exception {
        return ResponseEntity.ok(cartService.addToCart( productId));

    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @PutMapping("/removeFromCart")
    public ResponseEntity removeFromCart(@RequestParam(value = "productId") String productId) throws Exception {
        return ResponseEntity.ok(cartService.removeFromCart(productId));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @GetMapping("/getCartProducts")
    public ResponseEntity getCartProducts() throws Exception {
        try {
            return ResponseEntity.ok(cartService.getCartProducts());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching products from cart: " + e.getMessage());
        }
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/getCartProductsPageable",method = RequestMethod.GET)
    public ResponseEntity getCartProductsPageable(
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
        return ResponseEntity.ok(cartService.getCartProductsPageable(filterMap, PageRequest.of(page, size, sort)));
    }
}
