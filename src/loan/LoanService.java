package loan;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LoanService {

    private final LoanRepository loanRepository = new LoanRepository();

    public List<LoanDTO> getAllActiveLoans() throws SQLException {
        return loanRepository.findAllActive();
    }

    public List<LoanDTO> getLoansByMember(int memberId) throws SQLException {
        if (!loanRepository.memberExists(memberId)) {
            throw new IllegalArgumentException("Medlem med ID " + memberId + " hittades inte.");
        }
        return loanRepository.findByMemberId(memberId);
    }

    public void borrowBook(int bookId, int memberId, int loanDays) throws SQLException {
        // Kontrollera att boken finns
        if (!loanRepository.bookExists(bookId)) {
            throw new IllegalArgumentException("Bok med ID " + bookId + " hittades inte.");
        }

        // Kontrollera att boken har lediga exemplar
        int availableCopies = loanRepository.getAvailableCopies(bookId);
        if (availableCopies <= 0) {
            throw new IllegalStateException("Boken har inga lediga exemplar just nu.");
        }

        // Kontrollera att medlemmen finns
        if (!loanRepository.memberExists(memberId)) {
            throw new IllegalArgumentException("Medlem med ID " + memberId + " hittades inte.");
        }

        // Kontrollera att medlemmen är aktiv
        String status = loanRepository.getMemberStatus(memberId);
        switch (status) {
            case "suspended" -> throw new IllegalStateException("Medlemmen är avstängd och kan inte låna böcker.");
            case "expired"   -> throw new IllegalStateException("Medlemskapet har gått ut. Förnya för att låna böcker.");
        }

        // Allt ok - skapa lån och minska tillgängliga exemplar
        LocalDate dueDate = LocalDate.now().plusDays(loanDays);
        loanRepository.createLoan(bookId, memberId, dueDate);
        loanRepository.decreaseAvailableCopies(bookId);
    }

    public void returnBook(int loanId) throws SQLException {
        if (!loanRepository.loanExists(loanId)) {
            throw new LoanNotFoundException(loanId);
        }

        // Hämta bok-ID innan återlämning för att öka tillgängliga exemplar
        int bookId = loanRepository.getBookIdFromLoan(loanId);
        loanRepository.returnLoan(loanId);
        loanRepository.increaseAvailableCopies(bookId);
    }
}
