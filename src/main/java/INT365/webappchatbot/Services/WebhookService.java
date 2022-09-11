package INT365.webappchatbot.Services;

import INT365.webappchatbot.Constants.Status;
import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.Webhook.WebhookEvent;
import INT365.webappchatbot.Models.Webhook.WebhookMessage;
import INT365.webappchatbot.Models.Webhook.WebhookObject;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class WebhookService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    @Autowired
    private ExternalService externalService;
    @Autowired
    private BotService botService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public Object webhookMessageAPI(WebhookObject request) {
        // save message to chat history that send from user
        this.saveMessageFromLine(request);
        // use bot flow
        List<SendingMessageRequest> response = this.botService.responseToWebhook(request);
        // save message to chat history that send back to user
        this.saveMessageFromBot(response);
        // return webhook object to line api
        this.externalService.replyMessage(response);

        return ResponseEntity.ok();
        // use manual flow
    }

    @Transactional
    private void saveMessageFromLine(WebhookObject request) {
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
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId);
                    history.setChatId(chat.getChatId());
                    history.setSenderName(userObject.getDisplayName());
                    history.setReceiverName("admin");
                    // only text
                    history.setMessage(event.getMessage().getText());
                    history.setSentDate(event.getTimestamp());
                    this.chatHistoryRepository.saveAndFlush(history);
                    this.sendMessageToWebApp(chat, history);
                }
            }
        }
    }

    @Transactional
    private void saveMessageFromBot(List<SendingMessageRequest> responses) {
        for (SendingMessageRequest messageRequest : responses) {
            if (messageRequest.getMessages().isEmpty()) break;
            String userId = messageRequest.getTo();
            for (WebhookMessage message : messageRequest.getMessages()) {
                // focus on only user's text message
                if (message.getType().equals("text")) {
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
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId);
                    history.setChatId(chat.getChatId());
                    history.setReceiverName(userObject.getDisplayName());
                    history.setSenderName("admin");
                    // only text
                    history.setMessage(message.getText());
                    history.setSentDate(new Date());
                    this.chatHistoryRepository.saveAndFlush(history);
                    this.sendMessageToWebApp(chat, history);
                }
            }
        }
    }

    private void sendMessageToWebApp(Chat chat, ChatHistory chatHistory) {
        Message message = new Message();
        message.setChatId(chat.getChatId());
        message.setSenderName(chatHistory.getSenderName());
        message.setReceiverName(chatHistory.getReceiverName());
        message.setMessage(Status.MESSAGE.name());
        message.setDate(chatHistory.getSentDate());
        simpMessagingTemplate.convertAndSendToUser(chat.getName2(), "/private", message);
    }
}
