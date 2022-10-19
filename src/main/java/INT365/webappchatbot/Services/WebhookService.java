package INT365.webappchatbot.Services;

import INT365.webappchatbot.Constants.Status;
import INT365.webappchatbot.Constants.WebhookMessageType;
import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.Webhook.WebhookEvent;
import INT365.webappchatbot.Models.Webhook.WebhookMessage;
import INT365.webappchatbot.Models.Webhook.WebhookObject;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.ChatObject;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ChatService chatService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final String botTurnOnMessage = "เปิดการใช้งานระบบตอบอัตโนมัติ";
//    private final String botTurnOffMessage = "ปิดการใช้งานระบบตอบอัตโนมัติ"; // for deploy
    private final String botTurnOffMessage = "ปิด"; // for deploy


    public Object webhookMessageAPI(WebhookObject request) {
        // save message to chat history that send from user
        Boolean isBotResponse = this.saveMessageFromLine(request);
        if (!isBotResponse) return null;
        // use bot flow
        List<SendingMessageRequest> response = this.botService.responseToWebhook(request);
        // save message to chat history that send back to user
        this.saveMessageFromBot(response);
        // return webhook object to line api
//        this.externalService.replyMessage(response); // for deploy
        return null;
        // use manual flow
    }

    @Transactional
    private Boolean saveMessageFromLine(WebhookObject request) {
        Boolean isBotResponse = true;
        for (WebhookEvent event : request.getEvents()) {
            if (event.getMessage() != null) {
                String userId = event.getSource().getType().equals("user") ? event.getSource().getUserId() : null;
                // focus on only user's text message
                if (event.getMessage().getType().equals("text")) {
                    // save detail to database (message, sourceUserId, targetUserId, date, detail of message)
                    // chat detail
                    String message = event.getMessage().getText();
                    Chat chat = this.chatRepository.findChatBySenderAndReceiverName("admin", userId) == null ? new Chat() : this.chatRepository.findChatBySenderAndReceiverName("admin", userId);
                    boolean isChatNull = chat.getChatId() == null;
                    if (chat.getChatId() == null) {
                        chat.setName1("admin");
                        chat.setName2(userId);
                        chat.setCreateDate(new Date());
                        chat.setIsBotResponse(1);
                    }
                    chat.setIsBotResponse(message.equals(this.botTurnOnMessage) ? 1 : message.equals(this.botTurnOffMessage) ? 0 : chat.getIsBotResponse());
                    chat = this.chatRepository.saveAndFlush(chat);
                    isBotResponse = Tools.convertIntToBoolean(chat.getIsBotResponse());
                    // chat history detail
                    ChatHistory history = new ChatHistory();
//                    UserProfileResponse userObject = this.externalService.getUserProfile(userId);
//                    String displayName = userObject.getDisplayName(); // for deploy
                    String displayName = userId; // for local
                    history.setChatId(chat.getChatId());
//                    history.setSenderName(userObject.getDisplayName());
                    history.setSenderName(userId);
                    history.setReceiverName("admin");
                    // only text
                    history.setType(WebhookMessageType.MESSAGE.getType());
                    history.setMessage(message);
                    history.setIsRead(isBotResponse ? 1 : 0); // for deploy
//                    history.setIsRead(0); // for local
                    history.setSentDate(event.getTimestamp());
                    this.chatHistoryRepository.saveAndFlush(history);
                    if (isChatNull) {
//                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, userObject.getPictureUrl())); // for deploy
                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, null)); // for local
                    } else {
                        this.sendMessageToWebApp(chat, history, displayName);
                    }
                }
            }
        }
        return isBotResponse;
    }

    private void sendNewHistoryChatToWebApp(ChatObject chatHistory) {
        this.simpMessagingTemplate.convertAndSend("/updateNewChat", chatHistory);
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
                    history.setChatId(chat.getChatId());
                    history.setReceiverName(userId);
                    history.setSenderName("admin");
                    history.setType("message");
                    history.setIsRead(1);
                    // only text
                    history.setMessage(message.getText());
                    history.setSentDate(new Date());
                    this.chatHistoryRepository.saveAndFlush(history);
                    this.sendMessageToWebApp(chat, history, "admin");
                }
            }
        }
    }

    private void sendMessageToWebApp(Chat chat, ChatHistory chatHistory, String displayName) {
        Message message = new Message();
        message.setChatId(chat.getChatId());
        message.setSenderName(chatHistory.getSenderName());
        message.setReceiverName(chatHistory.getReceiverName());
        message.setMessage(chatHistory.getMessage());
        message.setStatus(Status.MESSAGE);
        message.setDate(chatHistory.getSentDate());
        message.setDisplayName(displayName);
        simpMessagingTemplate.convertAndSendToUser(chat.getName2(), "/private", message);
    }
}
