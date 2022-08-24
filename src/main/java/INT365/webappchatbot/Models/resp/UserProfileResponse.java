package INT365.webappchatbot.Models.resp;

import lombok.Data;

@Data
public class UserProfileResponse {
    private String displayName;
    private String userId;
    private String language;
    private String pictureUrl;
    private String statusMessage;
}
