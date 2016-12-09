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
package com.maomao.framework.utils;

import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * json转换工具类
 * 
 * @author maomao
 * 
 */
public final class JsonUtils {
	/**
	 * 将对象转换为JSONObject
	 * 
	 * @param obj
	 * @return
	 */
	public static final JSONObject bean2JSONObject(Object obj) {
		return (JSONObject) JSON.toJSON(obj);
	}

	/**
	 * 将对象转换未JSONArray
	 * 
	 * @param collection
	 * @return
	 */
	public static final JSONArray bean2JSONArray(Collection<?> collection) {
		return (JSONArray) JSON.toJSON(collection);
	}
	
	public static final String bean2String(Object obj) {
		return JSON.toJSONString(obj);
	}

	/**
	 * 字符串转JSONArray
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static final JSONArray String2JSONArray(String jsonStr) {
		return (JSONArray) JSONArray.parse(jsonStr);
	}

	/**
	 * 字符串转换JSONObject
	 * 
	 * @param jsonStr
	 * @return
	 */
	public static final JSONObject String2JSONObject(String jsonStr) {
		return (JSONObject) JSONObject.parse(jsonStr);
	}

	/**
	 * 字符串转Bean
	 * 
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static final <T> T String2Bean(String jsonStr, Class<T> clazz) {
		return JSON.parseObject(jsonStr, clazz);
	}

	/**
	 * JSONObject转Bean
	 * 
	 * @param jsonObject
	 * @param clazz
	 * @return
	 */
	public static final <T> T JSONObject2Bean(JSONObject jsonObject, Class<T> clazz) {
		return JSON.parseObject(jsonObject.toString(), clazz);
	}

	/**
	 * 字符串转换为Collection
	 * 
	 * @param str
	 * @param clazz
	 * @return
	 */
	public static final <T> Collection<T> String2Collection(String jsonStr, Class<T> clazz) {
		Collection<T> t = (Collection<T>) JSON.parseArray(jsonStr, clazz);
		return t;
	}

	/**
	 * 字符串转换为List
	 * 
	 * @param str
	 * @param clazz
	 * @return
	 */
	public static final <T> List<T> String2List(String jsonStr, Class<T> clazz) {
		List<T> t = (List<T>) JSON.parseArray(jsonStr, clazz);
		return t;
	}

	/**
	 * 将JSONArray转换为List对象
	 * 
	 * @param jsonArray
	 * @param clazz
	 * @return
	 */
	public static final <T> List<T> JSONArray2List(JSONArray jsonArray, Class<T> clazz) {
		List<T> t = (List<T>) JSON.parseArray(jsonArray.toString(), clazz);
		return t;
	}

	/**
	 * 将JSONArray转换为List对象
	 * 
	 * @param jsonArray
	 * @param clazz
	 * @return
	 */
	public static final <T> Collection<T> JSONArray2Collection(JSONArray jsonArray, Class<T> clazz) {
		Collection<T> t = (Collection<T>) JSON.parseArray(jsonArray.toString(), clazz);
		return t;
	}

	/**
	 * 通过json来复制对象
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static final <T> T clone(Object object, Class<T> clazz) {
		String json = bean2String(object);
		return String2Bean(json, clazz);
	}

	/**
	 * 通过json来复制对象
	 * 
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static final <T> List<T> cloneList(Object object, Class<T> clazz) {
		String json = JsonUtils.bean2String(object);
		List<T> result = JsonUtils.String2List(json, clazz);
		return result;
	}
}