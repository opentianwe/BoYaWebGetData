package com.tian.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 * @author BeiBei
 */
public class StringHelpTool {
	/**
	 * 对中文和空格进行URL编码
	 * @param URL
	 * @return 编码过后的URL
	 */
	public static String urlEncodeChinese(String url) {
		try {
			Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url);
			String tmp = "";
			while (matcher.find()) {
				tmp = matcher.group();
				url = url.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url.replace(" ", "%20");
	}


	
	
	/**
	 * 文本_取中间_批量
	 * 
	 * @param StringSeach      待搜寻的文本
	 * @param StringLeft       左边文本
	 * @param StringRight      右边文本
	 * @param Prefix           为取出的文本加上前缀
	 * @param Suffix           为取出的文本加上后缀
	 * @param StartingPosition 搜寻文本的起始位置
	 * @param isSensitive      是否区分大小写
	 * @return 搜寻到的文本列表
	 */
	public static List<String> getIntermediateList(String StringSeach, String StringLeft, String StringRight,
			String Prefix, String Suffix, int StartingPosition, Boolean isSensitive) {
		// 搜寻的起始位置
		int StartPos = StartingPosition;

		int EndPos = 0;
		if (Prefix == null)
			Prefix = "";
		if (Suffix == null)
			Suffix = "";

		// 中间文本的长度

		int intermediateLength = 0;
		String stringSeach = StringSeach;
		String stringLeft = StringLeft;
		String stringRight = StringRight;

		// 返回数组
		List<String> retList = new ArrayList<String>();
		retList.clear();

		if (stringSeach == null) {
			return retList;
		}

		// 如果区分大小写 就全部转为小写
		if (!isSensitive) {
			stringSeach = stringSeach.toLowerCase();
			stringLeft = stringLeft.toLowerCase();
			stringRight = stringRight.toLowerCase();
		}
		while (StartPos != -1) {
			StartPos = stringSeach.indexOf(stringLeft, StartPos);
			if (StartPos != -1) {
				StartPos = StartPos + stringLeft.length();
				EndPos = stringSeach.indexOf(StringRight, StartPos);
				if (EndPos != -1) {
					intermediateLength = EndPos - StartPos;
					if (intermediateLength == 0) {
						retList.add(Prefix + "" + Suffix);
						continue;
					}
					try {
						retList.add(Prefix + stringSeach.substring(StartPos, StartPos + intermediateLength) + Suffix);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			} else {
				break;
			}

		}
		return retList;
	}

	
	
	/**
	 * 文本_取中间文本
	 * 
	 * @param StringSeach      待搜寻的文本
	 * @param StringLeft       左边文本
	 * @param StringRight      右边文本
	 * @param Prefix           为取出的文本加上前缀
	 * @param Suffix           为取出的文本加上后缀
	 * @param StartingPosition 搜寻文本的起始位置
	 * @param isSensitive      是否区分大小写
	 * @return 搜寻到的文本
	 */
	public static String getIntermediateString(String StringSeach, String StringLeft, String StringRight, String Prefix,
			String Suffix, int StartingPosition, Boolean isSensitive) {
		// 搜寻的起始位置
		int StartPos = StartingPosition;

		int EndPos = 0;
		if (Prefix == null)
			Prefix = "";
		if (Suffix == null)
			Suffix = "";

		// 中间文本的长度

		int intermediateLength = 0;
		String stringSeach = StringSeach;
		String stringLeft = StringLeft;
		String stringRight = StringRight;
		// 如果区分大小写 就全部转为小写
		if (!isSensitive) {
			stringSeach = stringSeach.toLowerCase();
			stringLeft = stringLeft.toLowerCase();
			stringRight = stringRight.toLowerCase();
		}

		if (stringSeach == null) {
			return "";
		}
		if (StartPos != -1) {
			StartPos = stringSeach.indexOf(stringLeft, StartPos);
			if (StartPos != -1) {
				StartPos = StartPos + stringLeft.length();
				EndPos = stringSeach.indexOf(StringRight, StartPos);
				if (EndPos != -1) {
					intermediateLength = EndPos - StartPos;
					if (intermediateLength == 0) {
						return "";
					}
					return Prefix + stringSeach.substring(StartPos, StartPos + intermediateLength) + Suffix;
				} else {
					return "";
				}
			} else {
				return "";
			}

		}
		return "";
	}
}
