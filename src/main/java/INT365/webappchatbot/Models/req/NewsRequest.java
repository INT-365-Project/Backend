package INT365.webappchatbot.Models.req;

import lombok.Data;

@Data
public class NewsRequest {
    private Long newId;
    private String title;
    private String detail;
    private String thumbnailPath;
}
