package ce.web.daarbast.model.userworkspacerole;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWorkspaceRoleRepository extends JpaRepository<UserWorkspaceRole, Long> {
}
