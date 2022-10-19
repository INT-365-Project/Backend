package INT365.webappchatbot.Models.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ChatHistoryObject {
    private String senderName;
    private String type;
    private String message;
    private Date sentDate;
    @JsonProperty("isRead")
    private Boolean isRead;
}
