package com.fun.crawl.utils.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RecentRopotDto {
//    detail	[{"type":3,"path":"/我的资源/刘惜君 - 我很快乐.mp3","fs_id":526056080704766,"category":2,"op_time":1556190572}]
    private long  type;
    private String  path;
    private long  fs_id;
    private long  category;
    private long  op_time;

}
