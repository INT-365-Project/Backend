package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Services.WebhookService;
import INT365.webappchatbot.Models.Webhook.WebhookObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(value = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    @Autowired
    private WebhookService webhookService;

    @PostMapping("/test")
    public Object webhookMessageAPI(@RequestBody WebhookObject request) {
        return this.webhookService.webhookMessageAPI(request);
    }

}
