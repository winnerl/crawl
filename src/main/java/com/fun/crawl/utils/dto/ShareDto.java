package com.fun.crawl.utils.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ShareDto {

    private String link;
    private String pwd;
    private int expiredType;


}
