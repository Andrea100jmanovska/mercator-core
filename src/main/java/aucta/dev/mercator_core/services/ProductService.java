package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.SearchOperation;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.repositories.ProductRepository;
import aucta.dev.mercator_core.repositories.specifications.ProductSpecification;
import aucta.dev.mercator_core.repositories.specifications.SearchCriteria;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;



    public Page<Product> all(Map<String, String> params, Pageable pageable) {
        ProductSpecification productSpecification = new ProductSpecification();
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (!StringUtils.isEmpty(entry.getKey()) && !StringUtils.isEmpty(entry.getValue()))
                productSpecification.add(new SearchCriteria(entry.getKey(), entry.getValue(), SearchOperation.MATCH));
            Date date = new Date();

        }
        return productRepository.findAll(productSpecification, pageable);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    public Product getById(String id) {
        Product product = productRepository.getById(id);
        return product;
    }

    public Product update(Product product) {
        return productRepository.save(product);
    }

    public Boolean delete(String id) {
        Product product = productRepository.getById(id);
        productRepository.delete(product);
        return true;
    }
}
