package com.daken.project.sensitive;

import com.daken.project.sensitive.domain.Word;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 敏感词过滤
 *
 * @author daken
 */
@Component
public class SensitiveWordUtils {
    /**
     * 敏感词前缀树的根结点
     */
    private Word root = new Word(' ');
    /**
     * 替代字符
     */
    private static final char replace = '*';
    /**
     * 遇到这些字符跳过
     */
    private static final String skipChars = " !*-+_=,，.@;；:。~?？()（）[]【】<>《》''/";
    private static final Set<Character> skipSet = new HashSet<>();
    static {
        for (char c : skipChars.toCharArray()){
            skipSet.add(c);
        }
    }

    /**
     * 判断文本中是否存在敏感词
     * @param text
     * @return
     */
    public boolean hasSensitiveWord(String text){
        return !Objects.equals(filter(text), text);
    }

    /**
     * 过滤敏感词并替换为指定字符
     * @param text
     * @return 替换后的文本
     */
    private String filter(String text) {
        // 存储替换后的结果
        StringBuilder res = new StringBuilder(text);
        for(int index = 0; index < res.length(); index++){
            char c = res.charAt(index);
            if(needSkip(c)) {
                index++;
                continue;
            }
            Word word = root;
            int start = index;
            for (int i = index; i < res.length(); i++){
                c = res.charAt(i);
                if(needSkip(c)){
                    continue;
                }
                // 将大写转成小写
                if(c >= 'A' && c <= 'Z'){
                    c += 32;
                }
                word = word.getNext().get(c);
                if(word == null){
                    break;
                }
                if(word.isEnd()){
                    for (int j = start; j <= i; j++){
                        res.setCharAt(j, replace);
                    }
                    index = i;
                }
            }
        }
        return res.toString();
    }

    /**
     * 判断是否需要跳过当前字符
     * @param c
     * @return
     */
    private boolean needSkip(char c) {
        return skipSet.contains(c);
    }

    /**
     * 敏感词set
     * @param words
     */
    public void loadWord(Set<String> words){
        if(!CollectionUtils.isEmpty(words)){
            Word newRoot = new Word(' ');
            words.forEach(word -> build(word, newRoot));
            root = newRoot;
        }
    }

    /**
     * 构建前缀树
     * @param word
     * @param root
     */
    private void build(String word, Word root) {
        if(StringUtils.isBlank(word)){
            return ;
        }
        Word current = root;
        for (int i = 0; i< word.length(); i++){
            char c = word.charAt(i);
            if(c >= 'A' && c <= 'Z'){
                c += 32;
            }
            if(needSkip(c)){
                continue;
            }
            Word next = current.getNext().get(c);
            if(next == null){
                next = new Word(c);
                current.getNext().put(c, next);
            }
            current = next;
        }
        current.setEnd(true);
    }
}
