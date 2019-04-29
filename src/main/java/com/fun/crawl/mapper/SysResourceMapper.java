package com.fun.crawl.mapper;

import com.fun.crawl.model.SysResource;
import com.fun.crawl.base.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 资源表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
public interface SysResourceMapper extends BaseMapper<SysResource> {



    /**
     * 根据角色code查询资源集合
     * @param roleCode
     * @return
     */
    List<SysResource> findResourceByRoleCode(String roleCode);

}
