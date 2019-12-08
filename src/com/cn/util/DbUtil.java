package com.cn.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cn.config.Logger;
import com.cn.config.LoggerManager;

public class DbUtil {
	private static final Logger logger = LoggerManager.getLogger(DbUtil.class);

	private static String file = null;

	static {
		StringBuilder fileBuilder = new StringBuilder(System.getProperty("user.dir"));
		fileBuilder.append(File.separator).append("src");
		fileBuilder.append(File.separator).append("resources");
		fileBuilder.append(File.separator).append("DbConfig.properties");
		file = fileBuilder.toString();
		PropertiesUtil.init(file);
	}

	private static final String DRIVER = PropertiesUtil.getProperty("driver");
	private static final String URL = PropertiesUtil.getProperty("url");
	private static final String USERNAME = PropertiesUtil.getProperty("username");
	private static final String PASSWORD = PropertiesUtil.getProperty("password");

	/**
	 * -获取连接
	 * 
	 * @return 获取到的连接
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			logger.severe("获取连接接失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			logger.severe("驱动加载失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * -关闭资源
	 * 
	 * @param ps   关闭用于执行sql的PreparedStatement
	 * @param conn 关闭数据库连接
	 */
	public static void close(PreparedStatement ps, Connection conn) {
		try {
			if (ps != null)
				ps.close();
			if (conn != null && !conn.isClosed())
				conn.close();
		} catch (SQLException e) {
			logger.severe("关闭资源失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * -执行查询语句
	 * 
	 * @param conn    数据库连接
	 * @param sql     需要执行的sql语句
	 * @param objects sql语句的参数
	 * @return 结果集
	 */
	public static ResultSet excuseQuery(Connection conn, String sql, Object[] objects) {
		logger.info("sql-->" + sql);
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			if (objects != null && objects.length != 0) {
				for (int i = 0; i < objects.length; i++) {
					ps.setObject(i + 1, objects[i]);
				}
			}
			rs = ps.executeQuery();
		} catch (SQLException e) {
			logger.severe("<--查询失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		logger.info("<--执行成功。。。");
		return rs;
	}

	/**
	 * -执行查询语句，并将结果封装成List<Map<String, Object>>
	 * 
	 * @param conn    数据库连接
	 * @param sql     需要执行的sql语句
	 * @param objects sql语句的参数
	 * @return 结果列表
	 */
	public static List<Map<String, Object>> queryForList(Connection conn, String sql, Object[] params) {
		List<Map<String, Object>> list = new ArrayList<>();
		try {
			ResultSet rs = excuseQuery(conn, sql, params);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Map<String, Object> map = new HashMap<>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
				list.add(map);
			}
		} catch (Exception e) {
			logger.severe("捕获异常。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		} finally {
			close(null, conn);
		}
		return list;
	}

	public static Map<String, Object> queryForMap(Connection conn, String sql, Object[] params) {
		Map<String, Object> map = new HashMap<>();
		try {
			ResultSet rs = excuseQuery(conn, sql, params);
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					map.put(rsmd.getColumnLabel(i), rs.getObject(i));
				}
			}
		} catch (Exception e) {
			logger.severe("捕获异常。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		} finally {
			close(null, conn);
		}
		return map;
	}

	/**
	 * -执行增删改语句
	 * 
	 * @param conn    数据库连接
	 * @param sql     需要执行的sql语句
	 * @param objects sql语句的参数
	 * @return 受影响条数
	 */
	public static int excuseUpdate(Connection conn, String sql, Object[] params) {
		logger.info("sql-->" + sql);
		int i = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			if (params != null && params.length != 0) {
				for (int j = 0; j < params.length; j++) {
					ps.setObject(j + 1, params[j]);
				}
			}
			i = ps.executeUpdate();
		} catch (SQLException e) {
			logger.severe("<--查询失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		} finally {
			close(ps, null);
		}
		logger.info("<--执行成功。。。");
		return i;
	}

	/**
	 * -批量执行增加操作
	 * 
	 * @param conn    数据库连接
	 * @param sql     需要执行的sql语句
	 * @param objects sql语句的参数
	 * @return 受影响条数
	 */
	public static int excuseBatchUpdate(Connection conn, String sql, List<Object[]> params) {
		logger.info("sql-->" + sql);
		List<int[]> retsList = new ArrayList<int[]>();
		int[] rets = null;
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			if (params != null && params.size() != 0) {
				for (int i = 0; i < params.size(); i++) {
					Object[] param = params.get(i);
					if (param.length != 0) {
						for (int j = 0; j < param.length; j++) {
							ps.setObject(j + 1, param[j]);
						}
					}
					ps.addBatch();
					if (i != 0 && i % 10000 == 0) {
						rets = ps.executeBatch();
						ps.clearBatch();
					}
					if (rets != null && rets.length != 0) {
						retsList.add(rets);
						rets = null;
					}
				}
			}
			rets = ps.executeBatch();
			retsList.add(rets);
			conn.commit();
		} catch (SQLException e) {
			logger.severe("<--查询失败。。。");
			logger.info("-----异常信息:" + e.getMessage());
			e.printStackTrace();
		}
		int ret = 0;
		for (int[] rl : retsList) {
			for (int i : rl) {
				if (i == -2) {
					ret++;
				}
			}
		}
		logger.info("<--执行成功。。。");
		close(ps, null);
		return ret;
	}

	public static void main(String[] args) {
		Connection conn = getConnection();
		logger.info("conn:" + conn);
		close(null, conn);
		String sql = "SELECT DATETIME('NOW') AS TIME";
		Map<String, Object> map = queryForMap(getConnection(), sql, null);
		logger.info("map:" + map);
		List<Map<String, Object>> list = queryForList(getConnection(), sql, null);
		logger.info("list:" + list);
	}

}
