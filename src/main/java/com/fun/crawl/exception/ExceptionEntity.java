package com.fun.crawl.exception;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 异常信息封装实体
 *
 * @author rico
 */

@Getter
@Setter
public class ExceptionEntity {

    private String message;

    private String error;

    @JSONField(format = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp = new Date();

    private String path;

}
