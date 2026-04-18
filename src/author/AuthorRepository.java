package author;

import db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AuthorRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<AuthorDTO> findAll() throws SQLException {
        List<AuthorDTO> authors = new ArrayList<>();
        String sql = """
                SELECT a.id, a.first_name, a.last_name, a.nationality, a.birth_date,
                       ad.biography, ad.website,
                       COUNT(ba.book_id) AS book_count
                FROM authors a
                LEFT JOIN author_descriptions ad ON a.id = ad.author_id
                LEFT JOIN book_authors ba ON a.id = ba.author_id
                GROUP BY a.id, a.first_name, a.last_name, a.nationality, a.birth_date,
                         ad.biography, ad.website
                ORDER BY a.last_name, a.first_name
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                authors.add(mapToDTO(rs));
            }
        }
        return authors;
    }

    public AuthorDTO findById(int id) throws SQLException {
        String sql = """
                SELECT a.id, a.first_name, a.last_name, a.nationality, a.birth_date,
                       ad.biography, ad.website,
                       COUNT(ba.book_id) AS book_count
                FROM authors a
                LEFT JOIN author_descriptions ad ON a.id = ad.author_id
                LEFT JOIN book_authors ba ON a.id = ba.author_id
                WHERE a.id = ?
                GROUP BY a.id, a.first_name, a.last_name, a.nationality, a.birth_date,
                         ad.biography, ad.website
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

    public List<String> findBooksByAuthor(int authorId) throws SQLException {
        List<String> books = new ArrayList<>();
        String sql = """
                SELECT b.title
                FROM books b
                JOIN book_authors ba ON b.id = ba.book_id
                WHERE ba.author_id = ?
                ORDER BY b.title
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(rs.getString("title"));
                }
            }
        }
        return books;
    }

    public void create(String firstName, String lastName, String nationality,
                       LocalDate birthDate, String biography, String website) throws SQLException {
        Connection conn = getConnection();
        String authorSql = "INSERT INTO authors (first_name, last_name, nationality, birth_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(authorSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, nationality);
            stmt.setDate(4, birthDate != null ? Date.valueOf(birthDate) : null);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int authorId = keys.getInt(1);
                    insertDescription(conn, authorId, biography, website);
                }
            }
        }
    }

    private void insertDescription(Connection conn, int authorId, String biography, String website) throws SQLException {
        String sql = "INSERT INTO author_descriptions (author_id, biography, website) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, authorId);
            stmt.setString(2, biography);
            stmt.setString(3, website);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String firstName, String lastName, String nationality,
                       LocalDate birthDate, String biography, String website) throws SQLException {
        Connection conn = getConnection();
        String authorSql = """
                UPDATE authors SET first_name = ?, last_name = ?, nationality = ?, birth_date = ?
                WHERE id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(authorSql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, nationality);
            stmt.setDate(4, birthDate != null ? Date.valueOf(birthDate) : null);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }

        String descSql = """
                UPDATE author_descriptions SET biography = ?, website = ?
                WHERE author_id = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(descSql)) {
            stmt.setString(1, biography);
            stmt.setString(2, website);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT id FROM authors WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private AuthorDTO mapToDTO(ResultSet rs) throws SQLException {
        Date birthDate = rs.getDate("birth_date");
        return new AuthorDTO(
                rs.getInt("id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("nationality"),
                birthDate != null ? birthDate.toLocalDate() : null,
                rs.getString("biography"),
                rs.getString("website"),
                rs.getInt("book_count")
        );
    }
}
