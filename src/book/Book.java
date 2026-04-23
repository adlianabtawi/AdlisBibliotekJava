package book;

public class Book {

    private int id;
    private String title;
    private String isbn;
    private int yearPublished;
    private int totalCopies;
    private int availableCopies;
    private String authorName;
    private String categoryName;

    // Konstruktor för mappning från databasen (id känt)
    public Book(int id, String title, String isbn, int yearPublished,
                int totalCopies, int availableCopies) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.yearPublished = yearPublished;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Konstruktor för ny bok (id sätts av databasen)
    public Book(String title, String isbn, int yearPublished,
                int totalCopies, int availableCopies) {
        this(0, title, isbn, yearPublished, totalCopies, availableCopies);
    }

    // Getters
    public int getId()               { return id; }
    public String getTitle()         { return title; }
    public String getIsbn()          { return isbn; }
    public int getYearPublished()    { return yearPublished; }
    public int getTotalCopies()      { return totalCopies; }
    public int getAvailableCopies()  { return availableCopies; }
    public String getAuthorName() { return authorName; }
    public String getCategoryName() { return categoryName; }

    // Setters – bara fält som kan förändras
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
    public void setTitle(String title)          { this.title = title; }
    public void setYearPublished(int year)      { this.yearPublished = year; }
    public void setTotalCopies(int total)       { this.totalCopies = total; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }


    @Override
    public String toString() {
        return "[" + id + "] " + title + " (" + yearPublished + ")" +
                " – Tillgängliga: " + availableCopies + "/" + totalCopies;
    }
}