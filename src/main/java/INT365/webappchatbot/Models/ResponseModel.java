package INT365.webappchatbot.Models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel<T> {
    private String responseCode;
    private String responseMessage;
    private T responseData;
}
