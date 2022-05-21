package INT365.webappchatbot.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "TITLE_NAME_TH")
    private String titleNameTh;

    @Column(name = "FIRST_NAME_TH")
    private String firstNameTh;

    @Column(name = "LAST_NAME_TH")
    private String lastNameTh;

    @Column(name = "ROLES")
    private String roles;
}
