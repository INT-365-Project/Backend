package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CHAT_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long historyId;

    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "IS_READ")
    private Integer isRead;

    @Column(name = "SENDER_NAME")
    private String senderName;

    @Column(name = "RECEIVER_NAME")
    private String receiverName;

    @Column(name = "SENT_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate;
}
