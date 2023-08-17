package com.daken.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName sensitive_word
 */
@TableName(value ="sensitive_word")
@Data
public class SensitiveWord implements Serializable {
    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 词语
     */
    @TableField(value = "word")
    private String word;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 0 - 敏感词，1 - 白名单（防止被误杀的，ex：java -> j**a ）
     */
    @TableField(value = "word_type")
    private Integer word_type;
}