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

    public void addBook(String title, String isbn, int year, int copies) throws SQLException {
        Book book = new Book(title, isbn, year, copies, copies);
        bookRepository.save(book);
    }

    public void deleteBook(int id) throws SQLException {
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
        bookRepository.delete(id);
    }

    public List<Book> searchBooks(String title, String author, Integer categoryId, boolean onlyAvailable, String sortBy) throws SQLException {
        return bookRepository.search(title, author, categoryId, onlyAvailable, sortBy);
    }


}
