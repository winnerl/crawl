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
 * 用户表
 * </p>
 *
 * @author jobob
 * @since 2019-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号码
     */
    @TableField("mobile")
    private String mobile;

    /**
     * qq号码
     */
    @TableField("qq")
    private String qq;

    /**
     * 微信号码
     */
    @TableField("wechat")
    private String wechat;

    /**
     * 微博url
     */
    @TableField("weibo")
    private String weibo;

    /**
     * 头像url
     */
    @TableField("avatar")
    private String avatar;

    /**
     * qq openid
     */
    @TableField("qq_openid")
    private String qqOpenid;

    /**
     * 微信openid
     */
    @TableField("wechat_openid")
    private String wechatOpenid;

    /**
     * 微博openid
     */
    @TableField("weibo_openid")
    private String weiboOpenid;

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
     * 是否删除 0-未删除 1-删除
     */
    @TableField("del_flag")
    private String delFlag;


}
