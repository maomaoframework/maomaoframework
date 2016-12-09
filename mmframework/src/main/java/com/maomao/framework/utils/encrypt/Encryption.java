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
package com.maomao.framework.utils.encrypt;

import java.security.MessageDigest;

import com.maomao.framework.utils.ByteUtils;

/**
 * 加密工具类
 * 
 * @author maomao
 * 
 */
public class Encryption {
	public static void main(String[] args) throws Exception {
		System.out.println(Encryption.encodeMD5("admin"));
	}

	/**
	 * MD5
	 * 
	 * @param sString
	 *            String
	 * 
	 * @throws Exception
	 * 
	 * 
	 * @return String
	 */
	public static String encodeMD5(String sString) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");

			md5.update(sString.getBytes());
			byte[] abResult = md5.digest();

			return ByteUtils.bytes2Hex(abResult);
		} catch (Exception e) {
			return sString;
		}
	}

	/**
	 * DES
	 * 
	 * @param sString
	 *            String
	 * 
	 * @throws Exception
	 * 
	 * 
	 * @return String
	 */
	public static String encode_DES(String astr, String rawkey) throws Exception {
		return DES.encrypt(astr, rawkey);
	}

	public static String decode_DES(String astr, String rawkey) throws Exception {
		return DES.decrypt(astr, rawkey);
	}
}
