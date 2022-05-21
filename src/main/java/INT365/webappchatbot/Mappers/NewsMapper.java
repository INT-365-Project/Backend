package INT365.webappchatbot.Mappers;

import INT365.webappchatbot.Entities.News;
import INT365.webappchatbot.Models.req.NewsRequest;
import INT365.webappchatbot.Models.resp.NewsResponse;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.Date;
import java.util.List;

@Mapper
public interface NewsMapper {
    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    @IterableMapping(elementTargetType = NewsResponse.class)
    List<NewsResponse> createNewsListResponse(List<News> newsList);

    NewsResponse createNewsResponse(News news);

    @Mappings({
            @Mapping(source = "request.newId", target = "newId"),
            @Mapping(source = "request.title", target = "title"),
            @Mapping(source = "request.detail", target = "detail"),
            @Mapping(source = "request.thumbnailPath", target = "thumbnailPath"),
            @Mapping(source = "date", target = "createDate"),
            @Mapping(source = "fullName", target = "createBy"),
            @Mapping(source = "date", target = "updateDate"),
            @Mapping(source = "fullName", target = "updateBy"),
    })
    News createNews(NewsRequest request, Date date, String fullName);
}
