package com.lee.ylcommunity.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j(topic = "e")
public class SensitiveFilter {

    private static final String REPLACEMENT = "***";

    private TrieNode root = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyWord;
            while ((keyWord = reader.readLine()) != null) {
                this.addKeyWord(keyWord);
            }
        } catch (IOException e) {
            log.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    /**
     * 前缀树的结点
     */
    private class TrieNode {
        /**
         * 关键词结束标识
         */
        private boolean isKeyWordEnd = false;

        /**
         * 子节点
         */
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeyWordEnd() {
            return isKeyWordEnd;
        }

        public void setKeyWordEnd(boolean keyWordEnd) {
            isKeyWordEnd = keyWordEnd;
        }

        /**
         * 添加子节点
         */
        public void addSubNode(Character c, TrieNode node) {
            this.subNodes.put(c, node);
        }

        /**
         * 获取子节点
         */
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

    /**
     * 将一个敏感词添加到前缀树中
     */
    private void addKeyWord(String keyWord) {
        TrieNode tempNode = root;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);

            if (null == subNode) {
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指针指向下一个
            tempNode = subNode;

            // 结束
            if (i == keyWord.length() - 1) {
                tempNode.setKeyWordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text)){
            return null;
        }
        // 指针1
        TrieNode tempNode = root;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;
        // 结果
        StringBuilder sb = new StringBuilder();

        while(begin < text.length()){
            if(position < text.length()) {
                Character c = text.charAt(position);

                // 跳过符号
                if (isSymbol(c)) {
                    if (tempNode == root) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }

                // 检查下级节点
                tempNode = tempNode.getSubNode(c);
                if (tempNode == null) {
                    // 以begin开头的字符串不是敏感词
                    sb.append(text.charAt(begin));
                    // 进入下一个位置
                    position = ++begin;
                    // 重新指向根节点
                    tempNode = root;
                }
                // 发现敏感词
                else if (tempNode.isKeyWordEnd()) {
                    sb.append(REPLACEMENT);
                    begin = ++position;
                }
                // 检查下一个字符
                else {
                    position++;
                }
            }
            // position遍历越界仍未匹配到敏感词
            else{
                sb.append(text.charAt(begin));
                position = ++begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }

    /**
     * 判断是否是符号
     */
    private boolean isSymbol(Character c) {
        // 0x2E80、0x9FFF代表东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

}
