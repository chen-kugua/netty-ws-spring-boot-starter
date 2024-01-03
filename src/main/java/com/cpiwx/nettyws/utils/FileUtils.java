package com.cpiwx.nettyws.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author chenPan
 * @date 2023-08-25 10:46
 **/
@Slf4j
public class FileUtils {

    public static File inputStream2File(InputStream ins, String name) {
        File file = new File(FileUtil.getTmpDirPath() + File.separator + name);
        if (file.exists()) {
            return file;
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            IoUtil.copy(ins, out);
            IoUtil.close(ins);
            IoUtil.close(out);
            return file;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("IO流->File异常", e);
        }
    }

    public static String classPathFile2Text(String path) {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));) {
            return reader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
