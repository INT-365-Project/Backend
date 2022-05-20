package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NEWS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NEWS_SEQ")
    @SequenceGenerator(sequenceName = "NEWS_SEQ", name = "NEWS_SEQ", allocationSize = 1)
    @Column(name = "NEW_ID")
    private Long newId;

    @Column(name = "DETAIL")
    private String detail;

    @Column(name = "THUMBNAIL_PATH")
    private String thumbnailPath;

    @Column(name = "CREATE_DATE", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "CREATE_BY", updatable = false)
    private String createBy;

    @Column(name = "UPDATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "UPDATE_BY")
    private String updateBy;
}