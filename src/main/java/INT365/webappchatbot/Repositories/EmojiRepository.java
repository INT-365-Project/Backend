package INT365.webappchatbot.Repositories;

import INT365.webappchatbot.Entities.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmojiRepository extends JpaRepository<Emoji, Integer> {

    Emoji getEmojiByProductIdAndEmojiId(String productId, String emojiId);
}
