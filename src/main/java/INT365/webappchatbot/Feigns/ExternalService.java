package INT365.webappchatbot.Feigns;

import org.springframework.stereotype.Service;

@Service
public class ExternalService {

    private final String lineMessagingApi = "https://api.line.me/v2/bot/message/reply";
}
