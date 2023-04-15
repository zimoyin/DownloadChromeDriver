package org.example.net.download;

import lombok.Data;

import java.util.Date;

@Data
public class ChromePojo {

    private String id;
    private String category;
    private String name;
    private Date date;
    //dir file
    private String type;
    //文件大小
    private long size;
    private String url;
    private Date modified;
}