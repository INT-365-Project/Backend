package INT365.webappchatbot.Models;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserModelDetail {
    private Long userId;
    private String username;
    private String titleNameTh;
    private String firstNameTh;
    private String lastNameTh;
    private String fullName;
    private List<String> roles;
}
