package INT365.webappchatbot.Services;

import INT365.webappchatbot.Constants.Status;
import INT365.webappchatbot.Constants.WebhookMessageType;
import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.Image;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.Webhook.WebhookEmoji;
import INT365.webappchatbot.Models.Webhook.WebhookEvent;
import INT365.webappchatbot.Models.Webhook.WebhookMessage;
import INT365.webappchatbot.Models.Webhook.WebhookObject;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.ChatObject;
import INT365.webappchatbot.Models.resp.UserProfileResponse;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

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
    private FileService fileService;
    @Value("${http.image.path}")
    private String imagePath;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final String botTurnOnMessage = "เปิดการใช้งานระบบตอบอัตโนมัติ";
    private final String botTurnOffMessage = "ติดต่อผู้ดูแล"; // for deploy
    private final String confirmMessage = "ยืนยัน"; // for deploy

    //    private final String botTurnOffMessage = "ปิด"; // for deploy

    public Object webhookMessageAPI(WebhookObject request) {
        // save message to chat history that send from user
        Map<String,Boolean> map = this.saveMessageFromLine(request);
        Boolean isBotResponse = map.get("isBotResponse");
        Boolean isConfirm =  map.get("isConfirm");
        if (!isBotResponse && (isConfirm == null)) return null;
        // use bot flow
        List<SendingMessageRequest> response = this.botService.responseToWebhook(request,isConfirm);
        // save message to chat history that send back to user
        this.saveMessageFromBot(response);
        // return webhook object to line api
        this.externalService.replyMessage(response); // for deploy
        return null;
        // use manual flow
    }

    @Transactional
    private Map<String, Boolean> saveMessageFromLine(WebhookObject request) {
        Boolean isBotResponse = true;
        Boolean isConfirm = null;
        Map<String, Boolean> map = new HashMap<>();
        for (WebhookEvent event : request.getEvents()) {
            if (event.getMessage() != null) {
                String userId = event.getSource().getType().equals("user") ? event.getSource().getUserId() : null;
                // focus on only user's text message
                if (event.getMessage().getType().equals(WebhookMessageType.TEXT.getType())) {
                    // save detail to database (message, sourceUserId, targetUserId, date, detail of message)
                    // chat detail
                    String message = event.getMessage().getText();
                    Chat chat = this.chatRepository.findChatBySenderAndReceiverName("admin", userId) == null ? new Chat() : this.chatRepository.findChatBySenderAndReceiverName("admin", userId);
                    boolean isChatNull = chat.getChatId() == null;
                    if (isChatNull) {
                        chat.setName1("admin");
                        chat.setName2(userId);
                        chat.setCreateDate(new Date());
                        chat.setIsBotResponse(1);
                    }
                    // check is user confirm to disable auto answer bot with confirm message
                    if (!isChatNull) {
                        List<ChatHistory> chatHistoryList = this.chatHistoryRepository.findChatHistoriesByChatIdOrderByHistoryId(chat.getChatId());
                        if (message.equals(this.confirmMessage)) {
                            isConfirm = chatHistoryList.get(chatHistoryList.size() - 1).getMessage().equals(this.botTurnOffMessage);
                            chat.setIsBotResponse(isConfirm ? 0 : chat.getIsBotResponse());
                        }
                    }
                    chat = this.chatRepository.saveAndFlush(chat);
                    isBotResponse = Tools.convertIntToBoolean(chat.getIsBotResponse());
                    // chat history detail
                    ChatHistory history = new ChatHistory();
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId); // for deploy
                    String displayName = userObject.getDisplayName(); // for deploy
//                    String displayName = userId; // for local
                    history.setChatId(chat.getChatId());
//                    history.setSenderName(userObject.getDisplayName());
                    history.setSenderName(userId);
                    history.setReceiverName("admin");
                    // only text
                    history.setType(WebhookMessageType.TEXT.getType());
                    history.setMessage(message);
                    // check emoji
                    List<WebhookEmoji> emojis = event.getMessage().getEmojis();
                    if (emojis != null && !emojis.isEmpty()) {
                        message = this.convertRawMessageToEmojiFormMessage(message, emojis);
                    }
                    history.setMessage(message);
                    history.setIsRead(isBotResponse ? 1 : 0);
                    history.setSentDate(event.getTimestamp());
                    this.chatHistoryRepository.saveAndFlush(history);
                    if (isChatNull) {
                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, userObject.getPictureUrl())); // for deploy
//                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, null)); // for local
                    } else {
                        this.sendMessageToWebApp(chat, history, displayName);
                    }
                } else if (event.getMessage().getType().equals(WebhookMessageType.STICKER.getType())) {
                    // save detail to database (message, sourceUserId, targetUserId, date, detail of message)
                    // chat detail
                    Chat chat = this.chatRepository.findChatBySenderAndReceiverName("admin", userId) == null ? new Chat() : this.chatRepository.findChatBySenderAndReceiverName("admin", userId);
                    boolean isChatNull = chat.getChatId() == null;
                    if (chat.getChatId() == null) {
                        chat.setName1("admin");
                        chat.setName2(userId);
                        chat.setCreateDate(new Date());
                        chat.setIsBotResponse(1);
                    }
                    chat.setIsBotResponse(chat.getIsBotResponse() == null ? 1 : chat.getIsBotResponse());
                    chat = this.chatRepository.saveAndFlush(chat);
                    isBotResponse = Tools.convertIntToBoolean(chat.getIsBotResponse());
                    // chat history detail
                    ChatHistory history = new ChatHistory();
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId); // for deploy
                    String displayName = userObject.getDisplayName(); // for deploy
//                    String displayName = userId; // for local
                    history.setChatId(chat.getChatId());
//                    history.setSenderName(userObject.getDisplayName());
                    history.setSenderName(userId);
                    history.setReceiverName("admin");
                    // only text
                    history.setType(WebhookMessageType.STICKER.getType());
                    history.setMessage(event.getMessage().getPackageId() + "," + event.getMessage().getStickerId());
                    history.setIsRead(isBotResponse ? 1 : 0);
                    history.setSentDate(event.getTimestamp());
                    this.chatHistoryRepository.saveAndFlush(history);
                    if (isChatNull) {
                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, userObject.getPictureUrl())); // for deploy
//                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, null)); // for local
                    } else {
                        this.sendMessageToWebApp(chat, history, displayName);
                    }

                } else if (event.getMessage().getType().equals(WebhookMessageType.IMAGE.getType())) {
                    WebhookMessage message = event.getMessage();
                    // save detail to database (message, sourceUserId, targetUserId, date, detail of message)
                    // chat detail
                    Chat chat = this.chatRepository.findChatBySenderAndReceiverName("admin", userId) == null ? new Chat() : this.chatRepository.findChatBySenderAndReceiverName("admin", userId);
                    boolean isChatNull = chat.getChatId() == null;
                    if (chat.getChatId() == null) {
                        chat.setName1("admin");
                        chat.setName2(userId);
                        chat.setCreateDate(new Date());
                        chat.setIsBotResponse(1);
                    }
                    chat.setIsBotResponse(chat.getIsBotResponse() == null ? 1 : chat.getIsBotResponse());
                    chat = this.chatRepository.saveAndFlush(chat);
                    isBotResponse = Tools.convertIntToBoolean(chat.getIsBotResponse());
                    // chat history detail
                    ChatHistory history = new ChatHistory();
                    UserProfileResponse userObject = this.externalService.getUserProfile(userId); // for deploy
                    String displayName = userObject.getDisplayName(); // for deploy
//                    String displayName = userId; // for local
                    history.setChatId(chat.getChatId());
//                    history.setSenderName(userObject.getDisplayName());
                    history.setSenderName(userId);
                    history.setReceiverName("admin");
                    // only text
                    history.setType(WebhookMessageType.IMAGE.getType());
//                    history.setMessage("image");
                    history.setMessage(event.getMessage().getId());
//                    history.setOriginalContentUrl(message.getOriginalContentUrl()); // for local
//                    history.setPreviewImageUrl(message.getPreviewImageUrl()); // for local
                    history.setIsRead(isBotResponse ? 1 : 0);
                    history.setSentDate(event.getTimestamp());
                    history = this.chatHistoryRepository.saveAndFlush(history);
                    // set image
                    Image image = externalService.getImageById(event.getMessage().getId());  // for deploy
                    String randomNumber = Tools.randomFileNameNumber();
                    history.setMessage(fileService.uploadFile(randomNumber, image.getResource(), randomNumber + ".jpeg").get("filePath"));
                    String url = imagePath + history.getChatId() + "/" + history.getHistoryId();
                    history.setOriginalContentUrl(image.getResource() != null ? url : null); // for deploy
                    history.setPreviewImageUrl(image.getResource() != null ? url : null); // for deploy
                    this.chatHistoryRepository.saveAndFlush(history);
                    if (isChatNull) {
                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, userObject.getPictureUrl())); // for deploy
//                        this.sendNewHistoryChatToWebApp(this.chatService.getOneChatHistory(chat.getChatId(), displayName, null)); // for local
                    } else {
                        this.sendMessageToWebApp(chat, history, displayName);
                    }
                }
            }
        }
        map.put("isBotResponse",isBotResponse);
        map.put("isConfirm",isConfirm);
        return map;
    }

    private String convertRawMessageToEmojiFormMessage(String message, List<WebhookEmoji> emojis) {
        class EmojiClass {
            private String context;
            private String productId;
            private String emojiId;

            public EmojiClass(String context, String productId, String emojiId) {
                this.context = context;
                this.productId = productId;
                this.emojiId = emojiId;
            }

            public String getContext() {
                return context;
            }

            public String getProductId() {
                return productId;
            }

            public String getEmojiId() {
                return emojiId;
            }
        }
        List<EmojiClass> contexts = new ArrayList<>();
        // loop get all emoji context
        for (WebhookEmoji emoji : emojis) {
            String tempText = message.substring(emoji.getIndex(), emoji.getIndex() + emoji.getLength());
            if (contexts.contains(tempText)) {
                continue;
            }
            contexts.add(new EmojiClass(tempText, emoji.getProductId(), emoji.getEmojiId()));
        }
        for (EmojiClass context : contexts) {
            message = message.replace(context.getContext(), "<img src='/emoji/" + context.getProductId() + "/" + context.getEmojiId() + ".jpg' alt='emoji'/>");
        }
        return message;
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
                if (message.getType().equals(WebhookMessageType.TEXT.getType())) {
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
                    history.setType(WebhookMessageType.TEXT.getType());
                    history.setIsRead(1);
                    // only text
                    history.setMessage(message.getText());
                    history.setSentDate(new Date());
                    this.chatHistoryRepository.saveAndFlush(history);
                    this.sendMessageToWebApp(chat, history, "admin");
                } else if (message.getType().equals(WebhookMessageType.STICKER.getType())) {
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
                    history.setType(WebhookMessageType.STICKER.getType());
                    history.setIsRead(1);
                    // only text
                    history.setMessage(message.getPackageId() + "," + message.getStickerId());
                    history.setSentDate(new Date());
                    this.chatHistoryRepository.saveAndFlush(history);
                    this.sendMessageToWebApp(chat, history, "admin");
                } else if (message.getType().equals(WebhookMessageType.IMAGE.getType())) {
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
                    history.setType(WebhookMessageType.IMAGE.getType());
                    history.setIsRead(1);
                    // only text
                    history.setMessage(message.getText());
                    history.setPreviewImageUrl(message.getPreviewImageUrl());
                    history.setOriginalContentUrl(message.getOriginalContentUrl());
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
        message.setType(chatHistory.getType());
        message.setIsRead(Tools.convertIntToBoolean(chatHistory.getIsRead()));
        message.setDate(chatHistory.getSentDate());
        message.setOriginalContentUrl(chatHistory.getOriginalContentUrl());
        message.setPreviewImageUrl(chatHistory.getPreviewImageUrl());
        message.setDisplayName(displayName);
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("history", this.chatHistoryRepository.findChatHistoriesEntityByChatId(chat.getChatId()));
        simpMessagingTemplate.convertAndSendToUser(chat.getName2(), "/private", map);
    }
}
