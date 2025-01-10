package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.models.Category;
import aucta.dev.mercator_core.models.OrderedProduct;
import aucta.dev.mercator_core.models.Product;
import aucta.dev.mercator_core.models.dtos.ImageDTO;
import aucta.dev.mercator_core.models.dtos.ProductDTO;
import aucta.dev.mercator_core.repositories.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void initializeCategories() {
        if (categoryRepository.count() == 0) {
            for (CategoryType type : CategoryType.values()) {
                Category category = new Category();
                category.setCategoryType(type);
                category.setName(type.name().replace("_", " "));
                categoryRepository.save(category);
            }
        }
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllPublic() {
        return categoryRepository.findAll();
    }

}