package book;

import db.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {

    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();

        String sql = """
        SELECT 
            books.id,
            books.title,
                books.isbn,             -- Lägg till!
                books.year_published,   -- Lägg till!
                books.total_copies,     -- Lägg till!
            books.available_copies,
            GROUP_CONCAT(DISTINCT CONCAT(authors.first_name, ' ', authors.last_name) 
                SEPARATOR ', ') AS author_names,
            GROUP_CONCAT(DISTINCT categories.name 
                SEPARATOR ', ') AS category_names
        FROM books
        JOIN book_authors ON books.id = book_authors.book_id
        JOIN authors ON book_authors.author_id = authors.id
        LEFT JOIN book_categories ON books.id = book_categories.book_id
        LEFT JOIN categories ON book_categories.category_id = categories.id
                     GROUP BY books.id, books.title, books.isbn, books.year_published, books.total_copies, books.available_copies
        """;


        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }



    public Optional<Book> findById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    public List<Book> findByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                books.add(mapRow(rs));
            }
        }
        return books;
    }

    public int save(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, isbn, year_published, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getIsbn());
            stmt.setInt(3, book.getYearPublished());
            stmt.setInt(4, book.getTotalCopies());
            stmt.setInt(5, book.getAvailableCopies());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        }
        return -1;
    }

    public void update(Book book) throws SQLException {
        String sql = "UPDATE books SET title=?, year_published=?, total_copies=?, available_copies=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getYearPublished());
            stmt.setInt(3, book.getTotalCopies());
            stmt.setInt(4, book.getAvailableCopies());
            stmt.setInt(5, book.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("isbn"),
                rs.getInt("year_published"),
                rs.getInt("total_copies"),
                rs.getInt("available_copies")
        );
        book.setAuthorName(rs.getString("author_names"));     // ✅ Inte first_name/last_name!
        book.setCategoryName(rs.getString("category_names")); // ✅
        return book;
    }



    public List<Book> search(String title, String author, Integer categoryId, boolean onlyAvailable, String sortBy) throws SQLException {
        List<Book> books = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT 
            b.id,
            b.title,
            b.isbn,
            b.year_published,
            b.total_copies,
            b.available_copies,
            GROUP_CONCAT(DISTINCT CONCAT(a.first_name, ' ', a.last_name) 
                SEPARATOR ', ') AS author_names,
            GROUP_CONCAT(DISTINCT c.name 
                SEPARATOR ', ') AS category_names
        FROM books b
        LEFT JOIN book_authors ba ON b.id = ba.book_id
        LEFT JOIN authors a ON ba.author_id = a.id
        LEFT JOIN book_categories bc ON b.id = bc.book_id
        LEFT JOIN categories c ON bc.category_id = c.id
        WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append("AND b.title LIKE ? ");
            params.add("%" + title.trim() + "%");
        }
        if (author != null && !author.isBlank()) {
            sql.append("AND CONCAT(a.first_name, ' ', a.last_name) LIKE ? ");
            params.add("%" + author.trim() + "%");
        }
        if (categoryId != null) {
            sql.append("AND bc.category_id = ? ");
            params.add(categoryId);
        }
        if (onlyAvailable) {
            sql.append("AND b.available_copies > 0 ");
        }

        // GROUP BY måste vara med när vi använder GROUP_CONCAT!
        sql.append("GROUP BY b.id, b.title, b.isbn, b.year_published, b.total_copies, b.available_copies ");

        sql.append(switch (sortBy) {
            case "newest" -> "ORDER BY b.year_published DESC";
            case "oldest" -> "ORDER BY b.year_published ASC";
            default       -> "ORDER BY b.title ASC";
        });

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
        }
        return books;
    }



}
