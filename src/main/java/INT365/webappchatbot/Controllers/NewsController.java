package INT365.webappchatbot.Controllers;

import INT365.webappchatbot.Constants.ModelConstant;
import INT365.webappchatbot.Models.ResponseModel;
import INT365.webappchatbot.Models.UserModel;
import INT365.webappchatbot.Models.req.NewsRequest;
import INT365.webappchatbot.Services.JwtUserDetailService;
import INT365.webappchatbot.Services.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    JwtUserDetailService userDetailService;
    @Autowired
    private NewsService newsService;

    @GetMapping("")
    public ResponseModel<Object> getNews() {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.newsService.getNews())
                .build();
    }

    @GetMapping("/")
    public ResponseModel<Object> getNewsById(@RequestParam("newsId") Long newsId) {
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.newsService.getNewsById(newsId))
                .build();
    }

    @PostMapping("/createOrUpdateNews")
    public ResponseModel<Object> createOrUpdateNews(@RequestBody NewsRequest request) {
        UserModel user = this.userDetailService.getUserModel(SecurityContextHolder.getContext().getAuthentication());
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(this.newsService.createOrUpdateNews(request, user.getUserModelDetail()))
                .build();
    }

    @DeleteMapping("/deleteNews")
    public ResponseModel<Object> deleteNewsById(@RequestParam("newsId") Long newsId){
        this.newsService.deleteNewsById(newsId);
        return ResponseModel.builder()
                .responseCode(ModelConstant.OK.getCode())
                .responseMessage(ModelConstant.OK.getMessage())
                .responseData(null)
                .build();
    }
}
