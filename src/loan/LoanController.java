package loan;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class LoanController {

    private final Scanner scanner;
    private final LoanService loanService;

    public LoanController(Scanner scanner) {
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
            System.out.println("5. Förläng ett lån");
            System.out.println("6. Visa mest utlånade böckerna (Bibliotekarie)");
            System.out.println("7. Visa mest utlånade böckerna (Låntagare)");
            System.out.println("8. Visa förfallna lån");
            System.out.println("9. Visa böter för låntagare");
            System.out.println("10. Snabb-retur av bok");
            System.out.println("11. Hantera mitt medlemskap");
            System.out.println("12. Stäng av låntagarkonto");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showAllActiveLoans();
                case "2" -> showLoansByMember();
                case "3" -> borrowBook();
                case "4" -> returnBook();
                case "5" -> extendLoan();
                case "6" -> showTop10Books();
                case "7" -> showTop20Books();
                case "8" -> showOverdueLoans();
                case "9" -> showMyFines();
                case "10" -> quickReturnBook();
                case "11" -> changeMyMembershipType();
                case "12" -> suspendMemberAccount();
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
            System.out.print("Ange medlems-ID som lämnar tillbaka: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            // Hämtar medlemmens lån och filtrerar så vi BARA ser de som inte är återlämnade
            List<LoanDTO> activeLoans = loanService.getLoansByMember(memberId).stream()
                    .filter(loan -> !loan.isReturned())
                    .toList();

            if (activeLoans.isEmpty()) {
                System.out.println("Denna medlem har inga aktiva lån att lämna tillbaka.");
                return; // Avbryter så vi inte frågar efter Låne-ID i onödan
            }

            System.out.println("\n--- Böcker att återlämna ---");
            for (LoanDTO loan : activeLoans) {
                System.out.printf("Låne-ID: %d | Bok: %s | Förfaller: %s%n",
                        loan.getId(), loan.getBookTitle(), loan.getDueDate());
            }

            System.out.print("\nAnge låne-ID för den bok som ska återlämnas: ");
            int loanId = Integer.parseInt(scanner.nextLine());

            loanService.returnBook(loanId);
            System.out.println("Boken är återlämnad!");

        } catch (NumberFormatException e) {
            System.out.println("Ogiltig inmatning. Ange siffror.");
        } catch (LoanNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void extendLoan() {
        try {
            System.out.print("Ange medlems-ID som vill förlänga: ");
            int memberId = Integer.parseInt(scanner.nextLine());

            // Hämtar medlemmens aktiva lån även här
            List<LoanDTO> activeLoans = loanService.getLoansByMember(memberId).stream()
                    .filter(loan -> !loan.isReturned())
                    .toList();

            if (activeLoans.isEmpty()) {
                System.out.println("Denna medlem har inga aktiva lån att förlänga.");
                return;
            }

            System.out.println("\n--- Böcker att förlänga ---");
            for (LoanDTO loan : activeLoans) {
                System.out.printf("Låne-ID: %d | Bok: %s | Nuvarande förfallodatum: %s%n",
                        loan.getId(), loan.getBookTitle(), loan.getDueDate());
            }

            System.out.print("\nAnge låne-ID för den bok du vill förlänga: ");
            int loanId = Integer.parseInt(scanner.nextLine());

            System.out.print("Antal extra dagar (standard 14): ");
            String daysInput = scanner.nextLine();
            int days = daysInput.isBlank() ? 14 : Integer.parseInt(daysInput);

            loanService.extendLoan(loanId, days);
            System.out.println("Lånet har förlängts med " + days + " dagar!");

        } catch (NumberFormatException e) {
            System.out.println("Ogiltig inmatning. Ange siffror.");
        } catch (LoanNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Fel: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showTop20Books() {
        try {
            System.out.println("\n--- Topp 20 mest utlånade böckerna (Bibliotekarievy) ---");
            List<String> topBooks = loanService.getMostBorrowedBooks(20); // Skickar in 20!
            if (topBooks.isEmpty()) {
                System.out.println("Inga lån är registrerade i systemet ännu.");
            } else {
                topBooks.forEach(b -> System.out.println("⭐ " + b));
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showTop10Books() {
        try {
            System.out.println("\n--- Våra Topp 10 populäraste böcker! ---");
            List<String> topBooks = loanService.getMostBorrowedBooks(10); // Skickar in 10!
            if (topBooks.isEmpty()) {
                System.out.println("Inga lån är registrerade i systemet ännu.");
            } else {
                topBooks.forEach(b -> System.out.println("⭐ " + b));
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showOverdueLoans() {
        try {
            System.out.println("\n--- 🚨 Register över förfallna lån ---");
            List<String> overdueLoans = loanService.getOverdueLoans();

            if (overdueLoans.isEmpty()) {
                System.out.println("✅ Bra nyheter! Det finns inga försenade böcker.");
            } else {
                overdueLoans.forEach(loan -> System.out.println("🔴 " + loan));
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void showMyFines() {
        System.out.println("\n--- 💸 Kolla mina böter ---");
        System.out.print("Ange ditt Låntagar-ID: ");

        try {
            int memberId = Integer.parseInt(scanner.nextLine()); // Låter användaren skriva in ID

            List<String> fines = loanService.getMyFines(memberId);

            if (fines.isEmpty()) {
                System.out.println("✅ Du har inga försenade böcker och inga böter att betala. Bra jobbat!");
            } else {
                int totalFines = 0;
                for (String fineInfo : fines) {
                    System.out.println("🔴 " + fineInfo);
                    String[] parts = fineInfo.split("Böter: ");
                    totalFines += Integer.parseInt(parts[1].replace(" kr", ""));
                }
                System.out.println("-------------------------");
                System.out.println("💰 Totalt att betala: " + totalFines + " kr");
            }

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Ogiltigt ID. Du måste skriva in siffror.");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage()); // Här skrivs "Kunde inte hitta..." ut!
        } catch (SQLException e) {
            System.out.println("🔴 Databasfel: " + e.getMessage());
        }
    }

    private void quickReturnBook() {
        System.out.println("\n--- 📖 Snabb-retur (Manuell återlämning) ---");
        System.out.print("Ange Bok-ID (streckkod): ");

        try {
            int bookId = Integer.parseInt(scanner.nextLine());

            loanService.quickReturnBook(bookId);
            System.out.println("✅ Succé! Boken är återlämnad och tillgänglig för utlåning igen.");

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Ogiltig inmatning. Ange siffror.");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("🔴 Databasfel: " + e.getMessage());
        }
    }

    private void changeMyMembershipType() {
        System.out.println("\n--- 🎟️ Ändra medlemskapsnivå ---");
        System.out.print("Ange ditt Låntagar-ID: ");

        try {
            int memberId = Integer.parseInt(scanner.nextLine());

            System.out.println("Vilken typ av medlemskap vill du byta till?");
            System.out.println("1. Standard");
            System.out.println("2. Premium");
            System.out.println("3. Student");
            System.out.print("Val: ");
            String typeVal = scanner.nextLine();

            String newType;
            if (typeVal.equals("1")) {
                newType = "Standard"; // Ändra dessa så de exakt matchar det som ska in i din databas
            } else if (typeVal.equals("2")) {
                newType = "Premium";
            } else if (typeVal.equals("3")) {
                newType = "Student";
            } else {
                System.out.println("⚠️ Ogiltigt val, avbryter.");
                return;
            }

            loanService.changeMembershipType(memberId, newType);
            System.out.println("✅ Succé! Ditt medlemskap är nu uppdaterat till: " + newType);

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Ogiltigt ID. Du måste skriva in siffror.");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("🔴 Databasfel: " + e.getMessage());
        }
    }

    private void suspendMemberAccount() {
        System.out.println("\n--- 🚫 Stäng av låntagarkonto ---");
        System.out.print("Ange Låntagar-ID för kontot som ska stängas av: ");

        try {
            int memberId = Integer.parseInt(scanner.nextLine());

            loanService.suspendMember(memberId);
            System.out.println("✅ Succé! Kontot med ID " + memberId + " är nu avstängt (suspended).");

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Ogiltigt ID. Du måste skriva in siffror.");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("🔴 Databasfel: " + e.getMessage());
        }
    }

}
