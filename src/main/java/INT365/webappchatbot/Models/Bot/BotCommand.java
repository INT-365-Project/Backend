package INT365.webappchatbot.Models.Bot;

import lombok.Data;

import java.util.List;

@Data
public class BotCommand {
    private String name;
    private String topic;
    private List<BotExpression> expressions;
    private List<BotResponse> responses;
}
