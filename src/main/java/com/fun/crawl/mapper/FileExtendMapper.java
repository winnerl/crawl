package com.fun.crawl.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fun.crawl.model.FileExtend;
import com.fun.crawl.base.mapper.BaseMapper;
import com.fun.crawl.model.query.FileExtendQuery;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-04-22
 */
public interface FileExtendMapper extends BaseMapper<FileExtend> {

    IPage<FileExtend> queryByPage(FileExtendQuery query);

}
