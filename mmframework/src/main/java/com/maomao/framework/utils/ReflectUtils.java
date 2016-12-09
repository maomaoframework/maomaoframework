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

import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 
 * </p>
 * 
 * @author
 * @version 1.0
 */
public final class ReflectUtils {
	static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);
	
	
	/**
	 * 调用对象的某个方法
	 * @param obj
	 * @param methodName
	 * @param clazz
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static Object invok(Object obj, String methodName, Class<?>[] clazz, Object[] params) throws Exception {
		Method method = obj.getClass().getMethod(methodName, clazz);
		return method.invoke(obj, params);
	}
	
	/**
	 * 执行get方法
	 * @param bean
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static Object doGetMethod(Object bean, String name) throws Exception {
		return PropertyUtils.getProperty(bean, name);
	}

//	/**
//	 * 调用无参数方法，并返回值
//	 * 
//	 * @param methodName
//	 * @return
//	 */
//	public static Object invok(Object obj, String methodName) throws Exception {
//		Method method = obj.getClass().getMethod(methodName);
//		return method.invoke(obj);
//	}
//
//	/**
//	 * 带参数调用
//	 * 
//	 * @param obj
//	 * @param methodName
//	 * @param clazz
//	 * @param params
//	 * @return
//	 * @throws Exception
//	 */
//	public static Object invok(Object obj, String methodName, Class<?>[] clazz, Object[] params) throws Exception {
//		Method method = obj.getClass().getMethod(methodName, clazz);
//		return method.invoke(obj, params);
//	}
//
//
//	public static Object getInstance(Class clazz) {
//		try {
//			Object o = clazz.newInstance();
//			return o;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * 
//	 * 
//	 * @param sClassName
//	 * 
//	 * @return Object
//	 */
//	static public Object newInstance(String sClassName) {
//		try {
//			Object objClass = Class.forName(sClassName).newInstance();
//
//			return objClass;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * 
//	 * 
//	 * @param sClassName
//	 * 
//	 * @return Object
//	 */
//	static public Object newInstance(Class clazz) {
//		try {
//			Object objClass = clazz.newInstance();
//
//			return objClass;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	/**
//	 * 
//	 * 
//	 * @param sClassName
//	 * @param aclassParam
//	 * @param aobjParam
//	 * 
//	 * @return Object
//	 */
//	static public Object newInstance(String sClassName, Class[] aclassParam, Object[] aobjParam) {
//		try {
//			Constructor objConstructor = Class.forName(sClassName).getConstructor(aclassParam);
//
//			Object objClass = objConstructor.newInstance(aobjParam);
//
//			return objClass;
//		} catch (Exception e) {
//			return null;
//		}
//	}
//
//	public static Class loadClass(String className) throws ClassNotFoundException {
//		Class cls = null;
//		try {
//			cls = Thread.currentThread().getContextClassLoader().loadClass(className.trim());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		if (cls == null) {
//			cls = Class.forName(className.trim());
//		}
//
//		return cls;
//	}

}
