package member;

public class MemberNotFoundException extends Exception {
    public MemberNotFoundException(int id) {
        super("Medlem med id " + id + " hittades inte.");
    }
}
