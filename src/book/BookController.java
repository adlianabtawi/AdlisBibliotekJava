package book;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class BookController {

    private final BookService service;
    private final Scanner scanner;

    public BookController(Scanner scanner) {
        this.service = new BookService();
        this.scanner = scanner;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== BÖCKER ===");
            System.out.println("1. Visa alla böcker");
            System.out.println("2. Lägg till bok");
            System.out.println("3. Ta bort bok");
            System.out.println("4. Sök böcker");
            System.out.println("5. Redigera bok");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listBooks();
                case "2" -> addBook();
                case "3" -> deleteBook();
                case "4" -> searchBooks();
                case "5" -> editBook();
                case "0" -> running = false;
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void listBooks() {
        try {
            List<BookSummaryDTO> books = service.getAllBooks();
            if (books.isEmpty()) {
                System.out.println("Inga böcker hittades.");
            } else {
                books.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void addBook() {
        System.out.print("Titel: ");
        String title = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("År: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Antal exemplar: ");
        int copies = Integer.parseInt(scanner.nextLine());

        try {
            service.addBook(title, isbn, year, copies);
            System.out.println("Bok tillagd!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt värde för år eller antal exemplar.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void deleteBook() {
        System.out.print("Ange bokens id: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            service.deleteBook(id);
            System.out.println("Bok borttagen!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt id.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void searchBooks() {
        try {
            System.out.print("Titel (lämna tomt för alla): ");
            String title = scanner.nextLine();

            System.out.print("Författare (lämna tomt för alla): ");
            String author = scanner.nextLine();

            System.out.print("Kategori-ID (lämna tomt för alla): ");
            String catInput = scanner.nextLine();
            Integer categoryId = catInput.isBlank() ? null : Integer.parseInt(catInput);

            System.out.print("Visa bara tillgängliga? (j/n): ");
            boolean onlyAvailable = scanner.nextLine().equalsIgnoreCase("j");

            System.out.println("Sortering: 1. Titel A-Ö  2. Nyast  3. Äldst");
            System.out.print("Val: ");
            String sortBy = switch (scanner.nextLine()) {
                case "2" -> "newest";
                case "3" -> "oldest";
                default  -> "title";
            };

            List<Book> books = service.searchBooks(title, author, categoryId, onlyAvailable, sortBy);

            if (books.isEmpty()) {
                System.out.println("Inga böcker matchade sökningen.");
                return;
            }

            System.out.println("\n--- Sökresultat (" + books.size() + " böcker) ---");
            for (Book b : books) {
                System.out.printf("ID: %d | %-30s | Tillgängliga: %d%n",
                        b.getId(), b.getTitle(), b.getAvailableCopies());
            }

        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }
    private void editBook() {
        System.out.print("Ange bokens id: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Ny titel: ");
            String title = scanner.nextLine();
            System.out.print("Nytt utgivningsår: ");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.print("Nytt antal exemplar: ");
            int copies = Integer.parseInt(scanner.nextLine());

            service.updateBook(id, title, year, copies);
            System.out.println("Bok uppdaterad!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt värde.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        } catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}
