package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.News;
import INT365.webappchatbot.Mappers.NewsMapper;
import INT365.webappchatbot.Models.UserModelDetail;
import INT365.webappchatbot.Models.req.NewsRequest;
import INT365.webappchatbot.Models.resp.NewsResponse;
import INT365.webappchatbot.Repositories.NewsRepository;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private FileService fileService;

    @Transactional
    public Map<String, Long> createOrUpdateNews(NewsRequest request, UserModelDetail user) {
        News news = this.newsRepository.saveAndFlush(NewsMapper.INSTANCE.createNews(request, new Date(), user.getFullName()));
        if (StringUtils.isNotEmpty(request.getThumbnailFile())) {
            Map<String, String> map = this.fileService.uploadFile(news.getNewId().toString(), request.getThumbnailFile());
            this.newsRepository.updateNewsFilePathByNewsId(news.getNewId(), map.get("filePath"));
        }
        Map<String, Long> map = new HashMap<>();
        map.put("newsId", news.getNewId());
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
