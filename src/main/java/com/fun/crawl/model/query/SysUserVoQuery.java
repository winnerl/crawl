package com.fun.crawl.model.query;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fun.crawl.model.vo.SysUserVo;
import lombok.Data;

@Data
public class SysUserVoQuery extends Page<SysUserVo> {

    /**
     * 用户名
     */
    private String username;

}
