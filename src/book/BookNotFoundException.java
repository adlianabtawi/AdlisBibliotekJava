package book;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(int id) {
        super("Bok med id " + id + " hittades inte.");
    }
}
