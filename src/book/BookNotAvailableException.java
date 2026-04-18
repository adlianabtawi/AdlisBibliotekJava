package book;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(int id) {
        super("Bok med id " + id + " har inga lediga exemplar.");
    }
}
