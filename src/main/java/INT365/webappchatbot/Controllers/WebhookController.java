package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Webhook.WebhookEvent;
import INT365.webappchatbot.Webhook.WebhookMessage;
import INT365.webappchatbot.Webhook.WebhookRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final String channelAccessToken = "otHH5PaiURD4VbIuAdyS1MnGxhe5gTw5aH+emXYIT70a1HG3DLazeCT+Te94f8pOHuRAwKySHYetZ+uQrtffwgEbSugS14Zne6TZfxuYgv8qK+KXHumBNt3L2YsJdT6hZcbBvcVSKKlNxXXgvBA8XgdB04t89/1O/w1cDnyilFU=";
    private final String channelId = "1657101758";
    private final String channelSecret = "08ff6b71e9ae45dae62f27b762d8df65";
    private final String sendingMessageURI = "https://api.line.me/v2/bot/message/reply";

    @PostMapping("/test")
    public Object testWebhook(@RequestBody WebhookRequest request) {
        if (request.getEvents().size() == 0) return "ok";
        RestTemplate restTemplate = new RestTemplate();
        SendingMessageRequest msgRequest = new SendingMessageRequest();
        List<WebhookMessage> messages = new ArrayList<>();
        for (WebhookEvent event : request.getEvents()) {
            if (event.getMessage() != null) {
                if (event.getMessage().getType().equals("text")) {
                    WebhookMessage message = new WebhookMessage();
                    message.setType("text");
                    message.setText("Hello world");
                    messages.add(message);
                    msgRequest.setReplyToken(event.getReplyToken());
                }
            }
        }
        if (messages.size() > 0) {
            msgRequest.setMessages(messages);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.channelAccessToken);
            HttpEntity<SendingMessageRequest> entity = new HttpEntity<>(msgRequest, httpHeaders);
            return restTemplate.postForObject(this.sendingMessageURI, entity, Object.class);
        }
        return "not ok";
    }

}
