package author;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(int id) {
        super("Författare med ID " + id + " hittades inte.");
    }
}
