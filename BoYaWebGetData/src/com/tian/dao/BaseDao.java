package com.tian.dao;

import org.apache.commons.dbutils.QueryRunner;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.tian.util.JdbcUtils;

public class BaseDao {

	//使用DbUtils操作数据库
	private QueryRunner queryRunner = new QueryRunner();
	
	/**
	 * 执行: Insert\Update\Delete
	 * @param sql
	 * @param args
	 * @return 如果返回-1说明执行失败
	 */
	public int update(String sql,Object... args)
	{
		DruidPooledConnection duConnection = JdbcUtils.getConnection();
		try {
			return queryRunner.update(duConnection, sql,args);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			JdbcUtils.close(duConnection);
		}
		return -1;
	}
}
