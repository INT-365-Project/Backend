package INT365.webappchatbot.Models.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ReportResponse {
    private Long reportId;
    private String topic;
    private String description;
    @JsonFormat(pattern = "YYYY-MM-dd 24HH:MI:SS", timezone = "Asia/Bangkok")
    private Date createDate;
}
