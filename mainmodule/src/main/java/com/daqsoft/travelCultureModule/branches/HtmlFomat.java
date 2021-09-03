package com.daqsoft.travelCultureModule.branches;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlFomat {
    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";

    /**
     * 定义img的正则表达式
     */
    private static final String REGEX_IMG = "<img[^>]*?>[\\s\\S]*?<\\//>";
    private static final String REGEX_VIDEO = "<video[^>]*?>[\\s\\S]*?<\\//>";

    public static String delHTMLTag(String htmlStr) {
        // 过滤img标签
        Pattern p_script = Pattern.compile(REGEX_IMG, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        Pattern p_space2 = Pattern.compile(REGEX_VIDEO, Pattern.CASE_INSENSITIVE);
        Matcher m_space2 = p_space.matcher(htmlStr);
        htmlStr = m_space2.replaceAll("");
      /*  // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");


        Pattern a_space = Pattern.compile("&nbsp;", Pattern.CASE_INSENSITIVE);
        Matcher b_space = a_space.matcher(htmlStr);
        htmlStr = b_space.replaceAll("");*/
        htmlStr.replace("video", "");

        return htmlStr.trim(); // 返回文本字符串
    }

}
