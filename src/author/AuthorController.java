package author;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class AuthorController {

    private final Scanner scanner;
    private final AuthorService authorService;

    public AuthorController(Scanner scanner) {
        this.scanner = scanner;
        this.authorService = new AuthorService();
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== FÖRFATTARE ===");
            System.out.println("1. Visa alla författare");
            System.out.println("2. Visa författardetaljer");
            System.out.println("3. Visa böcker per författare");
            System.out.println("4. Lägg till författare");
            System.out.println("5. Uppdatera författare");
            System.out.println("6. Ta bort författare");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showAllAuthors();
                case "2" -> showAuthorDetails();
                case "3" -> showBooksByAuthor();
                case "4" -> addAuthor();
                case "5" -> updateAuthor();
                case "6" -> deleteAuthor();
                case "0" -> running = false;
                default  -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void showAllAuthors() {
        try {
            List<AuthorDTO> authors = authorService.getAllAuthors();
            if (authors.isEmpty()) {
                System.out.println("Inga författare hittades.");
                return;
            }
            System.out.println("\n--- Alla författare ---");
            for (AuthorDTO a : authors) {
                System.out.printf("ID: %d | %-25s | Nationalitet: %-15s | Böcker: %d%n",
                        a.getId(), a.getFullName(), a.getNationality(), a.getBookCount());
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showAuthorDetails() {
        try {
            System.out.print("Ange författar-ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            AuthorDTO a = authorService.getAuthorById(id);

            System.out.println("\n--- Författardetaljer ---");
            System.out.println("Namn       : " + a.getFullName());
            System.out.println("Nationalitet: " + a.getNationality());
            System.out.println("Född       : " + (a.getBirthDate() != null ? a.getBirthDate() : "Okänd"));
            System.out.println("Biografi   : " + (a.getBiography() != null ? a.getBiography() : "Ingen"));
            System.out.println("Webbplats  : " + (a.getWebsite() != null ? a.getWebsite() : "Ingen"));
            System.out.println("Antal böcker: " + a.getBookCount());
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (AuthorNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showBooksByAuthor() {
        try {
            System.out.print("Ange författar-ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            AuthorDTO author = authorService.getAuthorById(id);
            List<String> books = authorService.getBooksByAuthor(id);

            System.out.println("\n--- Böcker av: " + author.getFullName() + " ---");
            if (books.isEmpty()) {
                System.out.println("Inga böcker hittades för denna författare.");
            } else {
                books.forEach(b -> System.out.println("  - " + b));
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (AuthorNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void addAuthor() {
        try {
            System.out.print("Förnamn: ");
            String firstName = scanner.nextLine();
            System.out.print("Efternamn: ");
            String lastName = scanner.nextLine();
            System.out.print("Nationalitet: ");
            String nationality = scanner.nextLine();
            System.out.print("Födelsedatum (ÅÅÅÅ-MM-DD, lämna tomt om okänt): ");
            String dateInput = scanner.nextLine();
            LocalDate birthDate = dateInput.isBlank() ? null : LocalDate.parse(dateInput);
            System.out.print("Biografi: ");
            String biography = scanner.nextLine();
            System.out.print("Webbplats: ");
            String website = scanner.nextLine();

            authorService.addAuthor(firstName, lastName, nationality, birthDate, biography, website);
            System.out.println("Författaren har lagts till!");
        } catch (DateTimeParseException e) {
            System.out.println("Ogiltigt datumformat. Använd ÅÅÅÅ-MM-DD.");
        } catch (IllegalArgumentException e) {
            System.out.println("Fel: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void updateAuthor() {
        try {
            System.out.print("Ange författar-ID att uppdatera: ");
            int id = Integer.parseInt(scanner.nextLine());

            // Hämta befintlig data för att visa den
            AuthorDTO current = authorService.getAuthorById(id);

            System.out.println("\nTips: Tryck 'Enter' om du vill behålla det nuvarande värdet.");
            System.out.println("Nuvarande namn: " + current.getFullName());

            System.out.print("Nytt förnamn: ");
            String firstName = scanner.nextLine();

            System.out.print("Nytt efternamn: ");
            String lastName = scanner.nextLine();

            System.out.printf("Ny nationalitet [%s]: ", current.getNationality() != null ? current.getNationality() : "Ingen");
            String nationality = scanner.nextLine();

            System.out.printf("Nytt födelsedatum [%s] (ÅÅÅÅ-MM-DD): ", current.getBirthDate() != null ? current.getBirthDate() : "Okänt");
            String dateInput = scanner.nextLine();

            System.out.printf("Ny biografi [%s]: ", current.getBiography() != null ? current.getBiography() : "Ingen");
            String biography = scanner.nextLine();

            System.out.printf("Ny webbplats [%s]: ", current.getWebsite() != null ? current.getWebsite() : "Ingen");
            String website = scanner.nextLine();

            // Skicka allt som strängar till servicen
            authorService.updateAuthor(id, firstName, lastName, nationality, dateInput, biography, website);
            System.out.println("Författaren har uppdaterats!");

        } catch (DateTimeParseException e) {
            System.out.println("Ogiltigt datumformat. Använd ÅÅÅÅ-MM-DD.");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (AuthorNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }


    private void deleteAuthor() {
        try {
            System.out.print("Ange författar-ID att ta bort: ");
            int id = Integer.parseInt(scanner.nextLine());

            authorService.deleteAuthor(id);
            System.out.println("Författaren har tagits bort!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (AuthorNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }
}
