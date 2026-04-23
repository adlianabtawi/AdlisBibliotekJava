package book;

// ✅ Se till att toString() visar all info
public record BookSummaryDTO(
        int id,
        String title,
        String authorName,
        String categoryName,
        int availableCopies
) {
    @Override
    public String toString() {
        return String.format("[%d] %s av %s | %s | Kopior: %d",
                id, title, authorName, categoryName, availableCopies);
    }
}

