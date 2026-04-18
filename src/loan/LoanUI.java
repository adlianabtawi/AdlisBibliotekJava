package loan;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class LoanUI {

    private final Scanner scanner;
    private final LoanService loanService;

    public LoanUI(Scanner scanner) {
        this.scanner = scanner;
        this.loanService = new LoanService();
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== LÅNEHANTERING ===");
            System.out.println("1. Visa alla aktiva lån");
            System.out.println("2. Visa lån per medlem");
            System.out.println("3. Låna en bok");
            System.out.println("4. Återlämna en bok");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showAllActiveLoans();
                case "2" -> showLoansByMember();
                case "3" -> borrowBook();
                case "4" -> returnBook();
                case "0" -> running = false;
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void showAllActiveLoans() {
        try {
            List<LoanDTO> loans = loanService.getAllActiveLoans();
            if (loans.isEmpty()) {
                System.out.println("Inga aktiva lån hittades.");
                return;
            }
            System.out.println("\n--- Aktiva lån ---");
            for (LoanDTO loan : loans) {
                System.out.printf("ID: %d | Bok: %s | Medlem: %s | Förfaller: %s%s%n",
                        loan.getId(),
                        loan.getBookTitle(),
                        loan.getMemberName(),
                        loan.getDueDate(),
                        loan.isOverdue() ? " ⚠ FÖRSENAD" : "");
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showLoansByMember() {
        System.out.print("Ange medlems-ID: ");
        try {
            int memberId = Integer.parseInt(scanner.nextLine());
            List<LoanDTO> loans = loanService.getLoansByMember(memberId);
            if (loans.isEmpty()) {
                System.out.println("Inga lån hittades för denna medlem.");
                return;
            }
            System.out.println("\n--- Lån för medlem ---");
            for (LoanDTO loan : loans) {
                String status = loan.isReturned() ? "Återlämnad" : loan.isOverdue() ? "⚠ FÖRSENAD" : "Aktiv";
                System.out.printf("ID: %d | Bok: %s | Lånad: %s | Förfaller: %s | Status: %s%n",
                        loan.getId(),
                        loan.getBookTitle(),
                        loan.getLoanDate(),
                        loan.getDueDate(),
                        status);
            }
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void borrowBook() {
        try {
            System.out.print("Ange bok-ID: ");
            int bookId = Integer.parseInt(scanner.nextLine());
            System.out.print("Ange medlems-ID: ");
            int memberId = Integer.parseInt(scanner.nextLine());
            System.out.print("Antal dagar att låna (standard 21): ");
            String daysInput = scanner.nextLine();
            int days = daysInput.isBlank() ? 21 : Integer.parseInt(daysInput);

            loanService.borrowBook(bookId, memberId, days);
            System.out.println("Boken är nu utlånad! Förfaller: " + LocalDate.now().plusDays(days));

        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt värde.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Fel: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void returnBook() {
        try {
            System.out.print("Ange låne-ID: ");
            int loanId = Integer.parseInt(scanner.nextLine());
            loanService.returnBook(loanId);
            System.out.println("Boken är återlämnad!");

        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (LoanNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }
}
