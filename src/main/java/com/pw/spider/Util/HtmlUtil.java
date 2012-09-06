package com.pw.spider.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

	final static String REGEX_HTML_ENTITY = "(<[^>]+?>)"; // 定义HTML标签的正则表达式
	final static String REGEX_HTML = "<[^>]+>"; // 定义HTML标签的正则表达式
	final static String REGEX_SCRIPT = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";// 定义script的正则表达式{或<script[^>]*?>[\s\S]*?<\/script>

	public static String clearHtml(String srcHtml) {
		if (null == srcHtml || srcHtml.equals("")) {
			return srcHtml;
		}
		Pattern p = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		return m.replaceAll("");
	}

	public static boolean existHtmlCode(String htmlStr) {
		if (htmlStr == null || htmlStr.trim().equals("")) {
			return false;
		}
		Pattern p = Pattern
				.compile(REGEX_HTML_ENTITY, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(htmlStr);
		return m.find();
	}

	public static String clearHtmlAndEntity(String srcHtml) {
		if (null == srcHtml || srcHtml.equals("")) {
			return srcHtml;
		}
		Pattern p = Pattern
				.compile(REGEX_HTML_ENTITY, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		return m.replaceAll("");
	}

	public static String filterHtml(String srcHtml, String tag) {
		if (null == srcHtml || srcHtml.equals("")) {
			return srcHtml;
		}

		String regex_tag = "(<)[\\s]*?[\\/]*?[\\s]*?" + tag
				+ "[\\s]*?[\\/]*?[\\s]*?[^>]*?(>)";
		Pattern p = Pattern.compile(regex_tag, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		return m.replaceAll("");
	}

	public static boolean hasHtml(String srcHtml, String tag) {
		if (null == srcHtml || srcHtml.equals("")) {
			return false;
		}

		String regex_tag = "(<)[\\s]*?[\\/]*?[\\s]*?" + tag
				+ "[\\s]*?[\\/]*?[\\s]*?[^>]*?(>)";
		Pattern p = Pattern.compile(regex_tag, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		return m.find();
	}

	public static boolean hasHtml(String srcHtml) {
		if (null == srcHtml || srcHtml.equals("")) {
			return false;
		}
		Pattern p = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		return m.find();
	}

	public static String enCodeHtml(String srcHtml, String tag) {
		if (null == srcHtml || srcHtml.equals("")) {
			return srcHtml;
		}

		String outHtml = srcHtml;
		String regex_tag = "(<)([\\s]*?[\\/]*?[\\s]*?" + tag
				+ "[\\s]*?[\\/]*?[\\s]*?[^>]*?)(>)";

		Pattern p = Pattern.compile(regex_tag, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(srcHtml);
		while (m.find()) {
			outHtml = outHtml.replace(m.group(0), "#@%{%@#" + m.group(2)
					+ "#@%}%@#");
		}
		return outHtml;
	}

	public static String deCodeHtml(String srcHtml) {
		if (null == srcHtml || srcHtml.equals("")) {
			return srcHtml;
		}
		return srcHtml.replaceAll("\\#\\@\\%\\{\\%\\@\\#", "<").replaceAll(
				"\\#\\@\\%\\}\\%\\@\\#", ">");
	}

}