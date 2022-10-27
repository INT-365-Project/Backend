package INT365.webappchatbot.Services;

import INT365.webappchatbot.Constants.WebhookMessageType;
import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.Webhook.WebhookMessage;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.ChatHistoryObject;
import INT365.webappchatbot.Models.resp.ChatHistoryResponse;
import INT365.webappchatbot.Models.resp.ChatObject;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private ExternalService externalService;
    @Value("${http.image.path}")
    private String imagePath;

    @Transactional
    public Message saveChat(Message message) {
        // case private chat >> receiverName != null
        if (StringUtils.isNotEmpty(message.getReceiverName())) {
            // find old chat id
            Chat chat = chatRepository.findChatBySenderAndReceiverName(message.getSenderName(), message.getReceiverName());
            // check if old chat doesn't exist, create new one
            if (chat == null) {
                Chat newChat = new Chat();
                newChat.setChatId(null);
                newChat.setName1(message.getSenderName());
                newChat.setName2(message.getReceiverName());
                newChat.setIsBotResponse(1);
                newChat.setCreateDate(new Date());
                chat = this.chatRepository.saveAndFlush(newChat);
            }
            // create chat history
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setChatId(chat.getChatId() == null ? null : chat.getChatId());
            chatHistory.setSenderName(message.getSenderName());
            chatHistory.setReceiverName(message.getReceiverName());
            chatHistory.setType(message.getType());
            chatHistory.setMessage(message.getMessage());
            if (message.getType().equals(WebhookMessageType.IMAGE.getType())) {
                String base64 = message.getMessage();
                String imageExtension = base64.substring(base64.indexOf("/") + 1, base64.indexOf(";", 0));
                String randomNumber = this.randomNumber();
                String filePath = this.fileService.uploadFile(randomNumber, message.getMessage().split(",", 0)[1], randomNumber + "." + imageExtension, "chat").get("filePath");
                chatHistory.setMessage(filePath);
                message.setMessage(filePath);
            }
            chatHistory.setIsRead(0);
            chatHistory.setSentDate(message.getDate());
            chatHistory = this.chatHistoryRepository.saveAndFlush(chatHistory);
            // set image url
            if (message.getType().equals(WebhookMessageType.IMAGE.getType())) {
                String url = imagePath + chatHistory.getChatId() + "/" + chatHistory.getHistoryId();
                chatHistory.setOriginalContentUrl(url);
                chatHistory.setPreviewImageUrl(url);
                message.setOriginalContentUrl(url);
                message.setPreviewImageUrl(url);
            }
            this.chatHistoryRepository.saveAndFlush(chatHistory);
            // set message send to line
            SendingMessageRequest request = new SendingMessageRequest();
            List<WebhookMessage> webhookMessageList = new ArrayList<>();
            WebhookMessage webhookMessage = new WebhookMessage();
            webhookMessage.setText(message.getMessage());
            webhookMessage.setType("text");
            webhookMessageList.add(webhookMessage);
            request.setTo(chat.getName2());
            request.setMessages(webhookMessageList);
//            this.externalService.pushMessage(request); // for deploy
            message.setChatId(chat.getChatId());
//            message.setDisplayName(message.getSenderName().equals("admin") ? "admin" : this.externalService.getUserProfile(chat.getName2()).getDisplayName());
            // ^ for deploy
            message.setDisplayName(message.getSenderName().equals("admin") ? "admin" : chat.getName2());
            // ^ for local
            return message;
        }
        // case public chat >> receiverName == null
        else {
            // create chat history
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setChatId(null);
            chatHistory.setSenderName(message.getSenderName());
            chatHistory.setSentDate(message.getDate());
            chatHistory.setType(message.getType());
            chatHistory.setMessage(message.getMessage());
            chatHistory.setIsRead(0);
            this.chatHistoryRepository.saveAndFlush(chatHistory);
            return null;
        }
    }

    public List<ChatObject> getChatHistory() {
        List<ChatObject> responseList = new ArrayList<>();
        for (Chat chat : this.chatRepository.findAll()) {
            ChatObject chatObject = new ChatObject();
//            UserProfileResponse userProfile = this.externalService.getUserProfile(chat.getName2()); // for deploy
//            String displayName = userProfile.getDisplayName(); // for deploy
            String displayName = chat.getName2();
            chatObject.setChatId(chat.getChatId());
            chatObject.setUserId(chat.getName2());
            chatObject.setDisplayName(displayName);
//            chatObject.setImageUrl(userProfile.getPictureUrl()); // for deploy
            chatObject.setImageUrl("url"); // for local
            List<ChatHistoryObject> chatHistoryList = new ArrayList<>();
            for (ChatHistoryResponse chatHistory : this.chatHistoryRepository.findChatHistoriesByChatId(chat.getChatId())) {
                ChatHistoryObject chatHistoryObject = new ChatHistoryObject();
                chatHistoryObject.setMessage(chatHistory.getMessage());
                chatHistoryObject.setType(chatHistory.getType());
                chatHistoryObject.setIsRead(Tools.convertIntToBoolean(chatHistory.getIsRead()));
                chatHistoryObject.setSenderName(chatHistory.getSenderName().equals("admin") ? "admin" : chatHistory.getSenderName());
                chatHistoryObject.setReceiverName(chatHistory.getReceiverName().equals("admin") ? "admin" : chatHistory.getReceiverName());
                chatHistoryObject.setOriginalContentUrl(chatHistory.getOriginalContentUrl());
                chatHistoryObject.setPreviewImageUrl(chatHistory.getPreviewImageUrl());
                chatHistoryObject.setSentDate(chatHistory.getSentDate());
                chatHistoryList.add(chatHistoryObject);
            }
            chatObject.setChatHistory(chatHistoryList);
            responseList.add(chatObject);
        }
        return responseList;
    }

    public ChatObject getOneChatHistory(Long chatId, String displayName, String imageUrl) {
        ChatObject response = new ChatObject();
        Chat chat = this.chatRepository.findChatByChatId(chatId);
        response.setDisplayName(displayName);
        response.setUserId(chat.getName2());
        response.setChatId(chat.getChatId());
        response.setImageUrl(imageUrl);
        List<ChatHistoryObject> chatHistoryList = new ArrayList<>();
        for (ChatHistoryResponse chatHistory : this.chatHistoryRepository.findChatHistoriesByChatId(chat.getChatId())) {
            ChatHistoryObject chatHistoryObject = new ChatHistoryObject();
            chatHistoryObject.setMessage(chatHistory.getMessage());
            chatHistoryObject.setType(chatHistory.getType());
            chatHistoryObject.setSenderName(chatHistory.getSenderName().equals("admin") ? "admin" : chatHistory.getSenderName());
            chatHistoryObject.setReceiverName(chatHistory.getReceiverName().equals("admin") ? "admin" : chatHistory.getReceiverName());
            chatHistoryObject.setSentDate(chatHistory.getSentDate());
            chatHistoryObject.setOriginalContentUrl(chatHistory.getOriginalContentUrl());
            chatHistoryObject.setPreviewImageUrl(chatHistory.getPreviewImageUrl());
            chatHistoryObject.setIsRead(Tools.convertIntToBoolean(chatHistory.getIsRead()));
            chatHistoryList.add(chatHistoryObject);
        }
        response.setChatHistory(chatHistoryList);
        return response;
    }

    public void setMessageRead(Message message) {
        Chat chat = chatRepository.findChatBySenderAndReceiverName(message.getSenderName(), message.getReceiverName());
        if (chat == null) {
            return;
        }
        for (ChatHistory chatHistory : this.chatHistoryRepository.findChatHistoriesEntityByChatId(chat.getChatId())) {
            chatHistory.setIsRead(1);
            this.chatHistoryRepository.saveAndFlush(chatHistory);
        }
    }

    private String randomNumber() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder stringBuilder = new StringBuilder();
        int max = alphabet.length() - 1;
        int min = 0;
        int range = max - min + 1;
        for (int i = 0; i < 8; i++) {
            int randomNumber = (int) (Math.random() * range) + min;
            stringBuilder.append(alphabet.charAt(randomNumber));
        }
        return stringBuilder.toString();
    }
}
