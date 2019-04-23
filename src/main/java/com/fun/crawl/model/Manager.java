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
 * 
 * </p>
 *
 * @author jobob
 * @since 2019-04-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_manager")
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员用户表编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码(加密后)
     */
    @TableField("password")
    private String password;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 密码更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 最近一次登录时间
     */
    @TableField("lastlogin_time")
    private Date lastloginTime;

    /**
     * 前面一次登录时间
     */
    @TableField("prelogin_time")
    private Date preloginTime;

    /**
     * 最近一次登录IP地址
     */
    @TableField("login_ip")
    private String loginIp;

    /**
     * 前面一次登录IP地址
     */
    @TableField("prelogin_ip")
    private String preloginIp;

    /**
     * 是否开启，启用1，关闭0
     */
    @TableField("enable")
    private Integer enable;

    /**
     * 备注
     */
    @TableField("remarks")
    private String remarks;


}
