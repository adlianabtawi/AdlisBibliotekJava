package category;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CategoryUI {

    private final Scanner scanner;
    private final CategoryService categoryService;

    public CategoryUI(Scanner scanner) {
        this.scanner = scanner;
        this.categoryService = new CategoryService();
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== KATEGORIER ===");
            System.out.println("1. Visa alla kategorier");
            System.out.println("2. Visa böcker per kategori");
            System.out.println("3. Lägg till kategori");
            System.out.println("4. Uppdatera kategori");
            System.out.println("5. Ta bort kategori");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showAllCategories();
                case "2" -> showBooksByCategory();
                case "3" -> addCategory();
                case "4" -> updateCategory();
                case "5" -> deleteCategory();
                case "0" -> running = false;
                default  -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void showAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            if (categories.isEmpty()) {
                System.out.println("Inga kategorier hittades.");
                return;
            }
            System.out.println("\n--- Alla kategorier ---");
            for (CategoryDTO c : categories) {
                System.out.printf("ID: %d | Namn: %-20s | Böcker: %d | Beskrivning: %s%n",
                        c.getId(), c.getName(), c.getBookCount(), c.getDescription());
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showBooksByCategory() {
        try {
            System.out.print("Ange kategori-ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            CategoryDTO category = categoryService.getCategoryById(id);
            List<String> books = categoryService.getBooksByCategory(id);

            System.out.println("\n--- Böcker i kategorin: " + category.getName() + " ---");
            if (books.isEmpty()) {
                System.out.println("Inga böcker hittades i denna kategori.");
            } else {
                books.forEach(b -> System.out.println("  - " + b));
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void addCategory() {
        try {
            System.out.print("Namn: ");
            String name = scanner.nextLine();
            System.out.print("Beskrivning: ");
            String description = scanner.nextLine();

            categoryService.addCategory(name, description);
            System.out.println("Kategorin har lagts till!");
        } catch (IllegalArgumentException e) {
            System.out.println("Fel: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void updateCategory() {
        try {
            System.out.print("Ange kategori-ID att uppdatera: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Nytt namn: ");
            String name = scanner.nextLine();
            System.out.print("Ny beskrivning: ");
            String description = scanner.nextLine();

            categoryService.updateCategory(id, name, description);
            System.out.println("Kategorin har uppdaterats!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (CategoryNotFoundException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            System.out.print("Ange kategori-ID att ta bort: ");
            int id = Integer.parseInt(scanner.nextLine());

            categoryService.deleteCategory(id);
            System.out.println("Kategorin har tagits bort!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }
}
