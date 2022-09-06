package INT365.webappchatbot.Feigns;

import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Webhook.WebhookEvent;
import INT365.webappchatbot.Webhook.WebhookMessage;
import INT365.webappchatbot.Webhook.WebhookObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExternalService {

    private final String lineMessagingApi = "https://api.line.me/v2/bot/message/reply";

    private final String channelAccessToken = "otHH5PaiURD4VbIuAdyS1MnGxhe5gTw5aH+emXYIT70a1HG3DLazeCT+Te94f8pOHuRAwKySHYetZ+uQrtffwgEbSugS14Zne6TZfxuYgv8qK+KXHumBNt3L2YsJdT6hZcbBvcVSKKlNxXXgvBA8XgdB04t89/1O/w1cDnyilFU=";
    private final String channelId = "1657101758";
    private final String channelSecret = "08ff6b71e9ae45dae62f27b762d8df65";
    private final String sendingMessageURI = "https://api.line.me/v2/bot/message/reply";
    private final String getProfileURI = "https://api.line.me/v2/bot/profile"; // "/{userId}"
    private final String dialogflowURI = "https://dialogflow.cloud.google.com/v1/integrations/line/webhook/8dfbb52a-8ad0-41fa-b224-ebb744200442";
    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public WebhookObject sendToDialogflow(WebhookObject request) {
        // send message to Dialogflow and send it back to Line
        return this.restTemplate.postForObject(this.dialogflowURI, request, WebhookObject.class);
    }

    @Async
    public UserProfileResponse getUserProfile(String userId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.channelAccessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(httpHeaders);
        return this.restTemplate.exchange(this.getProfileURI + "/" + userId, HttpMethod.GET, requestEntity, UserProfileResponse.class).getBody();
    }

    private Object nothingJustKeepingCode(WebhookObject request) {
        // verify webhook
        if (request.getEvents().size() == 0) return "ok";
        // prepare data for send message back to user
        SendingMessageRequest msgRequest = new SendingMessageRequest();
        List<WebhookMessage> messages = new ArrayList<>();
        for (WebhookEvent event : request.getEvents()) {
            if (event.getMessage() != null) {
                // focus on only user's text message
                if (event.getMessage().getType().equals("text")) {
                    WebhookMessage message = new WebhookMessage();
                    message.setType("text");
                    message.setText("Hello world");
                    messages.add(message);
                    msgRequest.setReplyToken(event.getReplyToken());
                }
            }
        }
        // condition that when user send message, send it back
        if (messages.size() > 0) {
            msgRequest.setMessages(messages);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
            httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + this.channelAccessToken);
            HttpEntity<SendingMessageRequest> entity = new HttpEntity<>(msgRequest, httpHeaders);
            return this.restTemplate.postForObject(this.sendingMessageURI, entity, Object.class);
        }
        return "not ok";
    }
}
