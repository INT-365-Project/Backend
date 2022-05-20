package INT365.webappchatbot.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
    private String username;
    private String password;
    private List<String> roles;
    private UserModelDetail userModelDetail;
}
