package INT365.webappchatbot.Webhook;

import lombok.Data;

@Data
public class WebhookMessage {
    private String type;
    private String id;
    private String text;
}