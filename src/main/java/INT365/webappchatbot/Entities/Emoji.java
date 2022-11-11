package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "EMOJI")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Emoji {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;
    @Column(name = "EMOJI_ID")
    private String emojiId;
    @Column(name = "PRODUCT_ID")
    private String productId;
    @Column(name = "CONTEXT")
    private String context;
}
