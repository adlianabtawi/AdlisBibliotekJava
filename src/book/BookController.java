package book;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BookController {

    private final BookService bookService = new BookService();
    private final Scanner scanner;

    public BookController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n------ Bokmeny ------");
            System.out.println("1. Visa alla böcker");
            System.out.println("2. Sök på titel");
            System.out.println("3. Visa detaljer för en bok");
            System.out.println("4. Lägg till ny bok");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            try {
                switch (input) {
                    case "1" -> showAllBooks();
                    case "2" -> searchByTitle();
                    case "3" -> showBookDetails();
                    case "4" -> addBook();
                    case "0" -> { return; }
                    default -> System.out.println("Ogiltigt val.");
                }
            } catch (BookNotFoundException | BookNotAvailableException e) {
                System.out.println("Fel: " + e.getMessage());
            } catch (SQLException e) {
                System.out.println("Databasfel: " + e.getMessage());
            }
        }
    }

    private void showAllBooks() throws SQLException {
        List<BookSummaryDTO> books = bookService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("Inga böcker hittades.");
            return;
        }
        books.forEach(System.out::println);
    }

    private void searchByTitle() throws SQLException {
        System.out.print("Sök titel: ");
        String title = scanner.nextLine();
        List<BookSummaryDTO> books = bookService.searchByTitle(title);
        if (books.isEmpty()) {
            System.out.println("Inga böcker hittades.");
            return;
        }
        books.forEach(System.out::println);
    }

    private void showBookDetails() throws SQLException {
        System.out.print("Ange bok-id: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println(bookService.getBookById(id));
    }

    private void addBook() throws SQLException {
        System.out.print("Titel: ");
        String title = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Utgivningsår: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Antal exemplar: ");
        int copies = Integer.parseInt(scanner.nextLine());
        bookService.addBook(title, isbn, year, copies);
        System.out.println("Boken lades till!");
    }
}
