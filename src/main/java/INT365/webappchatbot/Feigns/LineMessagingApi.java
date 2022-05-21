package INT365.webappchatbot.Feigns;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface LineMessagingApi {

    @RequestLine("POST")
    @Headers({"Content-Type: application/json","Authorization: Bearer {token}"})
    void getRegFri4000 (@Param(value = "token") String token);


}
