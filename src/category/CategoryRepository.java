package category;

import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<CategoryDTO> findAll() throws SQLException {
        List<CategoryDTO> categories = new ArrayList<>();
        String sql = """
                SELECT c.id, c.name, c.description,
                       COUNT(bc.book_id) AS book_count
                FROM categories c
                LEFT JOIN book_categories bc ON c.id = bc.category_id
                GROUP BY c.id, c.name, c.description
                ORDER BY c.name
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categories.add(mapToDTO(rs));
            }
        }
        return categories;
    }

    public CategoryDTO findById(int id) throws SQLException {
        String sql = """
                SELECT c.id, c.name, c.description,
                       COUNT(bc.book_id) AS book_count
                FROM categories c
                LEFT JOIN book_categories bc ON c.id = bc.category_id
                WHERE c.id = ?
                GROUP BY c.id, c.name, c.description
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapToDTO(rs);
                }
            }
        }
        return null;
    }

    public List<String> findBooksByCategory(int categoryId) throws SQLException {
        List<String> books = new ArrayList<>();
        String sql = """
                SELECT b.title
                FROM books b
                JOIN book_categories bc ON b.id = bc.book_id
                WHERE bc.category_id = ?
                ORDER BY b.title
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(rs.getString("title"));
                }
            }
        }
        return books;
    }

    public void create(String name, String description) throws SQLException {
        String sql = "INSERT INTO categories (name, description) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String name, String description) throws SQLException {
        String sql = "UPDATE categories SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT id FROM categories WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private CategoryDTO mapToDTO(ResultSet rs) throws SQLException {
        return new CategoryDTO(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("book_count")
        );
    }
    public void addCategoryToBook(int bookId, int categoryId) throws SQLException {
        String sql = "INSERT INTO book_categories (book_id, category_id) VALUES (?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
    public void removeCategoryFromBook(int bookId, int categoryId) throws SQLException {
        String sql = "DELETE FROM book_categories WHERE book_id = ? AND category_id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        }
    }
}
