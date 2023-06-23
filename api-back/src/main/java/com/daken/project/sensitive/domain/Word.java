package com.daken.project.sensitive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 敏感词前缀树的节点
 *
 * @author daken
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {
    /**
     * 当前字符
     */
    private char c;
    /**
     * 结束标识
     */
    private boolean end;
    /**
     * 下一层级的敏感词字典
     */
    private Map<Character, Word> next;
    public Word(char c){
        this.c = c;
        this.end = false;
        this.next = new HashMap<>();
    }

}
