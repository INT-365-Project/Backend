package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "BOT")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Bot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXPRESSION_ID")
    private Long expressionId;

    @Column(name = "TOPIC")
    private String topic;

    @Column(name = "NAME", updatable = false)
    private String name;

    @Column(name = "EXPRESSION")
    private String expression;
}
