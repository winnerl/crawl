package com.fun.crawl.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Thumbs {
    private String icon;
    private String url1;
    private String url2;
    private String url3;

}
