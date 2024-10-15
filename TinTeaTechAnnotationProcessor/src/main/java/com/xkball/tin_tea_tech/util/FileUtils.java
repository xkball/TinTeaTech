package com.xkball.tin_tea_tech.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    
    public static List<String> readResourcesAllLines(String path) {
        try {
            var it = FileUtils.class.getClassLoader().getResources(path).asIterator();
            var result = new ArrayList<String>();
            while (it.hasNext()) {
                var url = it.next();
                try (var reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    var str = "";
                    while ((str = reader.readLine()) != null) {
                        result.add(str);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static List<String> readAllLines(String path) {
        try {
            
            var result = new ArrayList<String>();
            try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
                var str = "";
                while ((str = reader.readLine()) != null) {
                    result.add(str);
                }
            }
            return result;
        } catch (IOException e) {
            System.out.println("Error reading file " + path);
            return List.of();
        }
    }
}
