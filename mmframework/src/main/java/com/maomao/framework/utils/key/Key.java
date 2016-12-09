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
package com.maomao.framework.utils.key;

import java.util.Properties;

/**
 * 主键生成工具类
 * 
 * @author maomao
 * 
 */
public final class Key {
	public static final String key() {
		Properties props = new Properties();
		props.setProperty("separator", "");
		HexUUIDGenerator gen = new HexUUIDGenerator();
		gen.configure(props);

		return (String) gen.generate();
	}

	/**
	 * 生成6位数字验证码
	 * 
	 * @return
	 */
	public static final String generate6Number() {
		String s = "";
		while (s.length() < 6)
			s += (int) (Math.random() * 10);
		return s;
	}

	/**
	 * 随机码生成工具
	 * 
	 * @return
	 */
	public static String generate8bitCode() {
		String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
		int MaxPos = chars.length();
		String code = "";
		for (int i = 0; i < 8; i++) {
			code += chars.charAt((int) Math.floor(Math.random() * MaxPos));
		}
		return code;

	}
}