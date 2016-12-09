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

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;

public class ConvertResultSetToEntity {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> parseDataEntityBeans(ResultSet rsResult, String strEntity) throws Exception {
		DataTableEntity dataTable = null;
		java.util.List listResult = new java.util.ArrayList();
		Class classEntity = Class.forName(strEntity);
		HashMap hmMethods = new HashMap();
		for (int i = 0; i < classEntity.getDeclaredMethods().length; i++) {
			MethodEntity methodEntity = new MethodEntity();
			String methodName = classEntity.getDeclaredMethods()[i].getName();
			String methodKey = methodName.toUpperCase();
			Class[] paramTypes = classEntity.getDeclaredMethods()[i].getParameterTypes();
			methodEntity.setMethodName(methodName);
			methodEntity.setMethodParamTypes(paramTypes);
			if (hmMethods.containsKey(methodKey)) {
				methodEntity.setRepeatMethodNum(methodEntity.getRepeatMethodNum() + 1);
				methodEntity.setRepeatMethodsParamTypes(paramTypes);
			} else {
				hmMethods.put(methodKey, methodEntity);
			}

		}

		// Deal with ResultSet struct
		if (rsResult != null) {

			ResultSetMetaData rsMetaData = rsResult.getMetaData();
			int columnCount = rsMetaData.getColumnCount();
			dataTable = new DataTableEntity(columnCount);

			// Retrive filed name/type.
			for (int i = 0; i < columnCount; i++) {
				String columnName = rsMetaData.getColumnName(i + 1);
				int columnType = rsMetaData.getColumnType(i + 1);
				dataTable.setColumnName(columnName, i);
				dataTable.setColumnType(columnType, i);
			}
		}

		// Deal with ResultSet metadata.
		while (rsResult.next()) {
			Object objResult = ParseObjectFromResultSet(rsResult, dataTable, classEntity, hmMethods);
			listResult.add(objResult);

		}
		return listResult;

	}

	/**
	 * Parse object from resultset ,and store in object.
	 * 
	 * @param rs
	 * @param dataTable
	 * @param classEntity
	 * @param hsMethods
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object ParseObjectFromResultSet(ResultSet rs,

	DataTableEntity dataTable, Class classEntity,

	java.util.HashMap hsMethods) throws Exception {
		Object objEntity = classEntity.newInstance();
		Method method = null;
		int nColumnCount = dataTable.getColumnCount();
		String[] strColumnNames = dataTable.getColumnNames();

		for (int i = 0; i < nColumnCount; i++) {
			Object objColumnValue = rs.getObject(strColumnNames[i]);
			String strMethodKey = null;
			if (strColumnNames[i] != null) {

				strMethodKey = String.valueOf("SET" + strColumnNames[i].toUpperCase());
				if (strMethodKey.contains("_"))
					strMethodKey = strMethodKey.replaceAll("_", "");
			}

			if (strMethodKey != null) {

				try {
					MethodEntity methodEntity = (MethodEntity) hsMethods.get(strMethodKey);
					String methodName = methodEntity.getMethodName();
					int repeatMethodNum = methodEntity.getRepeatMethodNum();
					Class[] paramTypes = methodEntity.getMethodParamTypes();
					method = classEntity.getMethod(methodName, paramTypes);
					try {
						method.invoke(objEntity, new Object[] { objColumnValue });
					} catch (java.lang.IllegalArgumentException e) {

						for (int j = 1; j < repeatMethodNum; j++) {
							try {
								Class[] repeatParamTypes = methodEntity.getRepeatMethodsParamTypes(j - 1);
								method = classEntity.getMethod(methodName, repeatParamTypes);
								method.invoke(objEntity, new Object[] { objColumnValue });
								break;

							} catch (java.lang.IllegalArgumentException ex) {
								continue;
							}
						}
					}
				} catch (NoSuchMethodException e) {
					throw new NoSuchMethodException();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return objEntity;
	}
}
