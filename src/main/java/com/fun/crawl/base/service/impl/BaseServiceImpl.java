package com.fun.crawl.base.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fun.crawl.base.mapper.BaseMapper;
import com.fun.crawl.base.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;


public class BaseServiceImpl<K extends BaseMapper<T>, T> extends ServiceImpl<K , T> implements BaseService<T> {

    @Autowired
    protected K baseMapper;

    /**
     * 分页条件查询
     * @param query
     * @return
     */
    public IPage<T> pageByQuery(IPage<T> query) {
        return baseMapper.pageByQuery(query);
    }

}
