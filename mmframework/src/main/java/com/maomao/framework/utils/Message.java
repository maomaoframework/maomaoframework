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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.maomao.framework.support.paglit.PageInfo;

/**
 * 反馈消息类 JSON方式
 * 
 * @author maomao
 * 
 */
public class Message {
	boolean success = false;
	Integer rtnCode = 1;
	String message;

	JSONObject messageObj;

	public Message() {
		this.messageObj = new JSONObject();
	}

	// public Message setMessage(String message) {
	// messageObj.put("message", message);
	// return this;
	// }

	public Message setData(Collection<Object> data) {
		JSONObject joData = messageObj.getJSONObject("RtnMsg");
		if (joData == null) {
			joData = new JSONObject();
			messageObj.put("RtnMsg", joData);
		}
		joData.put("rows", data);
		return this;
	}

	public Message setData(Object data) {
		messageObj.put("RtnMsg", data);
		return this;
	}

	public boolean isSuccess() {
		return success;
	}

	public Message setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public static String okMessage() {
		Message m = new Message();
		m.setSuccess(true);
		return m.toString();
	}

	public static String okMessage(Object obj) {
		Message m = new Message();
		m.setSuccess(true);

		if (obj instanceof Collection) {
			m.setData((Collection<?>) obj);
		} else {
			m.setData(obj);
		}
		return m.toString();
	}

	public static String okMessage(String[] keys, Object[] objs) {
		Message m = new Message();
		m.setSuccess(true);

		JSONObject jo = new JSONObject();
		for (int i = 0; i < keys.length; i++) {
			jo.put(keys[i], objs[i]);
		}
		m.setData(jo);
		return m.toString();
	}

	public static String okMessage(Object obj, PageInfo pageInfo) {
		Message m = new Message();
		m.setSuccess(true);

		if (obj instanceof Collection) {
			m.setData((Collection<?>) obj);
		} else {
			m.setData(obj);
		}

		JSONObject data = m.messageObj.getJSONObject("RtnMsg");
		if (data == null) {
			data = new JSONObject();
			m.messageObj.put("RtnMsg", data);
		}
		data.put("page", pageInfo);
		return m.toString();
	}

	public static String errorMessage(String message) {
		Message m = new Message();
		m.setSuccess(false);
		m.setRtnCode(-1);
		m.message = message;
		return m.toString();
	}

	public static String errorMessage(String[] keys, Object[] objs) {
		Message m = new Message();
		m.setSuccess(false);

		JSONObject jo = new JSONObject();
		for (int i = 0; i < keys.length; i++) {
			jo.put(keys[i], objs[i]);
		}
		m.setData(jo);
		return m.toString();
	}

	public static String error() {
		Message m = new Message();
		m.setSuccess(false);
		return m.toString();
	}

	@Override
	public String toString() {
		JSONObject jo = null;
		if (messageObj == null) {
			return JSON.toJSONString(this);
		} else {
			jo = this.messageObj;
			jo.put("success", this.success);
			jo.put("rtnCode", this.rtnCode);
			jo.put("message", this.message);
			return JSON.toJSONString(jo);
		}
	}

	public Integer getRtnCode() {
		return rtnCode;
	}

	public void setRtnCode(Integer rtnCode) {
		this.rtnCode = rtnCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
