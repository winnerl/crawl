package com.fun.crawl.utils.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Garbageguid {
    //        [{"fs_id":391501086535647,"md5":"01c6f152109172e8858760edc56e4c21"}]
    private Long fs_id;
    private String md5;

}
