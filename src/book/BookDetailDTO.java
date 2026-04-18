package book;

public record BookDetailDTO(int id, String title, String isbn,
                            int yearPublished, int availableCopies) {
    @Override
    public String toString() {
        return "[" + id + "] " + title + "\n" +
                "    ISBN: " + isbn + "\n" +
                "    År: " + yearPublished + "\n" +
                "    Tillgängliga: " + availableCopies;
    }
}
