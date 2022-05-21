package INT365.webappchatbot.Models.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class NewsResponse {
    private Long newId;
    private String title;
    private String detail;
    private String thumbnailPath;
    @JsonFormat(pattern = "YYYY-MM-dd 24HH:MI:SS", timezone = "Asia/Bangkok")
    private Date createDate;
    private String createBy;
    @JsonFormat(pattern = "YYYY-MM-dd 24HH:MI:SS", timezone = "Asia/Bangkok")
    private Date updateDate;
    private String updateBy;
}
