package com.fun.crawl.service;

import com.fun.crawl.model.SysRole;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.dto.SysRoleDTO;
import com.fun.crawl.model.query.SysRoleQuery;

import java.util.List;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
public interface SysRoleService extends BaseService<SysRole> {


    /**
     * 添加角色信息 带权限资源信息
     * @param sysRoleDTO
     * @return
     */
    Boolean save(SysRoleDTO sysRoleDTO);

    /**
     * 更新角色信息 带权限资源信息
     * @param sysRoleDTO
     * @return
     */
    Boolean updateById(SysRoleDTO sysRoleDTO);

    /**
     * 根据id删除角色信息  同时删除与其绑定的资源信息
     * @param roleId
     * @return
     */
    Boolean deleteById(Integer roleId);

    /**
     * 根据角色id查询角色信息与其绑定的资源id
     * @param roleId
     * @return
     */
    SysRoleDTO getRoleInfoWithResourceById(Integer roleId);

    /**
     * 分页条件查询
     * @param query
     * @return
     */
    SysRoleQuery pageByQuery(SysRoleQuery query);

    /**
     * 查询所有的角色
     * @return
     */
    List<SysRole> listSysRole();

}
