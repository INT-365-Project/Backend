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

    List<Response> findResponsesByTopicName(String topicName);

    List<Response> findResponsesByTopic(String topic);

    Response findResponseByName(String name);

    @Modifying
    @Query("delete from Response r where r.topicName = :topicName")
    void deleteResponsesByTopicName(@Param("topicName") String topicName);
}
