package com.fun.crawl.service;

import com.fun.crawl.model.SysResource;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.dto.SysResourceTree;
import com.fun.crawl.model.vo.SysResourceVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
public interface SysResourceService extends BaseService<SysResource> {

    /**
     * 根据角色codes查询菜单树形
     * @param roleCodes
     * @return
     */
    List<SysResourceTree> getMenuTreeByRoleCodes(List<String> roleCodes);

    /**
     * 根据角色codes查询菜单列表
     * @param roleCodes
     * @return
     */
    Set<SysResource> getSysResourceRoleCodes(List<String> roleCodes);

    /**
     * 查询所有的资源
     * @return
     */
    List<SysResourceTree> getAllResourceTree();

    /**
     * 删除资源以及子资源
     * @param id
     * @return
     */
    Boolean deleteResource(Integer id);

    /**
     * 根据角色code查询资源信息
     * @param roleCode
     * @return
     */
    List<SysResource>findResourceByRoleCode(String roleCode);

    List<String> findPermission(List<String> roles);

    Set<SysResourceVO> listResourceByRole(String authority);
}
