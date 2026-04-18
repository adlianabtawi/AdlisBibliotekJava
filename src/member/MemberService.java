package member;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class MemberService {

    private final MemberRepository repository;

    public MemberService() {
        this.repository = new MemberRepository();
    }

    public List<MemberSummaryDTO> getAllMembers() throws SQLException {
        return repository.findAll()
                .stream()
                .map(m -> new MemberSummaryDTO(m.getId(), m.getName(), m.getEmail()))
                .collect(Collectors.toList());
    }

    public Member getMemberById(int id) throws SQLException, MemberNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
    }

    public void addMember(String name, String email, String phone) throws SQLException {
        Member member = new Member(name, email, phone);
        repository.save(member);
    }

    public void deleteMember(int id) throws SQLException, MemberNotFoundException {
        repository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(id));
        repository.delete(id);
    }
}
