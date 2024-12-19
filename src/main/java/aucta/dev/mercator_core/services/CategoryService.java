package aucta.dev.mercator_core.services;

import aucta.dev.mercator_core.enums.CategoryType;
import aucta.dev.mercator_core.models.Category;
import aucta.dev.mercator_core.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

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
                category.setName(type.name().replace("_", " ").toLowerCase());
                categoryRepository.save(category);
            }
        }
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

}