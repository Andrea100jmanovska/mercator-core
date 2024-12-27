package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.models.Cart;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.User;
import aucta.dev.mercator_core.models.dtos.ImageDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.CartRepository;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Transactional
    public ResponseEntity addToCart(String productId) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userService.getUser().getId());
        if(optionalUser.isEmpty()) throw new Error("User not found!");
        User user = optionalUser.get();
        Product product = productRepository.getById(productId);

        if(product == null) throw new Error("Product not found!");

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });
        cart.addProduct(product);

        cartRepository.save(cart);

        return ResponseEntity.ok("Product added to cart!");

    }

    @Transactional
    public ResponseEntity removeFromCart(String productId) throws Exception {

        Optional<User> optionalUser = userRepository.findById(userService.getUser().getId());
        if (optionalUser.isEmpty()) throw new Error("User not found!");
        User user = optionalUser.get();

        Product product = productRepository.findById(productId).orElseThrow(() -> new Error("Product not found!"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new Error("Cart not found!"));

        boolean removed = cart.getCartProducts().removeIf(cartProduct -> cartProduct.equals(product));

        if (removed) {
            cartRepository.save(cart);
            return ResponseEntity.ok("Product removed from cart!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in the cart!");
        }
    }

    @Transactional
    public ResponseEntity emptyCart() throws Exception {
        Optional<User> optionalUser = userRepository.findById(userService.getUser().getId());
        if (optionalUser.isEmpty()) throw new Error("User not found!");
        User user = optionalUser.get();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new Error("Cart not found!"));

        cart.getCartProducts().clear();

        cartRepository.save(cart);

        return ResponseEntity.ok("Cart is empty!");
    }


    @Transactional
    public List<ProductDTO> getCartProducts() throws Exception {
        Optional<User> optionalUser = userRepository.findById(userService.getUser().getId());
        if (optionalUser.isEmpty()) throw new Error("User not found!");
        User user = optionalUser.get();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });

        List<Product> productsInCart = cart.getCartProducts();
        List<ProductDTO> dtos = productsInCart.stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    dto.setIsFavorited(product.getUsers().contains(userService.getCurrentUser()));
                    dto.setIsInCart(productsInCart.contains(product));
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
    public Page<ProductDTO> getCartProductsPageable(Map<String, String> params, Pageable pageable) throws Exception {
        Optional<User> optionalUser = userRepository.findById(userService.getUser().getId());
        if (optionalUser.isEmpty()) throw new Error("User not found!");
        User user = optionalUser.get();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return newCart;
                });

        List<Product> productsInCart = cart.getCartProducts();

        int start = (int) pageable.getOffset();
        int end = (start + pageable.getPageSize()) > productsInCart.size() ? productsInCart.size() : (start + pageable.getPageSize());

        List<Product> pagedProductsInCart = productsInCart.subList(start, end);


        List<ProductDTO> dtos = pagedProductsInCart.stream()
                .map(product -> {
                    ProductDTO dto = new ProductDTO();
                    BeanUtils.copyProperties(product, dto);
                    dto.setIsFavorited(product.getUsers().contains(userService.getCurrentUser()));
                    dto.setIsInCart(productsInCart.contains(product));
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
        return new PageImpl<>(dtos, pageable, productsInCart.size());
    }

}
