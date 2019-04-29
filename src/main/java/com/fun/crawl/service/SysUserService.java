package com.fun.crawl.service;

import com.fun.crawl.model.SysUser;
import com.fun.crawl.base.service.BaseService;
import com.fun.crawl.model.dto.SysUserInfoDTO;
import com.fun.crawl.model.query.SysUserVoQuery;
import com.fun.crawl.model.vo.SysUserVo;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
public interface SysUserService extends BaseService<SysUser> {

    /**
     * 通过用户名查找用户
     * @param username
     * @return
     */
    SysUserVo loadUserByUsername(String username);

    /**
     * 通过mobile查找用户
     * @param mobile
     * @return
     */
    SysUserVo loadUserByMobile(String mobile);

    /**
     * 根据userid 与角色信息返回用户详细信息
     * @param userId
     * @param roles
     * @return
     */
    SysUserInfoDTO getUserInfo(Integer userId, List<String> roles);

    /**
     * 用户信息分页查询
     * @param query
     * @return
     */
    SysUserVoQuery pageUserVoByQuery(SysUserVoQuery query);

    /**
     * 添加用户信息（带角色）
     * @param sysUserVo
     * @return
     */
    Boolean save(SysUserVo sysUserVo);

    /**
     * 更新用户信息（带角色）
     * @param sysUserVo
     * @return
     */
    Boolean update(SysUserVo sysUserVo);

    /**
     * 删除用户信息
     * @param userId
     * @return
     */
    Boolean delete(Integer userId);
}
