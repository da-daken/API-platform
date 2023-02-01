package com.daken.common.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户调用接口关系(UserInterfaceInfo)表实体类
 *
 * @author makejava
 * @since 2022-11-23 11:06:26
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_interface_info")
public class UserInterfaceInfo implements Serializable {
    //主键
    @TableId(type = IdType.AUTO)
    private Long id;
    //调用用户 id
    private Long userId;
    //接口 id
    private Long interfaceInfoId;
    //总调用次数
    private Integer totalNum;
    //剩余调用次数
    private Integer leftNum;
    //0-正常，1-禁用
    private Integer status;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //是否删除(0-未删, 1-已删)
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

