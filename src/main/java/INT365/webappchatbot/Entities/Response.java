package INT365.webappchatbot.Entities;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "RESPONSE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Response {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESPONSE_ID")
    private Long responseId;

    @Column(name = "TOPIC")
    private String topic;

    @Column(name = "TOPIC_NAME")
    private String topicName;

    @Column(name = "NAME", updatable = false)
    private String name;

    @Column(name = "RESPONSE_TYPE")
    private String responseType;

    @Column(name = "RESPONSE")
    private String response;

    @Column(name = "SEQ")
    private Integer seq;
}
