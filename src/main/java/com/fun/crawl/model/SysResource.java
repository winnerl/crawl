package com.fun.crawl.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资源表
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_resource")
public class SysResource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 资源名称
     */
    @TableField("name")
    private String name;

    /**
     * 资源类型 0-菜单 1-按钮
     */
    @TableField("type")
    private String type;

    /**
     * 前端url
     */
    @TableField("path")
    private String path;

    /**
     * 按钮权限资源标识
     */
    @TableField("permission")
    private String permission;

    /**
     * 颜色
     */
    @TableField("color")
    private String color;

    /**
     * 父资源id
     */
    @TableField("parent_id")
    private Integer parentId;

    /**
     * 图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 组件路径
     */
    @TableField("component")
    private String component;

    /**
     * 排序权重
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("modify_time")
    private Date modifyTime;

    /**
     * 是否删除 1-删除，0-未删除
     */
    @TableField("del_flag")
    private String delFlag;

    /**
     * 后端路径
     */
    @TableField("url")
    private String url;

    /**
     * 请求方式
     */
    @TableField("method")
    private String method;


}
