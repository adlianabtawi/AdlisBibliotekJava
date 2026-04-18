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
                             LocalDate birthDate, String biography, String website) throws SQLException {
        if (!authorRepository.exists(id)) {
            throw new AuthorNotFoundException(id);
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalArgumentException("Förnamn får inte vara tomt.");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Efternamn får inte vara tomt.");
        }
        authorRepository.update(id, firstName.trim(), lastName.trim(), nationality.trim(),
                birthDate, biography.trim(), website.trim());
    }

    public void deleteAuthor(int id) throws SQLException {
        if (!authorRepository.exists(id)) {
            throw new AuthorNotFoundException(id);
        }
        authorRepository.delete(id);
    }
}
