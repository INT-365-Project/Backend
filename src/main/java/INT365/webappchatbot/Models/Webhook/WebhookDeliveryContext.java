package INT365.webappchatbot.Models.Webhook;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class WebhookDeliveryContext {
    @JsonProperty("isRedelivery")
    private Boolean isRedelivery;
}
