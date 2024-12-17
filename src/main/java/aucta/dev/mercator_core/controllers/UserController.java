package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.auth.models.EditUserRequest;
import aucta.dev.mercator_core.exceptions.HttpException;
import aucta.dev.mercator_core.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.GET, path = "/getCurrentUser")
    public ResponseEntity getUser() throws Exception {
        return ResponseEntity.ok(userService.getUser());
    }

    @Secured({"ROLE_ADMINISTRATION"})
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection,
            @RequestParam(value = "searchParams") String searchParams
    ) throws IOException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(userService.getAll(filterMap, PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION","ROLE_MERCATOR_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @RequestMapping(path = "/reset_password", method = RequestMethod.PUT)
    public ResponseEntity resetPassword(
            @RequestParam String username,
            @RequestParam String newPassword,
            @RequestParam String oldPassword
    ) throws HttpException {
        if (username.equalsIgnoreCase(userService.getCurrentUser().getUsername())) {
            return ResponseEntity.ok(userService.resetPassword(username, newPassword, oldPassword));
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Secured({"ROLE_ADMINISTRATION","ROLE_MERCATOR_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateUser(@RequestParam(value = "token") String token,
            @RequestBody EditUserRequest request
    ) throws Exception {

        return ResponseEntity.ok(userService.updateUser(token, request));
    }


    @Secured({"ROLE_ADMINISTRATION","ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @RequestMapping(path = "/addProductToFavorites", method = RequestMethod.PUT)
    public ResponseEntity addProductToFavorite(@RequestParam(value = "productId") String productId
    ) throws Exception {
        return ResponseEntity.ok(userService.addProductToFavorites(productId));
    }

    @Secured({"ROLE_ADMINISTRATION","ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @RequestMapping(path = "/removeProductFromFavorites", method = RequestMethod.PUT)
    public ResponseEntity removeProductFromFavorites(@RequestParam(value = "productId") String productId
    ) throws Exception {
        return ResponseEntity.ok(userService.removeProductFromFavorites(productId));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.GET, path = "/myFavoriteProducts")
    public ResponseEntity getMyFavoriteProducts() throws Exception {
        return ResponseEntity.ok(userService.getMyFavoriteProducts());
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(path = "/myFavoriteProductsPageable",method = RequestMethod.GET)
    public ResponseEntity getMyFavoriteProductsPageable(
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
        return ResponseEntity.ok(userService.getMyFavoriteProductsPageable(filterMap, PageRequest.of(page, size, sort)));
    }
}
