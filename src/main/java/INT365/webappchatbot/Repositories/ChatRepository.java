package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("select c from Chat c where (c.name1 = :senderName and c.name2 = :receiverName) or (c.name1 = :receiverName and c.name2 = :senderName)")
    Chat findChatBySenderAndReceiverName(@Param("senderName") String senderName, @Param("receiverName") String receiverName);

    Chat findChatByChatId(Long chatId);

    @Query("select max(chatId) + 1 from Chat")
    Long findLatestId();

}
