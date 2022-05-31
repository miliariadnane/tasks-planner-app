package nano.dev.tasksplanner.repository;

import nano.dev.tasksplanner.entity.Task;
import nano.dev.tasksplanner.entity.enumeration.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(
            value = "select * " +
                    "from task where type = :type",
            nativeQuery = true
    )
    List<Task> findByType(@Param("type") Type type);

    @Query(
            value = "select * " +
                    "from task where id = :id",
            nativeQuery = true
    )
    Task findByTaskId(@Param("id") long id);
}
