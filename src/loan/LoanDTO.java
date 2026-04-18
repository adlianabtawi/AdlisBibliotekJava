package loan;

import java.time.LocalDate;

public class LoanDTO {
    private final int id;
    private final int bookId;
    private final String bookTitle;
    private final int memberId;
    private final String memberName;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private final LocalDate returnDate;

    public LoanDTO(int id, int bookId, String bookTitle, int memberId, String memberName,
                   LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public int getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }
    public boolean isOverdue() { return !isReturned() && LocalDate.now().isAfter(dueDate); }
}
