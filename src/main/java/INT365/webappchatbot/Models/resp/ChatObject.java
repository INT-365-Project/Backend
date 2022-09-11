package INT365.webappchatbot.Models.resp;

import lombok.Data;

import java.util.List;

@Data
public class ChatObject {
    private Long chatId;
    private String userId;
    private String displayName;
    private String imageUrl;
    private List<ChatHistoryObject> chatHistory;
}
