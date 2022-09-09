package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Feigns.ExternalService;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.resp.ChatHistoryObject;
import INT365.webappchatbot.Models.resp.ChatHistoryResponse;
import INT365.webappchatbot.Models.resp.ChatObject;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ChatRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ExternalService externalService;

    @Transactional
    public void saveChat(Message message) {
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
                newChat.setCreateDate(new Date());
                chat = this.chatRepository.saveAndFlush(newChat);
            }
            // create chat history
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setChatId(chat.getChatId() == null ? null : chat.getChatId());
            chatHistory.setSenderName(message.getSenderName());
            chatHistory.setReceiverName(message.getReceiverName());
            chatHistory.setSentDate(message.getDate());
            chatHistory.setMessage(message.getMessage());
            this.chatHistoryRepository.saveAndFlush(chatHistory);
        }
        // case public chat >> receiverName == null
        else {
            // create chat history
            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setChatId(null);
            chatHistory.setSenderName(message.getSenderName());
            chatHistory.setSentDate(message.getDate());
            chatHistory.setMessage(message.getMessage());
            this.chatHistoryRepository.saveAndFlush(chatHistory);
        }
    }

    public List<ChatObject> getChatHistory() {
        List<ChatObject> responseList = new ArrayList<>();
        for (Chat chat : this.chatRepository.findAll()) {
            ChatObject chatObject = new ChatObject();
//            UserProfileResponse userProfile = this.externalService.getUserProfile(chat.getName2());
//            String displayName = userProfile.getDisplayName();
            String displayName = chat.getName2();
            chatObject.setChatId(chat.getChatId());
            chatObject.setDisplayName(displayName);
//            chatObject.setImageUrl(userProfile.getPictureUrl());
            chatObject.setImageUrl("url");
            List<ChatHistoryObject> chatHistoryList = new ArrayList<>();
            for (ChatHistoryResponse chatHistory : this.chatHistoryRepository.findChatHistoriesByChatId(chat.getChatId())) {
                ChatHistoryObject chatHistoryObject = new ChatHistoryObject();
                chatHistoryObject.setMessage(chatHistory.getMessage());
                chatHistoryObject.setSenderName(chatHistory.getSenderName().equals("admin") ? "admin" : displayName);
                chatHistoryObject.setSentDate(chatHistory.getSentDate());
                chatHistoryList.add(chatHistoryObject);
            }
            chatObject.setChatHistory(chatHistoryList);
            responseList.add(chatObject);
        }
        return responseList;
    }
}
