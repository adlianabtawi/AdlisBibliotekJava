package member;

public class MemberSummaryDTO {
    private final int id;
    private final String name;
    private final String email;

    public MemberSummaryDTO(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + name + " – " + email;
    }
}
