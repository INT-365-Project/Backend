package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Models.req.FileRequest;
import INT365.webappchatbot.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

//@CrossOrigin(value = "http://localhost:3000", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/viewFileByPath")
    public ResponseModel<Object> viewFileByPath(@RequestBody FileRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("base64", this.fileService.getBase64(request.getFilePath()));
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(map)
                .build();
    }

    @GetMapping("/viewImage/{chatId}/{historyId}")
    public ResponseEntity<Object> viewImage(@PathVariable("chatId") Long chatId, @PathVariable("historyId") Long historyId) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(this.fileService.getImageBytes(chatId, historyId));
    }

    @GetMapping("/viewImage/{name}")
    public ResponseEntity<Object> viewBotImage(@PathVariable("name") String name) {
        return this.fileService.getImageBytes(name);
    }
}
