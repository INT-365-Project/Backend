package INT365.webappchatbot.Models.resp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@AllArgsConstructor
@Data
public class ChatHistoryResponse {
    private String senderName;
    private String receiverName;
    private String message;
    private Date sentDate;
}
