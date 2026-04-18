import author.AuthorUI;
import book.BookUI;
import category.CategoryUI;
import loan.LoanUI;
import member.MemberUI;
import java.util.Scanner;

public class Main {

    private final Scanner scanner;
    private final BookUI bookUI;
    private final MemberUI memberUI;
    private final LoanUI loanUI;
    private final AuthorUI authorUI;
    private final CategoryUI categoryUI;

    public Main() {
        this.scanner = new Scanner(System.in);
        this.bookUI = new BookUI(scanner);
        this.memberUI = new MemberUI(scanner);
        this.loanUI = new LoanUI(scanner);
        this.authorUI = new AuthorUI(scanner);
        this.categoryUI = new CategoryUI(scanner);
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
                case "1" -> bookUI.show();
                case "2" -> memberUI.show();
                case "3" -> loanUI.show();
                case "4" -> authorUI.show();
                case "5" -> categoryUI.show();
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
