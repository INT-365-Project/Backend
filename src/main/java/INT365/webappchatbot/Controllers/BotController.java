package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.Bot.BotObject;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Services.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(value = "http://localhost:3000", allowedHeaders = "*")
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

    @GetMapping("/getAllBot")
    public ResponseModel<Object> getAllBot() {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.botService.getAllExpressions())
                .build();
    }

    @GetMapping("/getAllTopic")
    public ResponseModel<Object> getAllTopic() {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.botService.getAllTopics())
                .build();
    }
}
