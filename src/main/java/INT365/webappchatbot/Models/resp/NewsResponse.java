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
    private String thumbnailFileName;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "YYYY-MM-dd HH:MM:SS", timezone = "Asia/Bangkok")
    private Date createDate;
    private String createBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "YYYY-MM-dd HH:MM:SS", timezone = "Asia/Bangkok")
    private Date updateDate;
    private String updateBy;
}
