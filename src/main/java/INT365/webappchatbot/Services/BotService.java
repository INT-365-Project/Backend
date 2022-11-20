package INT365.webappchatbot.Services;

import INT365.webappchatbot.Constants.WebhookMessageType;
import INT365.webappchatbot.Entities.Bot;
import INT365.webappchatbot.Entities.Response;
import INT365.webappchatbot.Models.Bot.BotCommand;
import INT365.webappchatbot.Models.Bot.BotExpression;
import INT365.webappchatbot.Models.Bot.BotObject;
import INT365.webappchatbot.Models.Bot.BotResponse;
import INT365.webappchatbot.Models.Webhook.WebhookEvent;
import INT365.webappchatbot.Models.Webhook.WebhookMessage;
import INT365.webappchatbot.Models.Webhook.WebhookObject;
import INT365.webappchatbot.Models.req.SendingMessageRequest;
import INT365.webappchatbot.Models.resp.TopicResponse;
import INT365.webappchatbot.Repositories.BotRepository;
import INT365.webappchatbot.Repositories.ResponseRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BotService {
    @Autowired
    private BotRepository botRepository;
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private FileService fileService;
    @Value("${http.image.path}")
    private String url;
    private final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final String botTurnOffMessage = "ติดต่อผู้ดูแล"; // for deploy
    private final String getAllTopicMessage = "รวมคำถาม"; // for deploy

    @Transactional
    public BotObject createExpression(BotObject request) {
        if (request.getCommands() != null) {
            for (BotCommand command : request.getCommands()) {
                String requestName = command.getName();
                if (requestName == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name cannot be null");
                } else if (StringUtils.isNotEmpty(requestName)) {
                    if (this.botRepository.findBotsByTopicName(requestName).isEmpty() || this.responseRepository.findResponsesByTopicName(requestName).isEmpty())
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name does not match");
                }
                String name = StringUtils.isEmpty(requestName) ? this.randomName() : requestName;
                command.setName(name);
                String topic = command.getTopic();
                // delete expression and response by topic
                List<Response> tempResponseList = this.responseRepository.findResponsesByTopicName(name);
                this.botRepository.deleteBotsByTopicName(name);
                this.responseRepository.deleteResponsesByTopicName(name);
                // create new expression and response by topic
                for (BotResponse response : command.getResponses()) {
                    Response res = new Response();
                    res.setName(this.randomName());
                    res.setTopic(topic);
                    res.setTopicName(name);
                    if (response.getType().equals(WebhookMessageType.TEXT.getType())) {
                        res.setResponseType(WebhookMessageType.TEXT.getType());
                        res.setResponse(response.getContent());
                    }
                    if (response.getType().equals(WebhookMessageType.IMAGE.getType())) {
                        if (StringUtils.isEmpty(response.getContent())) {
//                            Response tempResponse = this.responseRepository.findResponseByName(response.getName());
                            Response tempResponse = tempResponseList.stream().filter((resp) -> resp.getName().equals(response.getName())).collect(Collectors.toList()).get(0);
                            res.setResponseType(tempResponse.getResponseType());
                            res.setResponse(tempResponse.getResponse());
                        } else {
                            String base64 = response.getContent();
                            String imageExtension = base64.substring(base64.indexOf("/") + 1, base64.indexOf(";", 0));
                            String randomNumber = Tools.randomFileNameNumber();
                            String filePath = this.fileService.uploadFile(randomNumber, base64.split(",", 0)[1], randomNumber + "." + imageExtension, "bot").get("filePath");
                            res.setResponseType(WebhookMessageType.IMAGE.getType());
                            res.setResponse(filePath);
                        }
                    }
                    res.setSeq(response.getSeq());
                    this.responseRepository.saveAndFlush(res);
                }
                for (BotExpression expression : command.getExpressions()) {
                    Bot bot = new Bot();
                    bot.setExpression(expression.getText());
                    bot.setName(this.randomName());
                    bot.setTopic(topic);
                    bot.setTopicName(name);
                    this.botRepository.saveAndFlush(bot);
                }
            }
            return request;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "commands cannot be null");
    }

    public BotObject getAllExpressions() {
        BotObject object = new BotObject();
        List<BotCommand> commandList = new ArrayList<>();
        List<String> nonDuplicatedTopicNameList = new ArrayList<>();
        for (String name : this.botRepository.findAll().stream().map(Bot::getTopicName).collect(Collectors.toList())) {
            if (nonDuplicatedTopicNameList.isEmpty()) nonDuplicatedTopicNameList.add(name);
            else {
                if (!nonDuplicatedTopicNameList.contains(name)) nonDuplicatedTopicNameList.add(name);
            }
        }
        for (String name : nonDuplicatedTopicNameList) {
            BotCommand command = new BotCommand();
            List<BotExpression> expressionList = new ArrayList<>();
            List<BotResponse> responseList = new ArrayList<>();
            for (Bot bot : this.botRepository.findBotsByTopicName(name)) {
                command.setName(bot.getTopicName());
                command.setTopic(bot.getTopic());
                BotExpression expression = new BotExpression();
                expression.setName(bot.getName());
                expression.setText(bot.getExpression());
                expressionList.add(expression);
            }
            command.setExpressions(expressionList);
            for (Response response : this.responseRepository.findResponsesByTopicName(name)) {
                BotResponse res = new BotResponse();
                res.setName(response.getName());
                res.setType(response.getResponseType());
                res.setContent(response.getResponseType().equals(WebhookMessageType.IMAGE.getType()) ? "" : response.getResponse());
                res.setSeq(response.getSeq());
                responseList.add(res);
            }
            command.setResponses(responseList);
            commandList.add(command);
        }
        object.setCommands(commandList);
        return object;
    }

    public List<SendingMessageRequest> responseToWebhook(WebhookObject object, Boolean isConfirm) {
        List<SendingMessageRequest> responseObjectList = new ArrayList<>();
        for (WebhookEvent event : object.getEvents()) {
            String replyToken = event.getReplyToken();
            String userId = event.getSource().getType().equals("user") ? event.getSource().getUserId() : null;
            if (event.getType().equals(WebhookMessageType.MESSAGE.getType())) {
                WebhookMessage message = event.getMessage();
                // case text and message is not null
                if (message != null && message.getType().equals("text")) {
                    SendingMessageRequest msgResponse = new SendingMessageRequest();
                    List<WebhookMessage> messages = new ArrayList<>();
                    msgResponse.setReplyToken(replyToken);
                    msgResponse.setTo(userId);
                    for (Response response : this.getResponseFromText(message.getText(), isConfirm)) {
                        WebhookMessage sendingMessage = new WebhookMessage();
                        if (response.getResponseType().equals(WebhookMessageType.TEXT.getType())) {
                            sendingMessage.setType(response.getResponseType());
                            sendingMessage.setText(response.getResponse());
                        }
                        if (response.getResponseType().equals(WebhookMessageType.IMAGE.getType())) {
                            sendingMessage.setText(response.getResponse());
                            sendingMessage.setType(response.getResponseType());
                            sendingMessage.setPreviewImageUrl(url + response.getName());
                            sendingMessage.setOriginalContentUrl(url + response.getName());
                        }
                        messages.add(sendingMessage);
                    }
                    msgResponse.setMessages(messages);
                    responseObjectList.add(msgResponse);
                }
            } else if (event.getType().equals(WebhookMessageType.FOLLOW.getType())) {
                SendingMessageRequest msgResponse = new SendingMessageRequest();
                List<WebhookMessage> messages = new ArrayList<>();
                msgResponse.setReplyToken(replyToken);
                msgResponse.setTo(userId);
                WebhookMessage sendingMessage = new WebhookMessage();
                sendingMessage.setType(WebhookMessageType.TEXT.getType());
                sendingMessage.setText("ยินดีต้อนรับเข้าสู่ SIT-CHATBOT นะคะ");
                messages.add(sendingMessage);
                msgResponse.setMessages(messages);
                responseObjectList.add(msgResponse);
            } else if (event.getType().equals(WebhookMessageType.UNFOLLOW.getType())) {

            }
        }
        return responseObjectList;
    }

    private List<Response> getResponseFromText(String text, Boolean isConfirm) {
        List<Bot> filteredExpression = this.botRepository.findAll().isEmpty() ? new ArrayList<>() : this.botRepository.findAll().stream().filter((expression) -> expression.getExpression().contains(text) || text.contains(expression.getExpression())).collect(Collectors.toList());
        List<Response> responseList = new ArrayList<>();
        if (isConfirm) {
            Response response = new Response();
            response.setResponseType("text");
            response.setResponse("กรุณารอสักครู่ ทีมงานจะรีบตอบกลับอย่างเร็วที่สุด");
            response.setSeq(0);
            responseList.add(response);
            return responseList;
        }
        if (text.equals(botTurnOffMessage)) {
            Response response = new Response();
            response.setResponseType("text");
            response.setResponse("กรุณาพิมพ์ 'ยืนยัน'\nเมื่อพิมพ์ยืนยันระบบจะทำการปิดการใช้งานแชทบอท และต้องรอทีมงานมาเปิดอีกครั้ง");
            response.setSeq(0);
            responseList.add(response);
            return responseList;
        }
        if (text.equals(getAllTopicMessage)) {
            int count = 0;
            StringBuilder builder = new StringBuilder();
            builder.append("ผู้ใช้งานสามารถใช้งานข้อมูลต่างๆ ที่มีอยู่ในระบบได้จากการพิมพ์คีย์เวิดจากลิสด้านล่างได้เลยค่ะ\n");
            Response response = new Response();
            response.setResponseType("text");
            for (TopicResponse topic : this.getAllTopics()) {
                builder.append(count + 1).append(". ").append(topic.getTopic()).append("\n");
                count++;
            }
            response.setResponse(builder.toString());
            response.setSeq(0);
            responseList.add(response);
            return responseList;
        }
        if (filteredExpression.size() == 0) {
            Response response = new Response();
            response.setResponseType("text");
            response.setResponse("ขอโทษค่ะ อะไรนะคะ");
            response.setSeq(0);
            responseList.add(response);
            return responseList;
//            return this.responseRepository.findResponsesByTopic("ไม่มีหัวข้อ");
        } else {
            for (Bot bot : filteredExpression) {
                responseList.addAll(this.responseRepository.findResponsesByTopic(bot.getTopic()));
            }
        }
        return responseList;
    }

    public List<TopicResponse> getAllTopics() {
        List<TopicResponse> topicResponseList = new ArrayList<>();
        List<String> nonDuplicatedTopicNameList = new ArrayList<>();
        for (Bot bot : this.botRepository.findAll()) {
            if (!nonDuplicatedTopicNameList.isEmpty() && nonDuplicatedTopicNameList.contains(bot.getTopicName())) {
                continue;
            }
            TopicResponse topicResponse = new TopicResponse();
            topicResponse.setTopic(bot.getTopic());
            topicResponse.setTopicName(bot.getTopicName());
            nonDuplicatedTopicNameList.add(bot.getTopicName());
            topicResponseList.add(topicResponse);
        }
        return topicResponseList;
    }

    private String randomName() {
        int max = this.alphabet.length() - 1;
        int min = 0;
        int range = max - min + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            int randomNumber = (int) (Math.random() * range) + min;
            if (i == 4 || i == 9) {
                builder.append("-");
                continue;
            }
            builder.append(alphabet.charAt(randomNumber));
        }
        return builder.toString();
    }
}
