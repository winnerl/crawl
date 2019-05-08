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
     * 根据is_delete全部删除
     * @param isDel
     * @return
     */
    int deleteByIsDelete(long isDel);

    /**
     * 分页查找
     * @param query
     * @return
     */
    FileExtendQuery queryByPage(FileExtendQuery query);


    /**
     * 获取音乐文件下载地址
     * @param oper_id
     * @param mpath
     * @return
     */
    String getMusicUrl(Long oper_id, Long fs_id);


    /**
     * 获取MP4文件播放流
     * @param oper_id
     * @param fs_id  百度文件标识
     * @return
     */
    String getVideoStreamByFsId(Long oper_id, Long fs_id);
}
