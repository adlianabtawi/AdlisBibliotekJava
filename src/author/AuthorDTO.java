package author;

import java.time.LocalDate;

public class AuthorDTO {
    private final int id;
    private final String firstName;
    private final String lastName;
    private final String nationality;
    private final LocalDate birthDate;
    private final String biography;
    private final String website;
    private final int bookCount;

    public AuthorDTO(int id, String firstName, String lastName, String nationality,
                     LocalDate birthDate, String biography, String website, int bookCount) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationality = nationality;
        this.birthDate = birthDate;
        this.biography = biography;
        this.website = website;
        this.bookCount = bookCount;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getNationality() { return nationality; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getBiography() { return biography; }
    public String getWebsite() { return website; }
    public int getBookCount() { return bookCount; }
}
