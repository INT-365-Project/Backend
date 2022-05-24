package INT365.webappchatbot.Services;

import org.apache.commons.io.FileUtils;
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

@Service
public class FileService {

    @Transactional
    public Map<String, String> uploadFile(String fileName, String base64,String originalFileName) {
        Map<String, String> map = new HashMap<>();
        try {
            String filePath = new File(".").getCanonicalPath() + "/src/main/resources/storage/profile/" + fileName + originalFileName.substring(originalFileName.lastIndexOf(".");
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

    public void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }
}
