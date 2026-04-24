package author;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AuthorService {

    private final AuthorRepository authorRepository = new AuthorRepository();

    public List<AuthorDTO> getAllAuthors() throws SQLException {
        return authorRepository.findAll();
    }

    public AuthorDTO getAuthorById(int id) throws SQLException {
        AuthorDTO author = authorRepository.findById(id);
        if (author == null) {
            throw new AuthorNotFoundException(id);
        }
        return author;
    }

    public List<String> getBooksByAuthor(int authorId) throws SQLException {
        if (!authorRepository.exists(authorId)) {
            throw new AuthorNotFoundException(authorId);
        }
        return authorRepository.findBooksByAuthor(authorId);
    }

    public void addAuthor(String firstName, String lastName, String nationality,
                          LocalDate birthDate, String biography, String website) throws SQLException {
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Förnamn får inte vara tomt.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Efternamn får inte vara tomt.");
        }
        authorRepository.create(firstName.trim(), lastName.trim(), nationality.trim(),
                birthDate, biography.trim(), website.trim());
    }

    public void updateAuthor(int id, String firstName, String lastName, String nationality,
                             String dateInput, String biography, String website) throws SQLException {

        // 1. Hämta den nuvarande författaren från databasen för att se vad som finns nu
        AuthorDTO current = getAuthorById(id);


        String[] nameParts = current.getFullName().split(" ", 2);
        String oldFirstName = nameParts[0];
        String oldLastName = nameParts.length > 1 ? nameParts[1] : "";

        // 2. Är den nya texten tom? Använd i så fall den gamla datan!
        String finalFirstName = (firstName != null && !firstName.isBlank()) ? firstName.trim() : oldFirstName;
        String finalLastName = (lastName != null && !lastName.isBlank()) ? lastName.trim() : oldLastName;
        String finalNationality = (nationality != null && !nationality.isBlank()) ? nationality.trim() : current.getNationality();
        String finalBiography = (biography != null && !biography.isBlank()) ? biography.trim() : current.getBiography();
        String finalWebsite = (website != null && !website.isBlank()) ? website.trim() : current.getWebsite();

        // 3. Smart hantering av datumet
        LocalDate finalDate = current.getBirthDate();
        if (dateInput != null && !dateInput.isBlank()) {
            finalDate = LocalDate.parse(dateInput.trim()); // Tolka bara datumet om användaren skrev in ett nytt
        }

        // 4. Skicka de färdiga värdena till Repositoryt
        authorRepository.update(id, finalFirstName, finalLastName, finalNationality,
                finalDate, finalBiography, finalWebsite);
    }


    public void deleteAuthor(int id) throws SQLException {
        if (!authorRepository.exists(id)) {
            throw new AuthorNotFoundException(id);
        }
        authorRepository.delete(id);
    }
}
