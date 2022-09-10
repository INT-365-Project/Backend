package INT365.webappchatbot.Models;

import INT365.webappchatbot.Constants.Status;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Long chatId;
    private String senderName;
    private String receiverName;
    private String message;
    private Date date;
    private Status status;
}
