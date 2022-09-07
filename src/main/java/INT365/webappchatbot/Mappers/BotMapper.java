package INT365.webappchatbot.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BotMapper {

    BotMapper INSTANCE = Mappers.getMapper(BotMapper.class);


}
