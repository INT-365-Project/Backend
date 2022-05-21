package INT365.webappchatbot.Models.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ReportResponse {
    private Long reportId;
    private String topic;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "YYYY-MM-dd HH:MM:SS", timezone = "Asia/Bangkok")
    private Date createDate;
}
