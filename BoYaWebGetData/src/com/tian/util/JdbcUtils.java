package com.tian.util;

import java.io.InputStream;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

public class JdbcUtils {
	private static DruidDataSource druidDataSource;
	
	static {
		try {
			Properties properties = new Properties();
		
			//��ȡ�����ļ���Ϣ
			InputStream inputStream = JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
			
			//�����м�������
			properties.load(inputStream);
			
			//�������ݿ����ӳ�
			druidDataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(properties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * ��ȡ���ݿ����ӳ��е�����
	 * @return �������null˵������ʧ��
	 */
	public static DruidPooledConnection getConnection()
	{
		DruidPooledConnection connection = null;
		try {
			connection = druidDataSource.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	/**
	 * �ر�����
	 * @param connection
	 */
	public static void close(DruidPooledConnection connection)
	{
		if(connection!=null)
		{
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
