package aucta.dev.mercator_core.validators;

import aucta.dev.mercator_core.exceptions.BadRequestError;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class ProductValidator {

    @Autowired
    ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    public void createProductValidation(ProductDTO productDTO) throws BadRequestError{

        if (productDTO == null)
            throw new BadRequestError("Product must not be null");

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty())
            throw new BadRequestError("Name is required field");

        if (productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty())
            throw new BadRequestError("Description is required field");

        if (productDTO.getPrice() == null || productDTO.getPrice().isNaN() || productDTO.getPrice()< 0)
            throw new BadRequestError("Price is required field");

        if (productDTO.getDeliveryPrice() == null || productDTO.getDeliveryPrice().isNaN() || productDTO.getDeliveryPrice()<0)
            throw new BadRequestError("Delivery Price is required field");

        if (productDTO.getDiscount() == null || productDTO.getDiscount() < 0 || productDTO.getDiscount() > 100)
            throw new BadRequestError("Discount must be between 0-100");

        if (productDTO.getCategory() == null )
            throw new BadRequestError("Category is required field");

        if (productDTO.getImages() == null || productDTO.getImages().isEmpty())
            throw new BadRequestError("Image is required field");

    }

    public void updateProductValidation(ProductDTO productDTO) throws BadRequestError {

        // Validate product fields
        if (productRepository.findById(productDTO.getId()).orElse(null) == null)
            throw new BadRequestError("No such product!");

        if (productDTO.getName() == null || productDTO.getName().trim().isEmpty())
            throw new BadRequestError("Name is a required field");

        if (productDTO.getDescription() == null || productDTO.getDescription().trim().isEmpty())
            throw new BadRequestError("Description is a required field");

        if (productDTO.getPrice() == null || productDTO.getPrice().isNaN() || productDTO.getPrice() < 0)
            throw new BadRequestError("Price is a required field");

        if (productDTO.getDeliveryPrice() == null || productDTO.getDeliveryPrice().isNaN() || productDTO.getDeliveryPrice() < 0)
            throw new BadRequestError("Delivery Price is a required field");

        if (productDTO.getDiscount() == null || productDTO.getDiscount() < 0 || productDTO.getDiscount() > 100)
            throw new BadRequestError("Discount must be between 0 and 100");

        if (productDTO.getCategory() == null)
            throw new BadRequestError("Category is a required field");


    }
}
