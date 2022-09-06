package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import INT365.webappchatbot.Webhook.WebhookEvent;
import INT365.webappchatbot.Webhook.WebhookObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WebhookService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    @Autowired
    private ExternalService externalService;


    public Object testWebhook(WebhookObject request) {
        // save message to chat history that send from user
        this.saveMessage(request, "get");
        // use bot flow
        WebhookObject object = this.externalService.sendToDialogflow(request);
        // save message to chat history that send back to user
        this.saveMessage(object, "send");
        // return webhook object to line api
        return object;
        // use manual flow
    }

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
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId);
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
}
