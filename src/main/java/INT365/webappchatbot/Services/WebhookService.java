package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import INT365.webappchatbot.Webhook.WebhookEvent;
import INT365.webappchatbot.Webhook.WebhookMessage;
import INT365.webappchatbot.Webhook.WebhookObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class WebhookService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    private final String channelAccessToken = "otHH5PaiURD4VbIuAdyS1MnGxhe5gTw5aH+emXYIT70a1HG3DLazeCT+Te94f8pOHuRAwKySHYetZ+uQrtffwgEbSugS14Zne6TZfxuYgv8qK+KXHumBNt3L2YsJdT6hZcbBvcVSKKlNxXXgvBA8XgdB04t89/1O/w1cDnyilFU=";
    private final String channelId = "1657101758";
    private final String channelSecret = "08ff6b71e9ae45dae62f27b762d8df65";
    private final String sendingMessageURI = "https://api.line.me/v2/bot/message/reply";
    private final String getProfileURI = "https://api.line.me/v2/bot/profile"; // "/{userId}"
    private final String dialogflowURI = "https://dialogflow.cloud.google.com/v1/integrations/line/webhook/8dfbb52a-8ad0-41fa-b224-ebb744200442";
    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);


    public Object testWebhook(WebhookObject request) {
        // save message to chat history that send from user
        this.saveMessage(request, "get");
        // use bot flow
        WebhookObject object = this.sendToDialogflow(request);
        ObjectMapper mapper = new ObjectMapper();
        //Object to JSON in file
        try {
            mapper.writeValue(new File("/home/azureuser/request.json"), request);
            mapper.writeValue(new File("/home/azureuser/object.json"), object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // save message to chat history that send back to user
        this.saveMessage(object, "send");
        // return webhook object to line api
        return object;
        // use manual flow
    }

    @Transactional
    private void saveMessage(WebhookObject request, String way) {
        for (WebhookEvent event : request.getEvents()) {
            if (event.getMessage() != null) {
                String userId = event.getSource().getType().equals("user") ? event.getSource().getUserId() : null;
                // focus on only user's text message
                if (event.getMessage().getType().equals("text")) {
                    // save detail to database (message, sourceUserId, targetUserId, date, detail of message)
                    // chat detail
                    Chat chat = this.chatRepository.findChatBySenderAndReceiverName("admin", userId) == null ? new Chat() : this.chatRepository.findChatBySenderAndReceiverName("admin", userId);
                    if (chat.getChatId() == null) {
                        chat.setName1("admin");
                        chat.setName2(userId);
                        chat.setCreateDate(new Date());
                        chat = this.chatRepository.saveAndFlush(chat);
                    }
                    // chat history detail
                    ChatHistory history = new ChatHistory();
                    UserProfileResponse userObject = this.getUserProfile(userId);
                    history.setChatId(chat.getChatId());
                    history.setSenderName(way.equals("get") ? userObject.getDisplayName() : "admin");
                    history.setReceiverName(way.equals("get") ? "admin" : userObject.getDisplayName());
                    // only text
                    history.setMessage(event.getMessage().getText());
                    history.setSentDate(event.getTimestamp());
                    this.chatHistoryRepository.saveAndFlush(history);
                }
            }
        }
    }

    public synchronized WebhookObject sendToDialogflow(WebhookObject request) {
        // send message to Dialogflow and send it back to Line
        return this.restTemplate.postForObject(this.dialogflowURI, request, WebhookObject.class);
    }

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
