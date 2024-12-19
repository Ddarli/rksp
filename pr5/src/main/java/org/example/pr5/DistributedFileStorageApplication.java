package org.example.pr5;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class DistributedFileStorageApplication{

	@Value("${file.storage.location}")
    private String fileStorageLocation;

    @Value("${server.port}")
    private String serverPort;

    @Value("${cluster.nodes}")
    private List<String> clusterNodes;

    public static void main(String[] args) {
        SpringApplication.run(DistributedFileStorageApplication.class, args);
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            Path targetLocation = Paths.get(fileStorageLocation).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String clientIp = getClientIp(request);

            // Synchronize with other nodes
            for (String node : clusterNodes) {
                if (!node.equals("http://localhost:" + serverPort)) {
                    // Implement synchronization logic here
                }
            }

            return "File uploaded successfully: " + fileName + " from IP: " + clientIp;
        } catch (IOException ex) {
            return "Could not upload file: " + fileName;
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        try {
            Path filePath = Paths.get(fileStorageLocation).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) {
                String clientIp = getClientIp(request);
                System.out.println("File " + fileName + " downloaded by IP: " + clientIp);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // Try to fetch from other nodes
                for (String node : clusterNodes) {
                    if (!node.equals("http://localhost:" + serverPort)) {
                        // Implement fetching logic here
                    }
                }
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/files")
    public List<String> listFiles() throws IOException {
        return Files.list(Paths.get(fileStorageLocation))
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @GetMapping("/status")
    public String getStatus(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        return "Node is active on port " + serverPort + ". Client IP: " + clientIp;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }

}
