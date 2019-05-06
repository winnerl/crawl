package com.fun.crawl.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author winner
 * @since 2019-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_file_extend")
public class FileExtend implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理员用户表编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父类ID
     */
    @TableField("parent_id")
    private Long parent_id;

    /**
     * 文件分类
     */
    @TableField("category")
    private Long category;

    @TableField("dir_empty")
    private Long dir_empty;

    @TableField("empty")
    private Long empty;

    @TableField("fs_id")
    private Long fs_id;

    /**
     * 是否目录 1  目录，0 不是目录
     */
    @TableField("isdir")
    private Long isdir;

    @TableField("local_ctime")
    private Long local_ctime;

    @TableField("local_mtime")
    private Long local_mtime;

    @TableField("oper_id")
    private Long oper_id;

    @TableField("path")
    private String path;

    @TableField("md5")
    private String md5;

    @TableField("server_ctime")
    private Long server_ctime;

    @TableField("server_filename")
    private String server_filename;

    @TableField("server_mtime")
    private Long server_mtime;

    @TableField("share")
    private Long share;

    @TableField("size")
    private Long size;

    @TableField("unlist")
    private Long unlist;


    @TableField("modify_time")
    private Date modify_time;


    @TableField("create_time")
    private Date create_time;

    @TableField("is_exist")
    private Long is_exist;




}
