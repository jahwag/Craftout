package com.mojang.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class Utils {

    public static ByteBuffer loadFileIntoBuffer(String fileName) throws IOException {
        File file = new File(fileName);
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
            return buffer;
        } else {
            throw new FileNotFoundException(fileName);
        }
    }

}
