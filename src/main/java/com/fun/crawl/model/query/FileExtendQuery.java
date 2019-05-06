package com.fun.crawl.model.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fun.crawl.model.FileExtend;
import lombok.Data;

@Data
public class FileExtendQuery  extends Page<FileExtend> {


    /**
     * 盘标识，等同于UK
     */
    private Long oper_id;

    /**
     * 文件名称
     */
    private String server_filename;

    /**
     * 是否目录 1  目录，0 不是目录
     */
    private Long isdir;

}
