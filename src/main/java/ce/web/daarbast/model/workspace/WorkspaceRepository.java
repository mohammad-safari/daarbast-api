package ce.web.daarbast.model.workspace;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    @Query("SELECT r.workspace FROM UserWorkspaceRole r WHERE r.user.userId = :userId")
    List<Workspace> findWorkspacesByUserId(@Param("userId") String userId);
}
