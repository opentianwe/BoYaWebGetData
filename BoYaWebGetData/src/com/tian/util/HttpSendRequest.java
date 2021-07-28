package com.tian.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 用于发送HTTP请求工具类
 * @author BeiBei
 *
 */
public class HttpSendRequest {
	
	/**
	 * 发送HTTP请求
	 * @param urlParam 请求的URL
	 * @param requestType 请求的类型
	 * @return
	 */
	public static String sendRequest(String urlParam, String requestType) {
		HttpURLConnection httpURLConnection = null;
		BufferedReader buffer = null;
		StringBuffer resultBuffer = null;
		try {
			URL url = new URL(urlParam);

			// 得到链接对象
			httpURLConnection = (HttpURLConnection) url.openConnection();

			// 设置请求方式
			httpURLConnection.setRequestMethod(requestType);

			// 设置请求需要返回的数据类型和字符集类型
			httpURLConnection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
			httpURLConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.67");
			// 设置允许输出
			httpURLConnection.setDoOutput(true);

			// 设置允许读入
			httpURLConnection.setDoInput(true);

			// 不使用缓存
			httpURLConnection.setUseCaches(false);

			// 获取响应状态码
			int response = httpURLConnection.getResponseCode();

			if (response == httpURLConnection.HTTP_OK) {
				// 得到响应流
				InputStream inputStream = httpURLConnection.getInputStream();

				// 将响应流转为字符串
				resultBuffer = new StringBuffer();
				String line;
				buffer = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
				while ((line = buffer.readLine()) != null) {
					resultBuffer.append(line);
				}
				return resultBuffer.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
