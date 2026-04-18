package author;

import java.time.LocalDate;

public class Author {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String nationality;
    private final LocalDate birthDate;

    public Author(int id, String firstName, String lastName, String nationality, LocalDate birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.birthDate = birthDate;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getNationality() { return nationality; }
    public LocalDate getBirthDate() { return birthDate; }
}
