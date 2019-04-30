package com.fun.crawl.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SysRoleDTO {


    /**
     * 主键
     */
    private Integer roleId;

    /**
     * 角色code用于springsecurity角色标识码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date modifyTime;

    /**
     * 是否删除 1-删除，0-未删除
     */
    private String delFlag;

    /**
     * 绑定的资源id集合
     */
    private List<Integer> sysResourceIds;

}
