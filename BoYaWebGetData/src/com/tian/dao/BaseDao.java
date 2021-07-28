package com.tian.dao;

import org.apache.commons.dbutils.QueryRunner;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.tian.util.JdbcUtils;

public class BaseDao {

	//ʹ��DbUtils�������ݿ�
	private QueryRunner queryRunner = new QueryRunner();
	
	/**
	 * ִ��: Insert\Update\Delete
	 * @param sql
	 * @param args
	 * @return �������-1˵��ִ��ʧ��
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
