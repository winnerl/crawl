package com.fun.crawl.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.query.FileExtendQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-04-22
 */
public interface FileExtendService extends BaseService<FileExtend> {

    /**
     * 分页查询
     * @param query
     * @return
     */
    FileExtendQuery pageByQuery(FileExtendQuery query);


    int deleteByIsDelete(long isDel);
}
