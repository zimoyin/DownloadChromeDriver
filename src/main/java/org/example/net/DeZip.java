package org.example.net;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DeZip {
    /**
     * 解压ZIP文件
     *
     * @param input           zip路径
     * @param output          解压路径（文件夹）
     * @param saveEmptyFolder 是否保存空目录结构
     * @param zipToRoot       是否将所有文件保存至一个文件夹内
     */
    public static void unzip(final String input, final String output, final boolean saveEmptyFolder, final boolean zipToRoot) throws IOException {
        ZipEntry zipEntry;
        byte[] byteArray = new byte[1024];
        int len;

        try (ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get(input)))) {
            //遍历zip文件中的所有项，并逐个解压到指定的目录中
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Path path = Paths.get(output + "\\" + zipEntry.getName());
                //如果指定将所有文件夹保存在跟路径就执行
                if (zipToRoot) path = Paths.get(output + "\\" + path.getFileName());
                //创建文件路径中存在的文件夹；
                if (zipEntry.isDirectory() && saveEmptyFolder) path.toFile().mkdirs(); //如果是文件夹并且允许存在空目录就将当前路径创建出文件夹
                else if (!zipEntry.isDirectory()) path.getParent().toFile().mkdirs();//如果是文件，则获取文件的父路径，并创建文件夹
                //解压,因为文件夹不存在数据所以跳过文件夹
                if (!zipEntry.isDirectory()) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(path.toString())) {
                        while ((len = zipInputStream.read(byteArray)) != -1) fileOutputStream.write(byteArray, 0, len);
                    }
                }
            }
        }
    }
}
