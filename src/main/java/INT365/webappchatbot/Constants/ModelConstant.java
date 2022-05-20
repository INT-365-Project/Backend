package INT365.webappchatbot.Constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ModelConstant {
    OK("200", "Success"),
    BAD_REQUEST("400", "Bad request");

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
