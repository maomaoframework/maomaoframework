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

import org.hibernate.EmptyInterceptor;

import com.maomao.framework.utils.encrypt.Encryption;

public class Test extends EmptyInterceptor{
	private static final long serialVersionUID = 4103862692025402062L;

	@Override
	public String onPrepareStatement(String sql) {
		//System.out.println(sql); //TODO 1
		return super.onPrepareStatement(sql);
	}
	
	public static void main(String [] args) {
		System.out.println(Encryption.encodeMD5("12345678"));
	}
}
