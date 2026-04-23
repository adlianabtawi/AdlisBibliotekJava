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

    public void addCategoryToBook(int bookId, int categoryId) throws SQLException {
        if (!categoryRepository.exists(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        try {
            categoryRepository.addCategoryToBook(bookId, categoryId);
        } catch (SQLException e) {
            // Om databasen klagar på främmande nyckel (Foreign Key), betyder det att bok-ID:t inte finns!
            if (e.getMessage().contains("foreign key constraint") || e.getMessage().contains("FOREIGN KEY")) {
                throw new IllegalArgumentException("Kunde inte hitta någon bok med ID: " + bookId);
            }
            // Om det är något annat databasfel, låt det kasta vidare som vanligt
            throw e;
        }
    }

    public void removeCategoryFromBook(int bookId, int categoryId) throws SQLException {
        if (!categoryRepository.exists(categoryId)) {
            throw new CategoryNotFoundException(categoryId);
        }
        categoryRepository.removeCategoryFromBook(bookId, categoryId);
    }
}
