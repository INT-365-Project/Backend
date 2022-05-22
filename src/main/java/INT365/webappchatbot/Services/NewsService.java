package INT365.webappchatbot.Services;

import INT365.webappchatbot.Mappers.NewsMapper;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.req.NewsRequest;
import INT365.webappchatbot.Models.resp.NewsResponse;
import INT365.webappchatbot.Repositories.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;

    @Transactional
    public Map<String, Long> createOrUpdateNews(NewsRequest request, UserModelDetail user) {
        Long newsId = this.newsRepository.saveAndFlush(NewsMapper.INSTANCE.createNews(request, new Date(), user.getFullName())).getNewId();
        System.out.println(request.getThumbnailFile());
        Map<String, Long> map = new HashMap<>();
        map.put("newsId", newsId);
        return map;
    }

    public List<NewsResponse> getNews() {
        return NewsMapper.INSTANCE.createNewsListResponse(this.newsRepository.findAll());
    }

    public NewsResponse getNewsById(Long newsId) {
        this.newsRepository.findById(newsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "news Id - not found"));
        return NewsMapper.INSTANCE.createNewsResponse(this.newsRepository.findById(newsId).get());
    }

    public void deleteNewsById(Long newsId) {
        this.newsRepository.findById(newsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "news Id - not found"));
        this.newsRepository.deleteById(newsId);
    }
}
