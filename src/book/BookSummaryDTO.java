package book;

public record BookSummaryDTO(int id, String title, int availableCopies) {
    @Override
    public String toString() {
        return "[" + id + "] " + title + " – Tillgängliga: " + availableCopies;
    }
}
