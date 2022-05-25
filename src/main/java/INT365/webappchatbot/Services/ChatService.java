package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.Chat;
import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.resp.ChatHistoryResponse;
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

    @Transactional
    public void saveChat(Message message) {
        // case private chat >> receiverName != null
        if (StringUtils.isNotEmpty(message.getReceiverName())) {
            // find old chat id
            Chat chat = chatRepository.findChatBySenderAndReceiverName(message.getSenderName(), message.getReceiverName());
            // check if old chat doesn't exist, create new one
            if (chat == null) {
                Chat newChat = new Chat();
                newChat.setChatId(this.chatRepository.findLatestId() == null ? 1L : this.chatRepository.findLatestId());
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

    public List<Message> getChatHistory(Message message) {
        Chat chat = this.chatRepository.findChatBySenderAndReceiverName(message.getSenderName(), message.getReceiverName());
        List<ChatHistoryResponse> historyList = this.chatHistoryRepository.findChatHistoriesByChatId(chat == null ? null : chat.getChatId());
        List<Message> messageList = new ArrayList<>();
        for (ChatHistoryResponse chatHistory : historyList) {
            Message returnMessage = new Message();
            message.setMessage(chatHistory.getMessage());
            message.setReceiverName(chatHistory.getReceiverName());
            message.setSenderName(chatHistory.getSenderName());
            message.setDate(chatHistory.getSentDate());
            messageList.add(returnMessage);
        }
        return messageList;
    }
}
