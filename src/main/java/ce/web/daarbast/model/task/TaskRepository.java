package ce.web.daarbast.model.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.workspace.id = :workspaceId")
    List<Task> findTasksByWorkspaceId(@Param("workspaceId") Long workspaceId);}
