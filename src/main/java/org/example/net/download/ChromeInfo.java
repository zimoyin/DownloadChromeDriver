package org.example.net.download;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

/**
 * 获取关于Chrome 的信息。目前仅支持 Win 与 Ubuntu
 */
@Slf4j
public class ChromeInfo {
    private ChromeInfo() {
    }


    /**
     * Ubuntu 安装 Chrome
     */
    @Deprecated
    public static void installChromeInUbuntu() throws IOException {
        log.info("Installing Chrome in Ubuntu");
        String[] a = {"sudo", "apt-get", "install", "libxss1", "libappindicator1", "libindicator7"};
        String[] b = {"wget", "https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb"};
        String[] c = {"sudo", "dpkg", "-i", "google-chrome*.deb", "#", "Might", "show", "\"errors\"", ",", "fixed", "by", "next", "line"};
        String[] d = {"sudo", "apt-get", "install", "-f"};

        log.info("exec command: {}",getCMD(a));
        new ProcessBuilder(a).start();
        log.info("exec command: {}",getCMD(b));
        new ProcessBuilder(b).start();
        log.info("exec command: {}",getCMD(c));
        new ProcessBuilder(c).start();
        log.info("exec command: {}",getCMD(d));
        new ProcessBuilder(d).start();
    }

    private static String getCMD(String... a) {
        StringBuilder ag = new StringBuilder();
        for (String s : a) {
            ag.append(s).append(" ");
        }
        return ag.toString();
    }

    /**
     * 获取在 Windows 下的Chrome 的版本号
     */
    public static String getChromeVersionByWindows() {
        String path = null;
        try {
            path = Objects.requireNonNull(getWinChromeInstalledPath()).getCanonicalPath();
        } catch (IOException e) {
            return null;
        }
        path = path.replace("\\", "\\\\");
        String[] command = {"cmd", "/c", "wmic", "datafile", "where", "name='" + path + "'", "get", "Version", "/value"};
        log.info("exec command: {}",getCMD(command));
        try {
            Process process = new ProcessBuilder(command).start();
            try (InputStream inputStream = process.getInputStream()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int size;
                while ((size = inputStream.read(temp)) != -1) {
                    outputStream.write(temp, 0, size);
                }
                return outputStream.toString().replaceAll("\\s*", "").replaceAll("Version=", "");
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取在 Ubuntu 下的Chrome 的版本号
     */
    public static String getChromeVersionByUbuntu() {
        String[] command = {"google-chrome", "-version"};
        try {
            log.info("exec command: {}",getCMD(command));
            Process process = new ProcessBuilder(command).start();
            try (InputStream inputStream = process.getInputStream()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int size;
                while ((size = inputStream.read(temp)) != -1) {
                    outputStream.write(temp, 0, size);
                }
                return outputStream.toString().replaceAll("\\s*", "").replaceAll("GoogleChrome", "");
            }
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 判断ubuntu 系统是否安装了chrome
     */
    public static boolean isChromeInstalledByUbuntu() {
        boolean isChromeInstalled = false;

        String[] command = {"which", "google-chrome"};
        log.info("exec command: {}",getCMD(command));
        try {
            Process process = new ProcessBuilder(command).start();
            String result;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                result = reader.readLine();
            }
            if (result != null) {
                isChromeInstalled = true;
            }
        } catch (IOException e) {
            return false;
        }

        return isChromeInstalled;
    }

    /**
     * windows 是否安装了chrome
     */
    public static boolean isChromeInstalledByWindows() {
        return getWinChromeInstalledPath() != null;
    }

    /**
     * win 系统判断是否安装了Chrome浏览器
     */
    public static File getWinChromeInstalledPath() {
        //通过文件检测是否安装了
        File chromeExe1 = new File("C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        File chromeExe2 = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        //环境变量判断是否安装
        String path = System.getenv("ProgramFiles") + "\\Google\\Chrome\\Application\\chrome.exe";
        File chromeExe3 = new File(path);

        if (chromeExe1.exists()) {
            return chromeExe1;
        }
        if (chromeExe2.exists()) {
            return chromeExe2;
        }
        if (chromeExe3.exists()) {
            return chromeExe3;
        }
        return null;
    }

    public static String getChromeDriver(String path){
        String[] command = {path, "-version"};
        try {
            log.info("exec command: {}",getCMD(command));
            Process process = new ProcessBuilder(command).start();
            try (InputStream inputStream = process.getInputStream()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int size;
                while ((size = inputStream.read(temp)) != -1) {
                    outputStream.write(temp, 0, size);
                }
                return outputStream.toString().replaceAll("\\s*", "").replaceAll("GoogleChrome", "");
            }
        } catch (IOException e) {
            return null;
        }
    }

}
