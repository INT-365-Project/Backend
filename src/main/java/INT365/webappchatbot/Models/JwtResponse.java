package INT365.webappchatbot.Models;

import java.io.Serializable;

public class JwtResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;

    private final String jwtToken;

    private final UserModelDetail userModelDetail;

    public JwtResponse(String jwtToken, UserModelDetail userModelDetail) {
        this.jwtToken = jwtToken;
        this.userModelDetail = userModelDetail;
    }

    public String getToken() {
        return this.jwtToken;
    }

    public UserModelDetail getUserModelDetail() {
        return userModelDetail;
    }
}
