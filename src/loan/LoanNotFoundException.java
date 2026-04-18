package loan;

public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException(int id) {
        super("Lån med ID " + id + " hittades inte eller är redan återlämnat.");
    }
}
