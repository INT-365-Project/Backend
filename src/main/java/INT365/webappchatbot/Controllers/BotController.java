package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.Bot.BotObject;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Services.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bot")
public class BotController {

    @Autowired
    private BotService botService;

    @PostMapping("/createOrUpdateBot")
    public ResponseModel<Object> createOrUpdateBot(@RequestBody BotObject request) {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.botService.createExpression(request))
                .build();
    }
}
