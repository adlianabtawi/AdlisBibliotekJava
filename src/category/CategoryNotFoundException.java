package category;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(int id) {
        super("Kategori med ID " + id + " hittades inte.");
    }
}
