package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.services.UserProductHistoryService;
import aucta.dev.mercator_core.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

@RestController
@RequestMapping(path = "/historyProduct")
public class UserProductHistoryController {

    @Autowired
    private UserProductHistoryService userProductHistoryService;
    @Autowired
    private UserService userService;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.GET, path = "/productAccess")
    public ResponseEntity getUserProductHistory(
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "size") Integer size,
            @RequestParam(value = "orderBy") String orderBy,
            @RequestParam(value = "orderDirection") String orderDirection
    ) {
        Sort sort;
        if (orderBy != null && orderDirection != null) {
            sort = Sort.by(orderDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC, orderBy);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateCreated");
        }
        return ResponseEntity.ok(userProductHistoryService.getUserHistoryProducts(PageRequest.of(page, size, sort)));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.GET, path = "/productAccessWithoutPaging")
    public ResponseEntity getUserProductHistoryWithoutPaging(
            @RequestParam(value = "searchParams") String searchParams,
            @RequestParam(value = "topValuesOnly", required = false) Boolean topValuesOnly
    ) throws IOException, ParseException {
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap filterMap = objectMapper.readValue(searchParams, HashMap.class);
        return ResponseEntity.ok(userProductHistoryService.getUserHistoryProductsAccessWithoutPaging(filterMap, topValuesOnly));
    }

}
