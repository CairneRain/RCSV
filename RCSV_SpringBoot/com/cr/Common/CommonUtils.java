package com.cr.Common;

import java.io.*;

public class CommonUtils {
    public static void writeFile(File file, String content) {
        try {
            file.createNewFile();
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                writer.write(content);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File file) {
        StringBuilder content = new StringBuilder();
        String line = "";
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
                while ((line = reader.readLine()) != null)
                    content.append(line).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return content.toString();
        }
        return null;
    }
}
