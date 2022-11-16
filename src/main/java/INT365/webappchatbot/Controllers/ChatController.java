package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.Status;
import INT365.webappchatbot.Constants.WebhookMessageType;
import INT365.webappchatbot.Models.Message;
import INT365.webappchatbot.Models.resp.ChatObject;
import INT365.webappchatbot.Services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//@CrossOrigin(value = "http://localhost:3000", allowedHeaders = "*")
@Controller
@RequestMapping("/api")
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
        if (message.getStatus().equals(Status.READ)) {
            this.chatService.setMessageRead(message);
        }
        if (message.getStatus().equals(Status.MESSAGE)) {
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message); // path > /user/{receiverName}/private
            return this.chatService.saveChat(message);
        }
        if (message.getType().equals(WebhookMessageType.IMAGE.getType())) {
            simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", this.chatService.saveChat(message)); // path > /user/{receiverName}/private
        }
        return message;
    }

    @MessageMapping("/getHistory")
    @SendTo("/chatroom/public")
    public List<ChatObject> getChatHistory(@Payload Message message) {
        return this.chatService.getChatHistory();
    }
}
