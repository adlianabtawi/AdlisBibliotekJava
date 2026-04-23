import author.AuthorController;
import book.BookController;
import category.CategoryController;
import loan.LoanController;
import member.MemberController;
import java.util.Scanner;

public class Main {

    private final Scanner scanner;
    private final BookController bookController;
    private final MemberController memberController;
    private final LoanController loanController;
    private final AuthorController authorController;
    private final CategoryController categoryController;

    public Main() {
        this.scanner = new Scanner(System.in);
        this.bookController = new BookController(scanner);
        this.memberController = new MemberController(scanner);
        this.loanController = new LoanController(scanner);
        this.authorController = new AuthorController(scanner);
        this.categoryController = new CategoryController(scanner);
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== HUVUDMENY ===");
            System.out.println("1. Böcker");
            System.out.println("2. Medlemmar");
            System.out.println("3. Lån");
            System.out.println("4. Författare");
            System.out.println("5. Kategorier");
            System.out.println("0. Avsluta");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> bookController.show();
                case "2" -> memberController.show();
                case "3" -> loanController.show();
                case "4" -> authorController.show();
                case "5" -> categoryController.show();
                case "0" -> {
                    System.out.println("Avslutar...");
                    running = false;
                }
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    public static void main(String[] args) {
        new Main().show();
    }
}
