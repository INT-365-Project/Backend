package INT365.webappchatbot.Models.Webhook;

import lombok.Data;

import java.util.List;

@Data
public class WebhookMessage {
    private String type;
    private String id;
    private String text;
    private List<WebhookEmoji> emojis;
    private String packageId;
    private String stickerId;
    private String originalContentUrl;
    private String previewImageUrl;
}
