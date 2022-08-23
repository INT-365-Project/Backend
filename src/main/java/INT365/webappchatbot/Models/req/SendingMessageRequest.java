package INT365.webappchatbot.Models.req;

import INT365.webappchatbot.Webhook.WebhookMessage;
import lombok.Data;

import java.util.List;

@Data
public class SendingMessageRequest {
    private String replyToken;
    private List<WebhookMessage> messages;
}
