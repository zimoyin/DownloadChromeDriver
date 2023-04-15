package org.example.net.download;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.example.net.DeZip;
import org.example.net.httpclient.HttpClientResult;
import org.example.net.httpclient.HttpClientUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 下载浏览器驱动
 */
@Slf4j
public class DownloadChromeDriver {
    public static void main(String[] args) throws IOException {
        new DownloadChromeDriver().autoDownloadDriver();
    }

    public void autoDownloadDriver() throws IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        DownloadChromeDriver driver = new DownloadChromeDriver();
        //TODO
        //获取chrome 版本
        String version;
        if (osName.contains("win")) {
            version = ChromeInfo.getChromeVersionByWindows();
        } else {
            version = ChromeInfo.getChromeVersionByUbuntu();
        }
        if (version == null) throw new NullPointerException("当前电脑没有安装 Chrome");
        List<ChromePojo> driverVersion = driver.getDriverVersion(version);
        ChromePojo win = driverVersion.stream().filter(pojo -> pojo.getName().contains("win")).findFirst().orElse(null);
        if (win == null) throw new NullPointerException("没有可用版本的驱动: " + version);
        File file = driver.downloadChromeDriver(win);//下载
        //解压
        DeZip.unzip(file.getPath(),"./cache/",false,true);
    }

    /**
     * 下载文件
     *
     * @return
     */
    public File downloadChromeDriver(ChromePojo pojo) {
        String url = pojo.getUrl();
        log.info("下载驱动: " + pojo);
        try (HttpClientResult result = HttpClientUtils.doGet(url)) {
            File file = new File("./cache/" + pojo.getName().replace("/", ""));
            file.getParentFile().mkdirs();
            try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
                result.inputStreamToOutSteam(outputStream);
            }
            return file;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取所有的驱动版本
     */
    public List<ChromePojo> getDriverVersions() {
        log.info("获取全部驱动: {}", CD_URL.URL_1);
        try (HttpClientResult result = HttpClientUtils.doGet(CD_URL.URL_1)) {
            String content = result.getContent();
            System.out.println(content);
            return JSON.parseArray(content, ChromePojo.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取具体的驱动
     */
    public List<ChromePojo> getDriverVersion(String version) {
        List<ChromePojo> versions = getDriverVersions();
        List<ChromePojo> pojos = null;
        //x.y.z.d
        String finalVersion = version;
        pojos = versions.stream().filter(chromePojo -> chromePojo.getName().startsWith(finalVersion)).collect(Collectors.toList());
        while (pojos.size() == 0 && version.contains(".")) {
            version = version.substring(0, version.lastIndexOf("."));
            String finalVersion1 = version;
            pojos = versions.stream().filter(chromePojo -> chromePojo.getName().startsWith(finalVersion1)).collect(Collectors.toList());
        }
        if (pojos.size() == 0) return null;
        String finalVersion2 = version;
        log.info("获取到符合要求的驱动列表: {}", pojos);
        //获取最新的驱动
        ChromePojo pojo = pojos.stream().max((o1, o2) -> {
            String s = o1.getName().replaceAll(finalVersion2 + "\\.", "");
            s = s.replace("/", "");
            String s2 = o2.getName().replaceAll(finalVersion2 + "\\.", "");
            s2 = s2.replace("/", "");
            return Integer.parseInt(s) - Integer.parseInt(s2);
        }).orElse(null);
        log.info("获取到的最新驱动: {}", pojo);
        //获取列表
        String url = pojo.getUrl();
        try (HttpClientResult result = HttpClientUtils.doGet(url)) {
            String content = result.getContent();
            return JSON.parseArray(content, ChromePojo.class);
        } catch (Exception e) {
            return null;
        }
    }


    private static final class CD_URL {
        /**
         * <a href="https://chromedriver.storage.googleapis.com/index.html">网站</a>
         */
        public static final String URL_0 = "https://chromedriver.storage.googleapis.com";
        /**
         * <a href="https://registry.npmmirror.com/binary.html?path=chromedriver/">网站</a>
         */
        public static final String URL_1 = "https://registry.npmmirror.com/-/binary/chromedriver/";
    }
}
