package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    @Modifying
    @Query("update News set thumbnailPath = :filePath where newId = :newsId")
    void updateNewsFilePathByNewsId(@Param("newsId") Long newsId, @Param("filePath") String filePath);
}
