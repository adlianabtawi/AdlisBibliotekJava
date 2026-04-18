package category;

public class CategoryDTO {
    private final int id;
    private final String name;
    private final String description;
    private final int bookCount;

    public CategoryDTO(int id, String name, String description, int bookCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.bookCount = bookCount;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getBookCount() { return bookCount; }
}
