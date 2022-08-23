package INT365.webappchatbot.Webhook;

import lombok.Data;

import java.util.List;

@Data
public class WebhookRequest {
    private String destination;
    private List<WebhookEvent> events;
}
