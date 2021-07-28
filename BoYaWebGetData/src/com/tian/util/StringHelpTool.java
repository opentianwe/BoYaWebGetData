package com.tian.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �ַ�������������
 * @author BeiBei
 */
public class StringHelpTool {
	/**
	 * �����ĺͿո����URL����
	 * @param URL
	 * @return ��������URL
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
	 * �ı�_ȡ�м�_����
	 * 
	 * @param StringSeach      ����Ѱ���ı�
	 * @param StringLeft       ����ı�
	 * @param StringRight      �ұ��ı�
	 * @param Prefix           Ϊȡ�����ı�����ǰ׺
	 * @param Suffix           Ϊȡ�����ı����Ϻ�׺
	 * @param StartingPosition ��Ѱ�ı�����ʼλ��
	 * @param isSensitive      �Ƿ����ִ�Сд
	 * @return ��Ѱ�����ı��б�
	 */
	public static List<String> getIntermediateList(String StringSeach, String StringLeft, String StringRight,
			String Prefix, String Suffix, int StartingPosition, Boolean isSensitive) {
		// ��Ѱ����ʼλ��
		int StartPos = StartingPosition;

		int EndPos = 0;
		if (Prefix == null)
			Prefix = "";
		if (Suffix == null)
			Suffix = "";

		// �м��ı��ĳ���

		int intermediateLength = 0;
		String stringSeach = StringSeach;
		String stringLeft = StringLeft;
		String stringRight = StringRight;

		// ��������
		List<String> retList = new ArrayList<String>();
		retList.clear();

		if (stringSeach == null) {
			return retList;
		}

		// ������ִ�Сд ��ȫ��תΪСд
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
	 * �ı�_ȡ�м��ı�
	 * 
	 * @param StringSeach      ����Ѱ���ı�
	 * @param StringLeft       ����ı�
	 * @param StringRight      �ұ��ı�
	 * @param Prefix           Ϊȡ�����ı�����ǰ׺
	 * @param Suffix           Ϊȡ�����ı����Ϻ�׺
	 * @param StartingPosition ��Ѱ�ı�����ʼλ��
	 * @param isSensitive      �Ƿ����ִ�Сд
	 * @return ��Ѱ�����ı�
	 */
	public static String getIntermediateString(String StringSeach, String StringLeft, String StringRight, String Prefix,
			String Suffix, int StartingPosition, Boolean isSensitive) {
		// ��Ѱ����ʼλ��
		int StartPos = StartingPosition;

		int EndPos = 0;
		if (Prefix == null)
			Prefix = "";
		if (Suffix == null)
			Suffix = "";

		// �м��ı��ĳ���

		int intermediateLength = 0;
		String stringSeach = StringSeach;
		String stringLeft = StringLeft;
		String stringRight = StringRight;
		// ������ִ�Сд ��ȫ��תΪСд
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
