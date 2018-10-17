package com.ambroz.formula.gamemodel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author Jiri Ambroz <ambroz88@seznam.cz>
 */
public abstract class FileIO {

    public static Properties loadProperties(String filePath) throws IOException {
        Properties prop = new Properties();
        FileInputStream inStream = new FileInputStream(filePath);
        prop.load(inStream);
        return prop;
    }

    public static String readFileToString(String filePath) throws IOException {
        return readFileToString(FileUtils.getFile(filePath));
    }

    public static String readFileToString(File filePath) throws IOException {
        return FileUtils.readFileToString(filePath, "UTF-8");
    }

    public static String getResourceFilePath(String path) {
        return FileIO.class.getClassLoader().getResource(path).getFile();
    }

    public static void saveFile(String content, String filePath) throws IOException {
        try (FileWriter file = new FileWriter(filePath)) {
            file.write(content);
            file.close();
        }
    }
}
