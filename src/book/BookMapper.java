package book;

public class BookMapper {

    public static BookSummaryDTO toSummaryDTO(Book book) {
        return new BookSummaryDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthorName(),      // Nytt!
                book.getCategoryName(),    // Nytt!
                book.getAvailableCopies()
        );
    }


    public static BookDetailDTO toDetailDTO(Book book) {
        return new BookDetailDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getYearPublished(),
                book.getAvailableCopies()
        );
    }
}
