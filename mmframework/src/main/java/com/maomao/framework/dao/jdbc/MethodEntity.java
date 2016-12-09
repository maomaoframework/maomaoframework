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

import java.util.ArrayList;

/**
 * The method entity.
 * 
 * @author maomao
 * 
 */
public class MethodEntity {
	// method name
	private String methodName;

	// The override method count.
	private int repeatMethodNum = 1;

	// method param types
	@SuppressWarnings("rawtypes")
	private Class[] methodParamTypes;

	// Overrided methods types
	@SuppressWarnings("rawtypes")
	private ArrayList repeatMethodsParamTypes;

	/**
	 * Get the method name
	 * 
	 * @return
	 */
	public String getMethodName() {

		return methodName;

	}

	/**
	 * Get the method types
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getMethodParamTypes() {

		return methodParamTypes;

	}

	/**
	 * Set the method name.
	 * 
	 * @param string
	 */
	public void setMethodName(String string) {

		methodName = string;

	}

	/**
	 * Set the method param types.
	 * 
	 * @param classes
	 */
	@SuppressWarnings("rawtypes")
	public void setMethodParamTypes(Class[] classes) {
		methodParamTypes = classes;

	}

	/**
	 * Get the override method count.
	 * 
	 * @return
	 */
	public int getRepeatMethodNum() {

		return repeatMethodNum;

	}

	/**
	 * Get the override method param types.
	 * 
	 * @param i
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class[] getRepeatMethodsParamTypes(int i) {

		int count = this.repeatMethodsParamTypes.size();

		if (i <= count) {

			return (Class[]) this.repeatMethodsParamTypes.get(i);

		} else {

			throw new ArrayIndexOutOfBoundsException();

		}

	}

	/**
	 * Reset the override method count
	 * 
	 * @param i
	 */
	public void setRepeatMethodNum(int i) {
		repeatMethodNum = i;
	}

	/**
	 * Set the override method param types
	 * 
	 * @param list
	 */
	@SuppressWarnings("rawtypes")
	public void setRepeatMethodsParamTypes(ArrayList list) {
		repeatMethodsParamTypes = list;

	}

	/**
	 * Return the override method param types
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ArrayList getRepeatMethodsParamTypes() {
		return repeatMethodsParamTypes;

	}

	/**
	 * Set the override methods param types
	 * 
	 * @param paramTypes
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setRepeatMethodsParamTypes(Class[] paramTypes) {
		if (this.repeatMethodsParamTypes == null)
			this.repeatMethodsParamTypes = new ArrayList();
		repeatMethodsParamTypes.add(paramTypes);
	}

}
