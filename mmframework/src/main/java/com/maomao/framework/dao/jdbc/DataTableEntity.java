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

public class DataTableEntity {
	private int columnCount = 0;
	private String[] columnNames;
	private int[] columnTypes;

	public DataTableEntity() {
		this(0);
	}

	public DataTableEntity(int columnCount) {
		this.columnCount = columnCount;
		this.columnNames = new String[columnCount];
		this.columnTypes = new int[columnCount];
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public String[] getColumnNames() {
		return this.columnNames;
	}

	public String getColumnName(int index) {
		if (index <= this.columnCount) {
			return this.columnNames[index];
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public void setColumnName(String columnName, int index) {
		if (index <= this.columnCount) {
			this.columnNames[index] = columnName;
		} else {
			throw new ArrayIndexOutOfBoundsException();

		}

	}

	public int[] getColumnTypes() {
		return this.columnTypes;
	}

	public int getColumnType(int index) {
		if (index <= this.columnCount) {
			return this.columnTypes[index];
		} else {
			throw new ArrayIndexOutOfBoundsException();

		}

	}

	public void setColumnTypes(int[] columnTypes) {
		this.columnTypes = columnTypes;

	}

	public void setColumnType(int columnType, int index) {
		if (index <= this.columnCount) {
			this.columnTypes[index] = columnType;
		} else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
}
