package INT365.webappchatbot.Models.Webhook;

import lombok.Data;

import java.util.Date;

@Data
public class WebhookEvent {
    private String type;
    private WebhookMessage message;
    private Date timestamp;
    private WebhookSource source;
    private String replyToken;
    private String mode;
    private String webhookEventId;
    private WebhookDeliveryContext deliveryContext;
}
