package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Models.resp.ChatHistoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    @Query("select new INT365.webappchatbot.Models.resp.ChatHistoryResponse(h.senderName,h.receiverName,h.message,h.sentDate) from ChatHistory h join Chat c on c.chatId = h.chatId where h.chatId = :chatId order by h.sentDate asc")
    List<ChatHistoryResponse> findChatHistoriesByChatId(@Param("chatId") Long chatId);
}
