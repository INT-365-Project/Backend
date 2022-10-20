package INT365.webappchatbot.Models;

import INT365.webappchatbot.Constants.Status;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private Long chatId;
    private String senderName;
    private String receiverName;
    private String type;
    private String message;
    private Date date;
    private Status status;
    private String displayName;
    @JsonProperty("isRead")
    private Boolean isRead;
}
