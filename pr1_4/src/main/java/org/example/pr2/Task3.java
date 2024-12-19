package org.example.pr2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class Task3 {
    public static int calculateChecksum(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);

        ByteBuffer byteBuffer = ByteBuffer.wrap(fileBytes);

        int checksum = 0;

        while (byteBuffer.remaining() > 1) {
            int word = byteBuffer.getShort() & 0xFFFF;

            checksum = (checksum + word) & 0xFFFF;

            if (checksum < word) {
                checksum++;
            }
        }


        if (byteBuffer.remaining() > 0) {
            int lastByte = (byteBuffer.get() & 0xFF) << 8;
            checksum = (checksum + lastByte) & 0xFFFF;

            if (checksum < lastByte) {
                checksum++;
            }
        }


        checksum = ~checksum & 0xFFFF;

        return checksum;
    }
    // Latyshev IKBO-16-21
    public static void main(String[] args) {
        Path filePath = Path.of("src/main/java/org/example/pr2/example.txt");

        try {
            int checksum = calculateChecksum(filePath);
            System.out.printf("Контрольная сумма файла: 0x%04X\n", checksum);
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }
}
