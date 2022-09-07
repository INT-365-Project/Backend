package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findResponsesByName(String name);

    List<Response> findResponsesByTopic(String topic);

    @Modifying
    @Query("delete from Response r where r.name = :name")
    void deleteResponsesByName(@Param("name") String name);
}
