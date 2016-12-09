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

import java.util.HashMap;
import java.util.Map;

/**
 * 表实体
 * @author maomao
 *
 */
public class Table {
	private String tableName;
	private String className;
	private String idColumnName;
	private String idGetterName;
	private String idSetterName;
	private Map<String, MetaData> metas = new HashMap<String, MetaData>();
	private MetaData idMetaData;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, MetaData> getMetas() {
		return metas;
	}

	public void setMetas(Map<String, MetaData> metas) {
		this.metas = metas;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public MetaData getMeta(String columnName) {
		return metas.get(columnName);
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}

	public MetaData getIdMetaData() {
		return idMetaData;
	}

	public void setIdMetaData(MetaData idMetaData) {
		this.idMetaData = idMetaData;
	}

	public String getIdGetterName() {
		return idGetterName;
	}

	public void setIdGetterName(String idGetterName) {
		this.idGetterName = idGetterName;
	}

	public String getIdSetterName() {
		return idSetterName;
	}

	public void setIdSetterName(String idSetterName) {
		this.idSetterName = idSetterName;
	}

}
