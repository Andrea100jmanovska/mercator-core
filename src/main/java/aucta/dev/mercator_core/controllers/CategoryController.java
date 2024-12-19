package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(path = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_MERCATOR_AGENT", "ROLE_CLIENT"})
    @RequestMapping(path = "/all",method = RequestMethod.GET)
    public ResponseEntity getAll() throws IOException {
        return ResponseEntity.ok(categoryService.getAll());
    }
}