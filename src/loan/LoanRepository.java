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

    public void extendLoan(int loanId, int extraDays) throws SQLException {
        String sql = "UPDATE loans SET due_date = DATE_ADD(due_date, INTERVAL ? DAY) WHERE id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, extraDays);
            stmt.setInt(2, loanId);
            stmt.executeUpdate();
        }
    }

    public List<String> getMostBorrowedBooks(int limit) throws SQLException {
        List<String> stats = new ArrayList<>();
        // Enkel SQL som räknar hur många gånger varje book_id dyker upp i låne-tabellen
        String query = "SELECT b.title, COUNT(l.book_id) as loan_count " +
                "FROM loans l " + // OBS: Byt ut 'loans' om din tabell för lån heter något annat
                "JOIN books b ON l.book_id = b.id " +
                "GROUP BY l.book_id " +
                "ORDER BY loan_count DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection(); // Använd din databaskoppling
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, limit); // Här sätter vi in 10 eller 20 beroende på vem som frågar!

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(rs.getString("title") + " - Utlånad " + rs.getInt("loan_count") + " gånger");
                }
            }
        }
        return stats;
    }

    public List<String> getOverdueLoans() throws SQLException {
        List<String> overdueList = new ArrayList<>();

        // Jämför due_date med dagens datum (CURDATE i MySQL)
        String query = "SELECT b.title, m.first_name, m.last_name, l.due_date " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "JOIN members m ON l.member_id = m.id " + // Byt "members" mot din användartabell
                "WHERE l.due_date < CURDATE() AND l.return_date IS NULL " +
                "ORDER BY l.due_date ASC"; // Visar de mest försenade först

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String title = rs.getString("title");
                String name = rs.getString("first_name") + " " + rs.getString("last_name");
                String dueDate = rs.getString("due_date");

                overdueList.add("Bok: '" + title + "' | Låntagare: " + name + " | Skulle varit inne: " + dueDate);
            }
        }
        return overdueList;
    }

    public List<String> getMyFines(int memberId) throws SQLException {
        List<String> finesList = new ArrayList<>();

        // DATEDIFF räknar ut dagar mellan idag och förfallodatumet
        String query = "SELECT b.title, DATEDIFF(CURDATE(), l.due_date) AS days_late " +
                "FROM loans l " +
                "JOIN books b ON l.book_id = b.id " +
                "WHERE l.member_id = ? AND l.due_date < CURDATE() AND l.return_date IS NULL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String title = rs.getString("title");
                    int daysLate = rs.getInt("days_late");

                    // Räkna ut påbörjade månader (delar på 30 och avrundar uppåt)
                    int monthsLate = (int) Math.ceil(daysLate / 30.0);
                    int fineAmount = monthsLate * 10; // 10 kr per påbörjad månad

                    finesList.add("Bok: '" + title + "' | Försenad: " + monthsLate + " månad(er) | Böter: " + fineAmount + " kr");
                }
            }
        }
        return finesList;
    }

    public void quickReturnBook(int bookId) throws SQLException {
        // Avslutar lånet där just den boken är utlånad just nu
        String sql = "UPDATE loans SET return_date = CURDATE() WHERE book_id = ? AND return_date IS NULL";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new IllegalArgumentException("Denna bok är inte utlånad just nu!");
            }
        }
        // Glöm inte att öka antalet tillgängliga exemplar!
        increaseAvailableCopies(bookId);
    }
    // För Låntagaren (ändra sin membership_type)
    public void updateMembershipType(int memberId, String newType) throws SQLException {
        String sql = "UPDATE members SET membership_type = ? WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, newType);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        }
    }

    // För Bibliotekarien (stänga av konto)
    public void suspendMember(int memberId) throws SQLException {
        String sql = "UPDATE members SET status = 'suspended' WHERE id = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }


}
