package category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository = new CategoryRepository();

    public List<CategoryDTO> getAllCategories() throws SQLException {
        return categoryRepository.findAll();
    }

    public CategoryDTO getCategoryById(int id) throws SQLException {
        CategoryDTO category = categoryRepository.findById(id);
        if (category == null) {
            throw new CategoryNotFoundException(id);
        }
        return category;
    }

    public List<String> getBooksByCategory(int categoryId) throws SQLException {
        if (!categoryRepository.exists(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        return categoryRepository.findBooksByCategory(categoryId);
    }

    public void addCategory(String name, String description) throws SQLException {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Kategorinamn får inte vara tomt.");
        }
        categoryRepository.create(name.trim(), description.trim());
    }

    public void updateCategory(int id, String name, String description) throws SQLException {
        if (!categoryRepository.exists(id)) {
            throw new CategoryNotFoundException(id);
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Kategorinamn får inte vara tomt.");
        }
        categoryRepository.update(id, name.trim(), description.trim());
    }

    public void deleteCategory(int id) throws SQLException {
        if (!categoryRepository.exists(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.delete(id);
    }
}
