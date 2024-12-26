package aucta.dev.mercator_core.validators;

import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    @Autowired
    ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    public void createProductValidation(Product product) throws BadRequestError{

        if (product == null)
            throw new BadRequestError("Product must not be null");

        if (product.getName() == null || product.getName().trim().isEmpty())
            throw new BadRequestError("Name is required field");

        if (product.getDescription() == null || product.getDescription().trim().isEmpty())
            throw new BadRequestError("Description is required field");

        if (product.getPrice() == null || product.getPrice().isNaN() || product.getPrice()< 0)
            throw new BadRequestError("Price is required field");

        if (product.getDeliveryPrice() == null || product.getDeliveryPrice().isNaN() || product.getDeliveryPrice()<0)
            throw new BadRequestError("Delivery Price is required field");

        if (product.getDiscount() == null || product.getDiscount() < 0 || product.getDiscount() > 100)
            throw new BadRequestError("Discount must be between 0-100");

        if (product.getCategory().getCategoryType().name().isEmpty())
            throw new BadRequestError("Category is required field");

        if (product.getImages() == null || product.getImages().isEmpty())
            throw new BadRequestError("Image is required field");

    }

    public void updateProductValidation(Product product) throws BadRequestError {

        if (productRepository.findById(product.getId()).orElse(null) == null)
            throw new BadRequestError("No such product!");

        if (product.getName() == null || product.getName().trim().isEmpty())
            throw new BadRequestError("Name is a required field");

        if (product.getDescription() == null || product.getDescription().trim().isEmpty())
            throw new BadRequestError("Description is a required field");

        if (product.getPrice() == null || product.getPrice().isNaN() || product.getPrice() < 0)
            throw new BadRequestError("Price is a required field");

        if (product.getDeliveryPrice() == null || product.getDeliveryPrice().isNaN() || product.getDeliveryPrice() < 0)
            throw new BadRequestError("Delivery Price is a required field");

        if (product.getDiscount() == null || product.getDiscount() < 0 || product.getDiscount() > 100)
            throw new BadRequestError("Discount must be between 0 and 100");

        if (product.getCategory().getCategoryType().name().isEmpty())
            throw new BadRequestError("Category is a required field");
    }

    public void validateProductDelete(String productId) throws BadRequestError {
        if (productRepository.findById(productId).orElse(null) == null)
            throw new BadRequestError("No such product!");
    }
}
