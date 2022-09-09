package INT365.webappchatbot.Models.resp;

import lombok.Data;

import java.util.Date;

@Data
public class ChatHistoryObject {
    private String senderName;
    private String message;
    private Date sentDate;
}
