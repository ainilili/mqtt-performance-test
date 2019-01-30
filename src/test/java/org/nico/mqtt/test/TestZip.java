package org.nico.mqtt.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestZip {

    public static void main(String[] args) throws IOException {
        String zipFileName="E:\\Downloads\\apache-jmeter-5.0.zip";
        String outPath ="E:\\Downloads\\test";
        FileInputStream fileInputStream = new FileInputStream(zipFileName);
        ZipInputStream inputStream = new ZipInputStream(fileInputStream);

        
        
        ZipEntry nextEntry = null;
        while ((nextEntry = inputStream.getNextEntry()) != null) {
            System.out.println(nextEntry.getName() + " - " + nextEntry.getSize());
//            File file = new File(outPath + "\\" + nextEntry.getName());
//            file.setWritable(true, false);
//            if(nextEntry.isDirectory()) {
//                file.mkdirs();
//            }else {
//                file.createNewFile();
//            }
//            OutputStream os = new FileOutputStream(file);
//            BufferedOutputStream bos = new BufferedOutputStream(os);
//            byte[] bs = new byte[2048];
//            int a = -1;
//            while((a = inputStream.read(bs)) != -1) {
//                bos.write(bs, 0, a);
//            }
        }
    }
}
