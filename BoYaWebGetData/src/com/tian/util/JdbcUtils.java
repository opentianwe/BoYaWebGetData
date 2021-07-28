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
		
			//获取配置文件信息
			InputStream inputStream = JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
			
			//从流中加载数据
			properties.load(inputStream);
			
			//创建数据库链接池
			druidDataSource = (DruidDataSource)DruidDataSourceFactory.createDataSource(properties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取数据库连接池中的链接
	 * @return 如果返回null说明链接失败
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
	 * 关闭链接
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
