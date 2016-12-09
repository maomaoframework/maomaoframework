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
package com.maomao.server;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * ClassLoader
 * 
 * @author maomao
 * 
 */
public class AppClassLoader extends URLClassLoader {
	public AppClassLoader(URL[] urls, ClassLoader parent, String pk) {
		this(urls, parent);
	}

	public AppClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public AppClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public void addURL(URL arg0) {
		super.addURL(arg0);
	}
}
