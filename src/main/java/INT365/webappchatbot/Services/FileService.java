package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Value("${storage.chat}")
    private String path;
    private final String profilePath = "/profile/";
    private final String chatPath = "/chat/";
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public Map<String, String> uploadFile(String fileName, String base64, String originalFileName, String type) {
        Map<String, String> map = new HashMap<>();
        try {
//            String filePath = new File(".").getCanonicalPath() + (type.equals("news") ? this.profilePath : this.chatPath) + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            String filePath = this.path + (type.equals("news") ? this.profilePath : this.chatPath) + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(decodedBytes);
            map.put("filePath", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public Map<String, String> uploadFile(String fileName, byte[] source, String originalFileName) {
        Map<String, String> map = new HashMap<>();
        try {
//            String filePath = new File(".").getCanonicalPath() + this.chatPath + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            String filePath = this.path + this.chatPath + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(source);
            map.put("filePath", filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getBase64(String filePath) {
        String base64 = null;
        try {
            byte[] bytes = FileUtils.readFileToByteArray(new File(filePath));
            byte[] encodedBytes = Base64.getEncoder().encode(bytes);
            base64 = new String(encodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }


    public byte[] getImageBytes(Long chatId, Long historyId) {
        ChatHistory chatHistory = chatHistoryRepository.findChatHistoriesEntityByChatId(chatId).stream().filter(history -> Objects.equals(history.getHistoryId(), historyId)).collect(Collectors.toList()).get(0);
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(new File(chatHistory.getMessage()));  // return byte[]
//            bytes = new UrlResource(new File(chatHistory.getMessage()).toPath().toUri()); // return Resource
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }
}
