package book;

import java.sql.SQLException;
import java.util.List;

public class BookService {

    private final BookRepository bookRepository = new BookRepository();

    public List<BookSummaryDTO> getAllBooks() throws SQLException {
        return bookRepository.findAll().stream()
                .map(BookMapper::toSummaryDTO)
                .toList();
    }

    public BookDetailDTO getBookById(int id) throws SQLException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        return BookMapper.toDetailDTO(book);
    }

    public List<BookSummaryDTO> searchByTitle(String title) throws SQLException {
        return bookRepository.findByTitle(title).stream()
                .map(BookMapper::toSummaryDTO)
                .toList();
    }

    // ==========================================
    // UPPUDATERAD METOD: Nu sparas författare & kategori!
    // ==========================================
    public void addBook(String title, String isbn, int year, int copies, int authorId, int categoryId) throws SQLException {
        Book book = new Book(title, isbn, year, copies, copies);

        // 1. Spara boken och hämta det nya ID:t som databasen skapade
        int newBookId = bookRepository.save(book);

        // 2. Om boken sparades korrekt, spara kopplingarna!
        if (newBookId != -1) {
            bookRepository.addAuthorToBook(newBookId, authorId);
            bookRepository.addCategoryToBook(newBookId, categoryId);
        } else {
            throw new SQLException("Kunde inte hämta det genererade ID:t för boken.");
        }
    }

    public void deleteBook(int id) throws SQLException {
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(id);
    }

    public List<Book> searchBooks(String title, String author, Integer categoryId, boolean onlyAvailable, String sortBy) throws SQLException {
        return bookRepository.search(title, author, categoryId, onlyAvailable, sortBy);
    }

    public void updateBook(int id, String title, String yearStr, String copiesStr) throws SQLException {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        // 1. Uppdatera titel om den inte är tom
        if (title != null && !title.isBlank()) {
            book.setTitle(title);
        }

        // 2. Uppdatera år om det inte är tomt
        if (yearStr != null && !yearStr.isBlank()) {
            book.setYearPublished(Integer.parseInt(yearStr));
        }

        // 3. Smart uppdatering av antal kopior!
        if (copiesStr != null && !copiesStr.isBlank()) {
            int newTotalCopies = Integer.parseInt(copiesStr);

            // Räkna ut hur många böcker som för tillfället är ute hos låntagare
            int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

            // Säkerhetsspärr: Man kan inte ha färre totala böcker än vad som är utlånat
            if (newTotalCopies < borrowedCopies) {
                throw new IllegalArgumentException("Kan inte sänka antalet till " + newTotalCopies +
                        ". Just nu är " + borrowedCopies + " böcker utlånade.");
            }

            book.setTotalCopies(newTotalCopies);
            // Sätt tillgängliga böcker = det nya totala - det som är utlånat
            book.setAvailableCopies(newTotalCopies - borrowedCopies);
        }

        // 4. Spara till databasen
        bookRepository.update(book);
    }

}
