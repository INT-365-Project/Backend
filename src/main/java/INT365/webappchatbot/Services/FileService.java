package INT365.webappchatbot.Services;

import INT365.webappchatbot.Entities.ChatHistory;
import INT365.webappchatbot.Entities.Response;
import INT365.webappchatbot.Repositories.ChatHistoryRepository;
import INT365.webappchatbot.Repositories.ResponseRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
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
    private final String botPath = "/bot/";
    @Autowired
    private ResponseRepository responseRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Transactional
    public Map<String, String> uploadFile(String fileName, String base64, String originalFileName, String type) {
        Map<String, String> map = new HashMap<>();
        try {
//            String filePath = new File(".").getCanonicalPath() + (type.equals("news") ? this.profilePath : this.chatPath) + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            String typePath = "";
            switch (type) {
                case "news":
                    typePath = this.profilePath;
                    break;
                case "chat":
                    typePath = this.chatPath;
                    break;
                case "bot":
                    typePath = this.botPath;
                    break;
            }
            String filePath = this.path + (typePath) + fileName + originalFileName.substring(originalFileName.lastIndexOf("."));
            byte[] decodedBytes = Base64.getDecoder().decode(base64);
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            FileOutputStream outputStream = new FileOutputStream(filePath);
            outputStream.write(decodedBytes);
            map.put("filePath", (typePath) + fileName + originalFileName.substring(originalFileName.lastIndexOf(".")));
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
            map.put("filePath", this.chatPath + fileName + originalFileName.substring(originalFileName.lastIndexOf(".")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getBase64(String filePath) {
        String base64 = null;
        try {
            byte[] bytes = FileUtils.readFileToByteArray(new File(this.path + filePath));
            byte[] encodedBytes = Base64.getEncoder().encode(bytes);
            base64 = new String(encodedBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }


    public byte[] getImageBytes(Long chatId, Long historyId) {
        ChatHistory chatHistory = chatHistoryRepository.findChatHistoriesEntityByChatId(chatId).stream().filter(history -> Objects.equals(history.getHistoryId(), historyId)).collect(Collectors.toList()).get(0);
        if (chatHistory == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chat Id and history Id do not match");
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(new File(this.path + chatHistory.getMessage()));  // return byte[]
//            bytes = new UrlResource(new File(chatHistory.getMessage()).toPath().toUri()); // return Resource
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "chat Id and history Id do not match");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public ResponseEntity<Object> getImageBytes(String name) {
        Response response = this.responseRepository.findResponseByName(name);
        if (response == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name does not match");
        byte[] bytes = null;
        try {
            bytes = FileUtils.readFileToByteArray(new File(this.path + response.getResponse()));  // return byte[]
//            bytes = new UrlResource(new File(chatHistory.getMessage()).toPath().toUri()); // return Resource
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name does not match");
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaType mediaType = MediaType.IMAGE_JPEG;
        switch (response.getResponse().substring(response.getResponse().lastIndexOf("."))) {
            case ".png":
                mediaType = MediaType.IMAGE_PNG;
                break;
            case ".gif":
                mediaType = MediaType.IMAGE_GIF;
                break;
        }
        return ResponseEntity.ok().contentType(mediaType).body(bytes);
    }

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }
}
