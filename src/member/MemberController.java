package member;

import loan.LoanService;
import loan.LoanDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MemberController {

    private final MemberService service;
    private final LoanService loanService;
    private final Scanner scanner;

    public MemberController(Scanner scanner) {
        this.service = new MemberService();
        this.loanService = new LoanService();
        this.scanner = scanner;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== MEDLEMMAR ===");
            System.out.println("1. Visa alla medlemmar");
            System.out.println("2. Lägg till medlem");
            System.out.println("3. Ta bort medlem");
            System.out.println("4. Visa profil");
            System.out.println("5. Uppdatera profil");
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listMembers();
                case "2" -> addMember();
                case "3" -> deleteMember();
                case "4" -> showProfile();
                case "5" -> updateProfile();
                case "0" -> running = false;
                default -> System.out.println("Ogiltigt val.");
            }
        }
    }

    private void listMembers() {
        try {
            List<MemberSummaryDTO> members = service.getAllMembers();
            if (members.isEmpty()) {
                System.out.println("Inga medlemmar hittades.");
            } else {
                members.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }

    private void addMember() {
        System.out.print("Förnamn: ");
        String firstName = scanner.nextLine();
        System.out.print("Efternamn: ");
        String lastName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefon: ");
        String phone = scanner.nextLine();

        try {
            service.addMember(firstName, lastName, email, phone);
            System.out.println("Medlem tillagd!");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        }
    }


    private void showProfile() {
        System.out.print("Ange medlems-ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            // 1. Hämta medlemsinformation
            Member member = service.getMemberById(id);
            System.out.println("\n=== PROFIL ===");
            System.out.println("ID:        " + member.getId());
            System.out.println("Namn:      " + member.getFullName());
            System.out.println("Email:     " + member.getEmail());
            System.out.println("Telefon:   " + member.getPhone());
            // 2. Hämta och visa lånen
            System.out.println("\n--- Din Lånehistorik ---");
            List<LoanDTO> loans = loanService.getLoansByMember(id);

            if (loans.isEmpty()) {
                System.out.println("Du har inga registrerade lån.");
            } else {
                for (LoanDTO loan : loans) {
                    String status = loan.isReturned() ? "Återlämnad" : loan.isOverdue() ? "⚠ FÖRSENAD" : "Aktiv";
                    System.out.printf("Bok: %-20s | Lånades: %s | Förfaller: %s | Status: %s%n",
                            loan.getBookTitle(),
                            loan.getLoanDate(),
                            loan.getDueDate(),
                            status);
                }
            }
            System.out.println("================\n");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        } catch (MemberNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void updateProfile() {
        System.out.print("Ange medlems-ID: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());

            // 1. Hämta den befintliga medlemmen först
            Member currentMember = service.getMemberById(id);

            System.out.println("Tips: Tryck bara 'Enter' om du vill behålla det nuvarande värdet.");

            // 2. Fråga efter ny info. Visar nuvarande värde inom [klamrar].
            // Om användaren lämnar tomt (.isBlank) så behåller vi det gamla värdet.

            System.out.print("Nytt förnamn [" + currentMember.getFirstName() + "]: ");
            String firstName = scanner.nextLine();
            if (firstName.isBlank()) firstName = currentMember.getFirstName();

            System.out.print("Nytt efternamn [" + currentMember.getLastName() + "]: ");
            String lastName = scanner.nextLine();
            if (lastName.isBlank()) lastName = currentMember.getLastName();

            System.out.print("Ny email [" + currentMember.getEmail() + "]: ");
            String email = scanner.nextLine();
            if (email.isBlank()) email = currentMember.getEmail();

            System.out.print("Nytt telefonnummer [" + currentMember.getPhone() + "]: ");
            String phone = scanner.nextLine();
            if (phone.isBlank()) phone = currentMember.getPhone();

            // 3. Skicka iväg den "blandade" datan till databasen
            service.updateMember(id, firstName, lastName, email, phone);
            System.out.println("Profil uppdaterad!");

        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt ID.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        } catch (MemberNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }


    private void deleteMember() {
        System.out.print("Ange medlemmens id: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            service.deleteMember(id);
            System.out.println("Medlem borttagen!");
        } catch (NumberFormatException e) {
            System.out.println("Ogiltigt id.");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
        } catch (MemberNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
