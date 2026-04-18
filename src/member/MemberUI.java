package member;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MemberUI {

    private final MemberService service;
    private final Scanner scanner;

    public MemberUI(Scanner scanner) {
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
            System.out.println("0. Tillbaka");
            System.out.print("Val: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listMembers();
                case "2" -> addMember();
                case "3" -> deleteMember();
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
        System.out.print("Namn: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefon: ");
        String phone = scanner.nextLine();

        try {
            service.addMember(name, email, phone);
            System.out.println("Medlem tillagd!");
        } catch (SQLException e) {
            System.out.println("Databasfel: " + e.getMessage());
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
