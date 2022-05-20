package INT365.webappchatbot.Models.req;

import lombok.Data;

@Data
public class ReportRequest {
    private Long reportId;
    private String topic;
    private String description;
}
