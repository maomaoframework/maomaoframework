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
package com.maomao.framework.configuration;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.PropertyPlaceholderHelper;

/**
 * Store configurations, which defined in user customer properties.
 * @author maomao
 */
public class SysConfiguration extends PropertyPlaceholderConfigurer {
	private static Map<String, String> properties = new HashMap<String, String>();

	private String[] configurations;
	private static final String PREFIX = "${";
	private static final String SUFFIX = "}";

	public void setConfigurations(String[] values) throws MalformedURLException {
		this.configurations = values;
		if (values == null)
			return;
		Resource[] resources = new Resource[values.length];
		for (int i = 0; i < values.length; i++) {
			String config = convert2UrlFormat(values[i]);
			Resource resource = getResource(config);
			resources[i] = resource;
		}
		super.setLocations(resources);
	}

	private Resource getResource(String config) throws MalformedURLException {
		Resource r = null;
		int cpIndex = config.indexOf("classpath:");
		if (cpIndex != -1) {
			r = new ClassPathResource(config.substring(cpIndex + "classpath:".length()));
		} else {
			r = new UrlResource(config);
		}
		return r;

	}

	public String[] getConfigurations() {
		return configurations;
	}

	private String convert2UrlFormat(String originalPlaceholderToUse) {
		if (originalPlaceholderToUse == null)
			return null;
		StringBuffer buf = new StringBuffer(originalPlaceholderToUse);
		int startIndex = buf.indexOf(PREFIX);
		while (startIndex != -1) {
			int endIndex = buf.indexOf(SUFFIX, startIndex + PREFIX.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + PREFIX.length(), endIndex);
				String propVal = resolvePlaceholder(placeholder);
				if (propVal != null) {
					buf.replace(startIndex, endIndex + SUFFIX.length(), propVal);
					startIndex = buf.toString().indexOf(PREFIX, startIndex + propVal.length());
				} else {
					startIndex = buf.toString().indexOf(PREFIX, endIndex + SUFFIX.length());
				}
			} else {
				startIndex = -1;
			}
		}
		return buf.toString();
	}

	private String resolvePlaceholder(String placeholder) {
		String value = System.getProperty(placeholder);
		if (value == null) {
			value = System.getenv(placeholder);
		}
		return value;
	}

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		if (properties == null || properties.size() == 0) {
			PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, DEFAULT_VALUE_SEPARATOR,
					false);
			for (Entry<Object, Object> entry : props.entrySet()) {
				String stringKey = String.valueOf(entry.getKey());
				String stringValue = String.valueOf(entry.getValue());
				stringValue = helper.replacePlaceholders(stringValue, props);
				properties.put(stringKey, stringValue);
			}
		}
		super.processProperties(beanFactoryToProcess, props);
	}

	public static Map<String, String> getProperties() {
		return properties;
	}

	public static String getProperty(String key) {
		return properties.get(key);
	}
}