package com.fun.crawl.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class FileExtend {
    //百度的字段
    private Long category;
    private Long dir_empty;
    private Long empty;
    private Long fs_id;
    private Long isdir;
    private Long local_ctime;
    private Long local_mtime;
    private Long oper_id;
    private String path;
    private String md5;
    private Long server_ctime;
    private String server_filename;
    private Long server_mtime;
    private Long share;
    private Long size;
    private Thumbs thumbs;
    private Long unlist;
    //自定义数据

    private Long parent_id;
    private List<FileExtend> childrens;





//    JSONObject json = JSONObject.fromObject(FileExtend);
//        JSONObject jsonObject = JSON.parseObject(str);
//String s = JSON.toJSONString(data);
}
