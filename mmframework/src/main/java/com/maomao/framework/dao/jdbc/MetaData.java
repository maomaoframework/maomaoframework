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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import com.maomao.framework.dao.jdbc.JdbcDAO.Column2Property;

/**
 * 表关系描述数据字典
 * 
 * @author maomao
 * 
 */
/**
 * Table's meta data.
 * @author maomao
 *
 */
public class MetaData {
	private String type;
	private String columnName;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setColumnValue(PreparedStatement st, Column2Property c, Object value, int parameterIndex) throws Exception {
		if (type.startsWith("varchar") || type.startsWith("char") || type.startsWith("text") || "longtext".equals(type)) {
			st.setString(parameterIndex, value.toString());
		} else if (type.startsWith("blob")) {
			if (!(value.getClass() == byte[].class)) {
				throw new SQLException(c.columnName + " is blob type , and the java class getter method must return byte[] instead of " + value.getClass());
			}
			st.setBytes(parameterIndex, (byte[]) value);
		} else if (type.startsWith("integer") || type.startsWith("int") || type.startsWith("tinyint") || type.startsWith("smallint")
				|| type.startsWith("mediumint")) {
			try {
				if (value instanceof Integer) {
					st.setInt(parameterIndex, (Integer) value);
				} else {
					int i = Integer.parseInt(value.toString());
					st.setInt(parameterIndex, i);
				}
			} catch (Exception e) {
				throw new SQLException(c.columnName + " is Integer type , and the java class getter method must return Integer instead of " + value.getClass());
			}
		} else if (type.startsWith("bit")) {
			try {
				if (value instanceof Boolean) {
					st.setBoolean(parameterIndex, (Boolean) value);
				}

			} catch (Exception e) {
				throw new SQLException(c.columnName + " is Integer type , and the java class getter method must return boolean instead of " + value.getClass());
			}

		} else if (type.startsWith("bigint")) {
			if (value instanceof BigInteger) {
				BigInteger b = (BigInteger) value;
				st.setLong(parameterIndex, b.longValue());
			} else if (value instanceof Long) {
				st.setLong(parameterIndex, (Long) value);
			} else if (value instanceof Integer) {
				st.setLong(parameterIndex, (Integer) value);
			} else {
				throw new SQLException(c.columnName + " is BIGINT type , and the java class getter method must return BigInteger instead of "
						+ value.getClass());
			}
		} else if (type.startsWith("float")) {
			if (value instanceof Float) {
				Float f = (Float) value;
				st.setFloat(parameterIndex, f);
			} else if (value instanceof Double) {
				Double d = (Double) value;
				st.setDouble(parameterIndex, d);
			} else {
				try {
					String s = value.toString();
					Float f = Float.parseFloat(s);
					st.setFloat(parameterIndex, f);
				} catch (Exception e) {
					throw new SQLException(c.columnName + " is Float type , and the java class getter method must return Float instead of " + value.getClass());
				}
			}
		} else if (type.startsWith("double")) {
			if (value instanceof Float) {
				Float f = (Float) value;
				st.setFloat(parameterIndex, f);
			} else if (value instanceof Double) {
				Double d = (Double) value;
				st.setDouble(parameterIndex, d);
			} else {
				try {
					String s = value.toString();
					Double f = Double.parseDouble(s);
					st.setDouble(parameterIndex, f);
				} catch (Exception e) {
					throw new SQLException(c.columnName + " is Double type , and the java class getter method must return Double instead of "
							+ value.getClass());
				}
			}
		} else if (type.startsWith("decimal")) {
			if (value instanceof BigDecimal) {
				BigDecimal b = (BigDecimal) value;
				st.setBigDecimal(parameterIndex, b);
			} else if (value instanceof Long) {
				Long l = (Long) value;
				BigDecimal b = BigDecimal.valueOf(l);
				st.setBigDecimal(parameterIndex, b);
			} else if (value instanceof Double) {
				Double l = (Double) value;
				BigDecimal b = BigDecimal.valueOf(l);
				st.setBigDecimal(parameterIndex, b);
			} else if (value instanceof Integer) {
				Integer l = (Integer) value;
				BigDecimal b = BigDecimal.valueOf(l);
				st.setBigDecimal(parameterIndex, b);
			} else if (value instanceof Float) {
				Float l = (Float) value;
				BigDecimal b = BigDecimal.valueOf(l);
				st.setBigDecimal(parameterIndex, b);
			} else {
				throw new SQLException(c.columnName
						+ " is DECIMAL type , and the java class getter method must return BigDecimal/Long/Double/Integer instead of " + value.getClass());
			}
		} else if (type.startsWith("boolean")) {
			if (value instanceof Boolean) {
				Boolean b = (Boolean) value;
				st.setBoolean(parameterIndex, b);
			} else {
				throw new SQLException(c.columnName + " is Boolean type , and the java class getter method must return Boolean instead of " + value.getClass());
			}
		} else if (type.startsWith("date")) {
			if (value instanceof java.sql.Date) {
				java.sql.Date d = (java.sql.Date) value;
				st.setDate(parameterIndex, d);
			} else if (value instanceof java.util.Date) {
				java.util.Date d = (java.util.Date) value;
				java.sql.Date sd = new java.sql.Date(d.getTime());
				st.setDate(parameterIndex, sd);
			} else {
				throw new SQLException(c.columnName + " is Date type , and the java class getter method must return Date instead of " + value.getClass());
			}
		} else if (type.startsWith("time") && !type.equals("timestamp")) {
			if (value instanceof java.sql.Time) {
				java.sql.Time t = (java.sql.Time) value;
				st.setTime(parameterIndex, t);
			} else {
				try {
					String s = value.toString();
					Time t = Time.valueOf(s);
					st.setTime(parameterIndex, t);
				} catch (Exception e) {
					throw new SQLException(c.columnName + " is Time type , and the java class getter method must return Time instead of " + value.getClass());
				}
			}
		} else if (type.startsWith("DATETIME") || type.equals("timestamp")) {
			if (value instanceof java.sql.Date) {
				java.sql.Date d = (java.sql.Date) value;
				st.setDate(parameterIndex, d);
			} else if (value instanceof java.util.Date) {
				java.util.Date d = (java.util.Date) value;
				java.sql.Date sd = new java.sql.Date(d.getTime());
				st.setDate(parameterIndex, sd);
			} else {
				throw new SQLException(c.columnName + " is DateTime type , and the java class getter method must return Date instead of " + value.getClass());
			}
		} else if (type.startsWith("timestamp")) {
			if (value instanceof java.sql.Timestamp) {
				java.sql.Timestamp t = (java.sql.Timestamp) value;
				st.setTimestamp(parameterIndex, t);
			} else {
				try {
					String s = value.toString();
					Timestamp t = Timestamp.valueOf(s);
					st.setTimestamp(parameterIndex, t);
				} catch (Exception e) {
					throw new SQLException(c.columnName + " is Timestamp type , and the java class getter method must return Timestamp instead of "
							+ value.getClass());
				}
			}
		}
	}
}
