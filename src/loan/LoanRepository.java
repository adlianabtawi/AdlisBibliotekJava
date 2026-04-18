package loan;

import db.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    public List<LoanDTO> findAllActive() throws SQLException {
        List<LoanDTO> loans = new ArrayList<>();
        String sql = """
                SELECT l.id, l.book_id, b.title, l.member_id,
                       CONCAT(m.first_name, ' ', m.last_name) AS member_name,
                       l.loan_date, l.due_date, l.return_date
                FROM loans l
                JOIN books b ON l.book_id = b.id
                JOIN members m ON l.member_id = m.id
                WHERE l.return_date IS NULL
                ORDER BY l.due_date
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(mapToDTO(rs));
            }
        }
        return loans;
    }

    public List<LoanDTO> findByMemberId(int memberId) throws SQLException {
        List<LoanDTO> loans = new ArrayList<>();
        String sql = """
                SELECT l.id, l.book_id, b.title, l.member_id,
                       CONCAT(m.first_name, ' ', m.last_name) AS member_name,
                       l.loan_date, l.due_date, l.return_date
                FROM loans l
                JOIN books b ON l.book_id = b.id
                JOIN members m ON l.member_id = m.id
                WHERE l.member_id = ?
                ORDER BY l.loan_date DESC
                """;
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapToDTO(rs));
                }
            }
        }
        return loans;
    }

    public void createLoan(int bookId, int memberId, LocalDate dueDate) throws SQLException {
        String sql = "INSERT INTO loans (book_id, member_id, loan_date, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, memberId);
            stmt.setDate(3, Date.valueOf(LocalDate.now()));
            stmt.setDate(4, Date.valueOf(dueDate));
            stmt.executeUpdate();
        }
    }

    public void returnLoan(int loanId) throws SQLException {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, loanId);
            stmt.executeUpdate();
        }
    }

    public boolean loanExists(int loanId) throws SQLException {
        String sql = "SELECT id FROM loans WHERE id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // --- Nya valideringsmetoder ---

    public boolean bookExists(int bookId) throws SQLException {
        String sql = "SELECT id FROM books WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public int getAvailableCopies(int bookId) throws SQLException {
        String sql = "SELECT available_copies FROM books WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("available_copies");
                }
            }
        }
        return 0;
    }

    public boolean memberExists(int memberId) throws SQLException {
        String sql = "SELECT id FROM members WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public String getMemberStatus(int memberId) throws SQLException {
        String sql = "SELECT status FROM members WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    public void decreaseAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies - 1 WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public void increaseAvailableCopies(int bookId) throws SQLException {
        String sql = "UPDATE books SET available_copies = available_copies + 1 WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public int getBookIdFromLoan(int loanId) throws SQLException {
        String sql = "SELECT book_id FROM loans WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, loanId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("book_id");
                }
            }
        }
        return -1;
    }

    private LoanDTO mapToDTO(ResultSet rs) throws SQLException {
        return new LoanDTO(
                rs.getInt("id"),
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getInt("member_id"),
                rs.getString("member_name"),
                rs.getDate("loan_date").toLocalDate(),
                rs.getDate("due_date").toLocalDate(),
                rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null
        );
    }
}
