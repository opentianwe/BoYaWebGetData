package com.tian.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * ���ڷ���HTTP���󹤾���
 * @author BeiBei
 *
 */
public class HttpSendRequest {
	
	/**
	 * ����HTTP����
	 * @param urlParam �����URL
	 * @param requestType ���������
	 * @return
	 */
	public static String sendRequest(String urlParam, String requestType) {
		HttpURLConnection httpURLConnection = null;
		BufferedReader buffer = null;
		StringBuffer resultBuffer = null;
		try {
			URL url = new URL(urlParam);

			// �õ����Ӷ���
			httpURLConnection = (HttpURLConnection) url.openConnection();

			// ��������ʽ
			httpURLConnection.setRequestMethod(requestType);

			// ����������Ҫ���ص��������ͺ��ַ�������
			httpURLConnection.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
			httpURLConnection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.67");
			// �����������
			httpURLConnection.setDoOutput(true);

			// �����������
			httpURLConnection.setDoInput(true);

			// ��ʹ�û���
			httpURLConnection.setUseCaches(false);

			// ��ȡ��Ӧ״̬��
			int response = httpURLConnection.getResponseCode();

			if (response == httpURLConnection.HTTP_OK) {
				// �õ���Ӧ��
				InputStream inputStream = httpURLConnection.getInputStream();

				// ����Ӧ��תΪ�ַ���
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
