package INT365.webappchatbot.Constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum WebhookMessageType {
    MESSAGE("message"), FOLLOW("follow"), UNFOLLOW("unfollow"),TEXT("text"),STICKER("sticker"),EMOJI("emoji");
    private String type;

    public String getType() {
        return type;
    }
}
