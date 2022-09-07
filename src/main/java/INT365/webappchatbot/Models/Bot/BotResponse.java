package INT365.webappchatbot.Models.Bot;

import lombok.Data;

@Data
public class BotResponse {
    private String type;
    private String content;
    private Integer seq;
}
