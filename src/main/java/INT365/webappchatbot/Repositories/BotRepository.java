package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BotRepository extends JpaRepository<Bot, Long> {

    List<Bot> findBotsByName(String name);

    @Modifying
    @Query("delete from Bot b where b.name = :name")
    void deleteBotsByName(@Param("name") String name);
}
