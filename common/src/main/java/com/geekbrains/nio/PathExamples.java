package com.geekbrains.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public class PathExamples {

    public static void main(String[] args) throws IOException {
        
        Path dir = Path.of("common", "..", "common", "..", "common").normalize();
        System.out.println(dir);
        System.out.println(Files.size(dir.resolve("1.txt")));
        System.out.println(dir.toAbsolutePath());
        Path file1 = dir.resolve("1.txt");
        String string = Files.readString(file1);
        System.out.println(string);
        // InputStream stream = Files.newInputStream(file1);
        Files.copy(dir.resolve("image.png"), dir.resolve("copy.png"), StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(file1, "I'm Mike! \n", StandardOpenOption.APPEND);

    }
}
