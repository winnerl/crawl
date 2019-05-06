package com.fun.crawl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fun.crawl.base.service.impl.BaseServiceImpl;
import com.fun.crawl.mapper.FileExtendMapper;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.model.query.FileExtendQuery;
import com.fun.crawl.service.FileExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-04-22
 */
@Service
public class FileExtendServiceImpl extends BaseServiceImpl<FileExtendMapper, FileExtend> implements FileExtendService {


    @Autowired
    private FileExtendMapper fileExtendMapper;


    /**
     * 分页查询
     *
     * @param query
     * @return
     */
    @Override
    public FileExtendQuery pageByQuery(FileExtendQuery query) {
        fileExtendMapper.pageByQuery(query);
        return query;
    }

    @Override
    public int deleteByIsDelete(long isDel) {
        QueryWrapper<FileExtend> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(FileExtend::getIs_exist, 0L);
        return fileExtendMapper.delete(wrapper);
    }


}
