package INT365.webappchatbot.Webhook;

import lombok.Data;

import java.util.List;

@Data
public class WebhookObject {
    private String destination;
    private List<WebhookEvent> events;
}
