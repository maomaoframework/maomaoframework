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
 * 表结构管理器
 * 
 * @author Huxg
 * 
 */
public class TableMetaManager {
	private static TableMetaManager instance;
	private Map<String, Table> mp;

	public static TableMetaManager getInstance() {
		if (instance == null) {
			instance = new TableMetaManager();
			instance.init();
		}
		return instance;
	}

	private void init() {
		mp = new HashMap<String, Table>();
	}

	public void addMeta(Table t) {
		mp.put(t.getTableName(), t);
	}
	
	public Table getTable(String tableName){
		return mp.get(tableName);
	}
}
