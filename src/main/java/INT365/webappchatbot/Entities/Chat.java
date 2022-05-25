package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "CHAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID")
    private Long chatId;

    @Column(name = "NAME_1")
    private String name1;

    @Column(name = "NAME_2")
    private String name2;

    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
}
