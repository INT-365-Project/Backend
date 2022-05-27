package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Models.req.FileRequest;
import INT365.webappchatbot.Services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(value = "http://20.92.229.38", allowedHeaders = "*")
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
}
