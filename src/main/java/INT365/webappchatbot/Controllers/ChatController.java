package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.Status;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@CrossOrigin("http://20.92.229.38:80")
@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/message") // path > /app/message
    @SendTo("/chatroom/public")
    private Message receivePublicMessage(@Payload Message message) {
        if (message.getStatus().equals(Status.MESSAGE)) {
            this.chatService.saveChat(message);
        }
        return message;
    }

    @MessageMapping("/private-message")
    public Message receivePrivateMessage(@Payload Message message) {
        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message); // path > /user/{receiverName}/private
        if (message.getStatus().equals(Status.MESSAGE)) {
            this.chatService.saveChat(message);
        }
        return message;
    }

    @MessageMapping("/getHistory")
    public List<Message> getChatHistory(@Payload Message message) {
        return this.chatService.getChatHistory(message);
    }
}
