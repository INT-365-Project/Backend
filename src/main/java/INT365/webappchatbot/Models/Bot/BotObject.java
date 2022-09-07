package INT365.webappchatbot.Models.Bot;

import lombok.Data;

import java.util.List;

@Data
public class BotObject {
    private List<BotCommand> commands;
}
