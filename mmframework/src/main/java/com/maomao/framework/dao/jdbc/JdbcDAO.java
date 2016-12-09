/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maomao.framework.dao.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.beanutils.ConstructorUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.maomao.framework.support.paglit.PageInfo;
import com.maomao.framework.utils.StringUtils;

/**
 * JDBC 数据访问类
 * 
 * @author maomao
 * 
 */
public class JdbcDAO extends JdbcDaoSupport {
	public static final String BEAN_NAME = "jdbcDAO";

	public static final String KEY_VALUES = "values";

	public static final String KEY_META = "metadata";

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map query(String sql) {
		final Map map = new HashMap();
		List lstValue = new ArrayList();
		map.put(KEY_VALUES, lstValue);
		logger.debug(sql);
		getJdbcTemplate().query(sql, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				map.put(KEY_META, rs.getMetaData());
				int columnSize = rs.getMetaData().getColumnCount();
				List lstField = new ArrayList();
				try {
					String content;
					;
					Object obj;
					for (int i = 1; i <= columnSize; i++) {
						content = "";
						obj = rs.getObject(i);
						if (obj != null) {
							content = rs.getObject(i).toString().trim();
						}

						lstField.add(content);
					}
					((List) map.get(KEY_VALUES)).add(lstField);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		return map;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map query(String sql, Object[] params) {
		final Map map = new HashMap();
		List lstValue = new ArrayList();
		map.put(KEY_VALUES, lstValue);
		logger.debug(sql);
		getJdbcTemplate().query(sql, params, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				map.put(KEY_META, rs.getMetaData());
				int columnSize = rs.getMetaData().getColumnCount();
				List lstField = new ArrayList();
				try {
					String content;
					Object obj;
					for (int i = 1; i <= columnSize; i++) {
						content = "";
						obj = rs.getObject(i);
						if (obj != null) {
							content = rs.getObject(i).toString().trim();
						}

						lstField.add(content);
					}
					((List) map.get(KEY_VALUES)).add(lstField);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		return map;
	}

	@SuppressWarnings("rawtypes")
	public List queryExt(String sql) {
		final List lstValue = new ArrayList();
		getJdbcTemplate().query(sql, new RowCallbackHandler() {
			@SuppressWarnings("unchecked")
			public void processRow(ResultSet rs) throws SQLException {
				Map mp = new HashMap();
				int columnSize = rs.getMetaData().getColumnCount();
				try {
					String content;
					Object obj;
					String columnName;
					for (int i = 1; i <= columnSize; i++) {
						columnName = rs.getMetaData().getColumnName(i);
						content = "";
						obj = rs.getObject(i);
						if (obj != null) {
							content = rs.getObject(i).toString().trim();
						}

						mp.put(columnName, content);
					}
					lstValue.add(mp);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		});
		return lstValue;
	}

	@SuppressWarnings("rawtypes")
	public int getCount(String query) {
		Map mp = query(query);

		if (mp == null)
			return 0;
		List values = (List) mp.get("values");
		if (values == null || values.size() == 0)
			return 0;
		List row = (List) values.get(0);

		if (row == null || row.size() == 0)
			return 0;
		try {
			return Integer.parseInt(row.get(0).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	@SuppressWarnings("rawtypes")
	public int getCount(String query, Object[] params) {
		Map mp = query(query, params);

		if (mp == null)
			return 0;
		List values = (List) mp.get("values");
		if (values == null || values.size() == 0)
			return 0;
		List row = (List) values.get(0);

		if (row == null || row.size() == 0)
			return 0;
		try {
			return Integer.parseInt(row.get(0).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	// jdbc方式分页调用方法
	// public static String Page(String ctsql, JdbcDAO dao) {
	// PageInfo pageInfo = (PageInfo)
	// ParameterWrapper.getWrapper().get("com.cloudcard.PagePolit");
	// int num = 0;
	// num = dao.getCount(ctsql);
	// pageInfo.setRowCount(num);
	// String pageWhere = null;
	// if (num < 20) {
	// pageWhere = "";
	// } else {
	// pageWhere = " limit " + (pageInfo.getCurrentPageIndex() - 1) * 20 + "," +
	// 15;
	// }
	// return pageWhere;
	// }

	/**
	 * 将列名转换为属性名
	 * 
	 * @param columnName
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String columnName2PropertyName(String columnName) {
		String[] ary = columnName.split("_");
		String str = "";
		for (String s : ary) {
			s = s.toLowerCase();
			s = StringUtils.upperCaseTheFirstChar(s);
			str += s;
		}
		return str;
	}

	@SuppressWarnings("rawtypes")
	private Object getColumnValue(ResultSet rs, ResultSetMetaData meta, int index, Class clazz) throws Exception {
		Object value = null;

		int type = meta.getColumnType(index);
		if (clazz == String.class) {
			value = rs.getString(index);
		} else if (clazz == Integer.class) {
			value = rs.getInt(index);
		} else if (clazz == Boolean.class) {
			value = rs.getBoolean(index);
		} else if (clazz == byte[].class) {
			if (type == Types.BLOB)
				value = rs.getBlob(index);
			else
				value = rs.getBytes(index);
		} else if (clazz == Long.class) {
			value = rs.getLong(index);
		} else if (clazz == BigInteger.class) {
			value = rs.getBigDecimal(index);
		} else if (clazz == Float.class) {
			value = rs.getFloat(index);
		} else if (clazz == Double.class) {
			value = rs.getDouble(index);
		} else if (clazz == java.util.Date.class) {
			Timestamp time = rs.getTimestamp(index);
			if (time == null)
				value = null;
			else {
				value = new java.util.Date(time.getTime());
			}
		} else if (clazz == java.sql.Date.class) {
			value = rs.getDate(index);
		} else if (clazz == java.sql.Time.class) {
			value = rs.getTime(index);
		} else if (clazz == java.sql.Timestamp.class) {
			value = rs.getTimestamp(index);
		} else {
			throw new Exception("Cannote determin this column type:" + meta.getColumnName(index));
		}
		return value;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object fetchRowObject(ResultSet rs, Class clazz) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		Object obj = null;
		try {
			obj = ConstructorUtils.invokeConstructor(clazz, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		if (obj == null) {
			throw new SQLException("Cannot instance object; " + clazz.getName());
		}

		int columnCount = meta.getColumnCount();
		String columnName, propertyName;
		Method m;
		PropertyDescriptor pd;
		Object value;
		List<Column2Property> setterColumnsNames = getColumnsFromObj(obj, null);
		for (int i = 1; i <= columnCount; i++) {
			propertyName = null;
			value = null;
			columnName = meta.getColumnName(i);
			for (Column2Property c : setterColumnsNames) {
				if (c.columnName.equals(columnName)) {
					propertyName = c.propertyName;
				}
			}
			if (propertyName == null)
				continue;

			try {
				pd = new PropertyDescriptor(propertyName, clazz);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if (pd != null) {
				m = pd.getWriteMethod();
				Class[] classes = m.getParameterTypes();
				Class c = classes[0];
				try {
					value = getColumnValue(rs, meta, i, c);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if (null != value) {
					try {
						m.invoke(obj, value);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}
		return obj;
	}

	@SuppressWarnings("rawtypes")
	public List<?> queryForObject(String sql, Object[] params, final Class clazz) {
		final List result = new ArrayList();
		getJdbcTemplate().query(sql, params, new RowCallbackHandler() {
			@SuppressWarnings("unchecked")
			public void processRow(ResultSet rs) throws SQLException {
				Object obj = fetchRowObject(rs, clazz);
				if (obj != null) {
					result.add(obj);
				}
			}
		});
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<?> queryForObject(String sql, final Class clazz) {
		final List result = new ArrayList();
		getJdbcTemplate().query(sql, new RowCallbackHandler() {
			@SuppressWarnings("unchecked")
			public void processRow(ResultSet rs) throws SQLException {
				Object obj = fetchRowObject(rs, clazz);
				if (obj != null) {
					result.add(obj);
				}
			}
		});
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<?> queryForObject(String sql, String ctql, final Class clazz, PageInfo pageInfo) throws Exception {
		if (pageInfo == null)
			queryForObject(sql, clazz);

		final List result = new ArrayList();

		int num = getCount(ctql);
		pageInfo.setRowCount(num);
		String pageLimit = null;
		if (num < 20) {
			pageLimit = "";
		} else {
			pageLimit = " limit " + (pageInfo.getCurrentPageIndex() - 1) * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
		}
		sql = sql + pageLimit;

		getJdbcTemplate().query(sql, new RowCallbackHandler() {
			@SuppressWarnings("unchecked")
			public void processRow(ResultSet rs) throws SQLException {
				Object obj = fetchRowObject(rs, clazz);
				if (obj != null) {
					result.add(obj);
				}
			}
		});
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<?> queryForObject(String sql, String ctql, Object[] params, final Class clazz, PageInfo pageInfo) throws Exception {

		if (pageInfo == null)
			return queryForObject(sql, params, clazz);

		final List result = new ArrayList();
		int num = getCount(ctql, params);
		pageInfo.setRowCount(num);
		String pageLimit = null;
		if (num < 0) {
			pageLimit = "";
		} else {
			pageLimit = " limit " + (pageInfo.getCurrentPageIndex() - 1) * pageInfo.getPageSize() + "," + pageInfo.getPageSize();
		}
		sql = sql + pageLimit;

		getJdbcTemplate().query(sql, params, new RowCallbackHandler() {
			@SuppressWarnings("unchecked")
			public void processRow(ResultSet rs) throws SQLException {
				Object obj = fetchRowObject(rs, clazz);
				if (obj != null) {
					result.add(obj);
				}
			}
		});
		return result;
	}

	private boolean _inarray_(String[] p, String str) {
		if (p == null || StringUtils.isEmpty(str))
			return false;

		for (String s : p) {
			if (s != null && s.equals(str)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private Column2Property getIdFromObject(Class clazz) throws Exception {

		// 提取全部属性
		Column2Property c = null;
		for (Field field : clazz.getDeclaredFields()) {
			String columnName = null;
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation a : annotations) {
				if (a instanceof Id) {
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
					c = new Column2Property();
					c.propertyName = pd.getName();
					c.setterMethodName = pd.getWriteMethod().getName();
					c.getterMethodName = pd.getReadMethod().getName();
					c.columnName = columnName;
					break;
				}
			}
		}

		if (c == null) {
			Class superClass = clazz.getSuperclass();
			for (Field field : superClass.getDeclaredFields()) {
				String columnName = null;
				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation a : annotations) {
					if (a instanceof Id) {
						PropertyDescriptor pd = new PropertyDescriptor(field.getName(), superClass);
						c = new Column2Property();
						c.propertyName = pd.getName();
						c.setterMethodName = pd.getWriteMethod().getName();
						c.getterMethodName = pd.getReadMethod().getName();
						c.columnName = columnName;
						break;

					}
				}
			}
		}

		return c;
	}

	@SuppressWarnings("rawtypes")
	private List<Column2Property> getColumnsFromObj(Object obj, String[] columns) {
		Class clazz = obj.getClass();
		List<Column2Property> validColumns = new ArrayList<Column2Property>();

		// 提取全部属性
		for (Field field : clazz.getDeclaredFields()) {
			boolean skip = true;
			String columnName = null;
			Annotation[] annotations = field.getAnnotations();
			for (Annotation a : annotations) {
				if (a instanceof Column) {
					columnName = ((Column) a).name();
					if (columns != null && !_inarray_(columns, columnName))
						skip = true;
					else {
						skip = false;
					}

					break;
				}
			}

			String s = field.getName();
			PropertyDescriptor pd = null;

			if (!skip) {
				// 判断是否存在getter和setter方法
				try {
					pd = new PropertyDescriptor(s, clazz);
					if (pd == null || pd.getWriteMethod() == null || pd.getReadMethod() == null) {
						skip = true;
					}
				} catch (Exception e) {
					skip = true;
				}
			}

			if (!skip) {
				Column2Property c = new Column2Property();
				c.propertyName = pd.getName();
				c.setterMethodName = pd.getWriteMethod().getName();
				c.getterMethodName = pd.getReadMethod().getName();
				c.columnName = columnName;
				validColumns.add(c);
			}
		}

		Column2Property c = new Column2Property();
		c.propertyName = "id";
		c.setterMethodName = "setId";
		c.getterMethodName = "getId";
		c.columnName = "C_ID";
		validColumns.add(c);

		return validColumns;
	}

	@SuppressWarnings("rawtypes")
	private String getTableName(Class clazz) {
		for (Annotation a : clazz.getAnnotations()) {
			if (a instanceof javax.persistence.Table) {
				return ((javax.persistence.Table) a).name();
			}
		}
		return null;
	}

	private void setColumnValue(PreparedStatement st, Column2Property c, MetaData m, Object value, int index) throws Exception {
		m.setColumnValue(st, c, value, index);
	}

	private void setColumnValue(PreparedStatement st, String columnName, MetaData m, Object value, int index) throws Exception {
		Column2Property c = new Column2Property();
		c.columnName = columnName;
		m.setColumnValue(st, c, value, index);
	}

	@SuppressWarnings("rawtypes")
	private void _loadTable_(Connection conn, String tableName, Class clazz) throws Exception {
		String sql = "desc " + tableName;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String fieldName, type, key;
			Table t = new Table();
			t.setTableName(tableName);
			t.setClassName(clazz.getName());
			Map<String, MetaData> metas = new HashMap<String, MetaData>();
			while (rs.next()) {
				MetaData m = new MetaData();
				fieldName = rs.getString("Field");
				type = rs.getString("Type");
				key = rs.getString("Key");
				type = type.toLowerCase();
				m.setColumnName(fieldName);
				m.setType(type);
				if (!StringUtils.isEmpty(key) && key.equals("PRI")) {
					t.setIdColumnName(fieldName);
					t.setIdMetaData(m);
				}
				metas.put(fieldName, m);
			}
			t.setMetas(metas);
			TableMetaManager.getInstance().addMeta(t);
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
		}
	}

	/**
	 * 取得一个对象的查询
	 * 
	 * @param id
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object get(Object idValue, Class clazz) throws Exception {
		String tableName = getTableName(clazz);
		TableMetaManager tableManager = TableMetaManager.getInstance();
		Table t = tableManager.getTable(tableName);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getJdbcTemplate().getDataSource().getConnection();
			if (t == null) {
				_loadTable_(conn, tableName, clazz);
				t = tableManager.getTable(tableName);
			}
			String sql = "select * from " + tableName + " where " + t.getIdColumnName() + " = ? ";
			ps = conn.prepareStatement(sql);
			setColumnValue(ps, t.getIdColumnName(), t.getIdMetaData(), idValue, 1);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Object o = fetchRowObject(rs, clazz);
				return o;
			}
			return null;
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception e) {
			}
		}

	}

	@SuppressWarnings("rawtypes")
	public void save(Object obj, String[] columns) throws Exception {
		// 取得表名
		Class clazz = obj.getClass();
		String tableName = getTableName(clazz);

		if (StringUtils.isEmpty(tableName))
			throw new SQLException("No @Table annotation in Class " + clazz.getName());
		List<Column2Property> setterColumnsNames = getColumnsFromObj(obj, columns);
		if (null == setterColumnsNames || setterColumnsNames.size() == 0)
			throw new SQLException("Column is nul, you must specified update columns.");

		StringBuffer sb = new StringBuffer("insert into " + tableName);
		sb.append(" ( ");
		int size = setterColumnsNames.size();
		Column2Property c;
		for (int i = 0; i < size; i++) {
			c = setterColumnsNames.get(i);
			if (i == 0)
				sb.append(c.columnName);
			else
				sb.append("," + c.columnName);
		}
		sb.append(" ) values ( ");
		for (int i = 0; i < size; i++) {
			c = setterColumnsNames.get(i);
			if (i == 0)
				sb.append("?");
			else
				sb.append(",?");
		}
		sb.append(" ) ");

		Connection conn = null;
		try {
			conn = getJdbcTemplate().getDataSource().getConnection();

			TableMetaManager tableManager = TableMetaManager.getInstance();
			Table t = tableManager.getTable(tableName);
			if (t == null) {
				_loadTable_(conn, tableName, clazz);
				t = tableManager.getTable(tableName);
			}

			if (conn.isClosed()) {
				throw new SQLException("Connection is closed!");
			}

			PreparedStatement st = conn.prepareStatement(sb.toString());
			for (int i = 1; i <= size; i++) {
				Column2Property column = setterColumnsNames.get(i - 1);
				if (obj == null) {
					st.setNull(i, java.sql.Types.NULL);
					continue;
				}

				Object value = MethodUtils.invokeMethod(obj, column.getterMethodName, null);
				if (value == null) {
					st.setNull(i, java.sql.Types.NULL);
					continue;
				}

				setColumnValue(st, column, t.getMeta(column.columnName), value, i);
			}

			st.execute();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void update(Object obj, String[] columns) throws Exception {
		// 取得表名
		Class clazz = obj.getClass();
		String tableName = getTableName(clazz);

		if (StringUtils.isEmpty(tableName))
			throw new SQLException("No @Table annotation in Class " + clazz.getName());
		List<Column2Property> setterColumnsNames = getColumnsFromObj(obj, columns);
		if (null == setterColumnsNames || setterColumnsNames.size() == 0)
			throw new SQLException("Column is nul, you must specified update columns.");

		StringBuffer sb = new StringBuffer("update " + tableName);
		sb.append(" set ");
		int size = setterColumnsNames.size();
		Column2Property c;
		for (int i = 0; i < size; i++) {
			c = setterColumnsNames.get(i);
			if (i == 0)
				sb.append(c.columnName + " = ?");
			else
				sb.append("," + c.columnName + " = ? ");
		}

		Connection conn = null;
		try {
			conn = getJdbcTemplate().getDataSource().getConnection();

			TableMetaManager tableManager = TableMetaManager.getInstance();
			Table t = tableManager.getTable(tableName);
			if (t == null) {
				_loadTable_(conn, tableName, clazz);
				t = tableManager.getTable(tableName);
			}

			sb.append(" where " + t.getIdColumnName() + " = ?");

			if (conn.isClosed()) {
				throw new SQLException("Connection is closed!");
			}
			PreparedStatement st = conn.prepareStatement(sb.toString());
			for (int i = 1; i <= size; i++) {
				Column2Property column = setterColumnsNames.get(i - 1);
				if (obj == null) {
					st.setNull(i, java.sql.Types.NULL);
					continue;
				}

				Object value = MethodUtils.invokeMethod(obj, column.getterMethodName, null);
				if (value == null) {
					st.setNull(i, java.sql.Types.NULL);
					continue;
				}

				setColumnValue(st, column, t.getMeta(column.columnName), value, i);
			}

			// 取得ID值
			Column2Property id = getIdFromObject(obj.getClass());
			Object idValue = MethodUtils.invokeMethod(obj, id.getterMethodName, null);
			setColumnValue(st, t.getIdColumnName(), t.getIdMetaData(), idValue, size + 1);

			st.execute();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void delete(Object idValue, Class clazz) throws Exception {
		String tableName = getTableName(clazz);
		TableMetaManager tableManager = TableMetaManager.getInstance();
		Table t = tableManager.getTable(tableName);
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = getJdbcTemplate().getDataSource().getConnection();
			if (t == null) {
				_loadTable_(conn, tableName, clazz);
				t = tableManager.getTable(tableName);
			}
			String sql = "delete from " + tableName + " where " + t.getIdColumnName() + " = ? ";
			ps = conn.prepareStatement(sql);
			setColumnValue(ps, t.getIdColumnName(), t.getIdMetaData(), idValue, 1);
			ps.execute();
		} finally {
			try {
				conn.close();
				ps.close();
			} catch (Exception e) {
			}
		}
	}

	class Column2Property {
		String propertyName;
		String getterMethodName;
		String setterMethodName;
		String columnName;
	}
}
