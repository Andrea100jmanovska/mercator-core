package aucta.dev.mercator_core.controllers;

import aucta.dev.mercator_core.models.Comment;
import aucta.dev.mercator_core.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CLIENT"})
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody Comment comment, @RequestParam String productId) {
        //validator.validateCreate(bank);
        return ResponseEntity.ok(commentService.create(comment, productId));
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_CAPITELIZE_AGENT", "ROLE_CLIENT_ADMIN", "ROLE_CLIENT"})
    @GetMapping("/getComments")
    public ResponseEntity getComments(@RequestParam String productId) throws Exception {
        try {
            return ResponseEntity.ok(commentService.getCommentsByProduct(productId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching comment for product: " + e.getMessage());
        }
    }

    @Secured({"ROLE_ADMINISTRATION", "ROLE_MERCATOR_AGENT", "ROLE_CLIENT"})
    @RequestMapping(path = "/all",method = RequestMethod.GET)
    public List<Comment> getAll() throws IOException {
        return commentService.getAll();
    }

}
