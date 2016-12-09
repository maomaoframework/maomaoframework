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
package com.maomao.framework.datasource;

import java.util.HashMap;
import java.util.Map;

public class ThreadVariable {
	
	private static ThreadLocal<String> dataSourceName = new ThreadLocal<String>();
	
	private static ThreadLocal<String> firstMethod=new ThreadLocal<String>();
	
	private static ThreadLocal<Context> lastTopContext = new ThreadLocal<Context>();
	
	private static ThreadLocal<Map<String,Context>> threadMap=new ThreadLocal<Map<String,Context>>();
	
	public static Context getLastTopContext(){
		return lastTopContext.get();
	}
	public static void setLastTopContext(Context topContext){
		lastTopContext.set(topContext);
	}
	public static void removeLastTopContext(){
		lastTopContext.remove();
	}
	
	public static String getDataSourceName() {
		return dataSourceName.get();
	}
	
	public static void setDataSourceName(String dsName) {
		dataSourceName.set(dsName);
	}
	
	public static void removeDataSourceName() {
		dataSourceName.remove();
	}
	
	public static String getFirstMethod() {
		return firstMethod.get();
	}
	
	public static void setFirstMethod(String str) {
		firstMethod.set(str);
	}
	
	public static void removeFirstMethod(){
		firstMethod.remove();
	}
	
	public static Map<String,Context> getThreadMap() {
		return threadMap.get();
	}
	
	public static void setThreadMap(String key,Context value) {
		if(null!=getThreadMap()){
			getThreadMap().put(key, value);
		}else{
			Map<String,Context> map=new HashMap<String,Context>();
			map.put(key, value);
			threadMap.set(map);
		}
	}
	public static void removeThreadMap(){
		threadMap.remove();
	}
	
	
	
}