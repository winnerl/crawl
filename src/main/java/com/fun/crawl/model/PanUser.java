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
 * @since 2019-04-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_pan_user")
public class PanUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 网盘信息表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("uid")
    private Long uid;

    /**
     * 百度网盘标识
     */
    @TableField("uk")
    private Long uk;

    /**
     * 用户昵称
     */
    @TableField("pan_name")
    private String panName;

    /**
     * 百度网盘COOKIE
     */
    @TableField("cookie")
    private String cookie;

    /**
     * 百度网盘标识
     */
    @TableField("headers")
    private String headers;

    /**
     * 百度网盘登录信息
     */
    @TableField("jsons")
    private String jsons;

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


}
