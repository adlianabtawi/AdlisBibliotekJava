package member;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MemberController {

    private final MemberService service;
    private final Scanner scanner;

    public MemberController(Scanner scanner) {
        this.service = new MemberService();
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
            Member member = service.getMemberById(id);
            System.out.println("\n--- Profil ---");
            System.out.println("ID:        " + member.getId());
            System.out.println("Namn:      " + member.getFullName());
            System.out.println("Email:     " + member.getEmail());
            System.out.println("Telefon:   " + member.getPhone());
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
            System.out.print("Nytt förnamn: ");
            String firstName = scanner.nextLine();
            System.out.print("Nytt efternamn: ");
            String lastName = scanner.nextLine();
            System.out.print("Ny email: ");
            String email = scanner.nextLine();
            System.out.print("Nytt telefonnummer: ");
            String phone = scanner.nextLine();

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
