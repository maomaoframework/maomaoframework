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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.PatternMatcherInput;

/**
 * 字符串工具类
 * 
 * @author maomao
 * 
 */
public class StringUtils {
	/**
	 * 是否非空字符串
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isNotEmptyString(final String input) {
		return input != null && !"".equals(input.trim());
	}

	/**
	 * 分空检查判定
	 * 
	 * @param sString
	 * @return
	 */
	public static final boolean isEmpty(final String sString) {
		if (sString == null || sString.length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 非空判定
	 * 
	 * @param sString
	 * @return
	 */
	public static final boolean isEmptyAfterTrim(final String sString) {
		if (sString == null || sString.length() == 0)
			return true;

		if (isEmpty(sString.trim()))
			return true;

		return false;
	}

	/**
	 * 以spattern为模式进行分割字符串操作
	 * 
	 * @param sInput
	 * @param sPattern
	 * @return
	 */
	public static List<String> split(final String sInput, String sPattern) {
		if (isEmptyAfterTrim(sInput))
			return null;

		List<String> arrayResult = new ArrayList<String>();

		Perl5Util util = new Perl5Util();
		sPattern = sPattern.replace('#', ' ');
		sPattern = "#" + sPattern + "#";
		util.split(arrayResult, sPattern, sInput);

		if (arrayResult.size() == 0)
			arrayResult.add(sInput);

		return arrayResult;
	}

	/**
	 * 以模式分割成数组
	 * 
	 * @param sInput
	 * @param sPattern
	 * @return
	 */
	public static String[] split2Array(final String sInput, String sPattern) {
		if (isEmptyAfterTrim(sInput))
			return null;
		return sInput.split(sPattern);
	}

	/**
	 * 以模式分割，并取其中的某个元素
	 * 
	 * @param sInput
	 * @param sPattern
	 * @param iIdx
	 * @return
	 */
	public static final String getElement(final String sInput, final String sPattern, final int iIdx) {
		List<String> listElement = split(sInput, sPattern);

		if (listElement != null) {
			if (iIdx < listElement.size())
				return (String) listElement.get(iIdx);
		}

		return null;
	}

	/**
	 * 字符串裁剪
	 * 
	 * @param sString
	 * @return
	 */
	public static String trim(final String sString) {
		String str = sString;
		if (isEmpty(str))
			return str;

		Perl5Util utilPerl = new Perl5Util();
		str = utilPerl.substitute("s#^\\s*##g", str);
		str = utilPerl.substitute("s#\\s*$##g", str);

		return str;
	}

	/**
	 * 字符串替换
	 * 
	 * @param sString
	 * @param sSrc
	 * @param sDest
	 * @return
	 */
	public static String replace(final String sString, final String sSrc, final String sDest) {
		return replace(sString, sSrc, sDest, true);
	}

	/**
	 * 字符串替换
	 * 
	 * @param sString
	 * @param sSrc
	 * @param sDest
	 * @param bCaseSensitive
	 * @return
	 */
	public static final String replace(String sString, String sSrc, String sDest, final boolean bCaseSensitive) {
		if (isEmpty(sString))
			return sString;

		if (!bCaseSensitive) {
			sSrc = sSrc.toLowerCase();
			sDest = sDest.toLowerCase();
			sString = sString.toLowerCase();
		}

		Perl5Util utilPerl = new Perl5Util();
		sSrc = sSrc.replace('#', ' ');
		sDest = sDest.replace('#', ' ');
		sString = utilPerl.substitute("s#" + sSrc + "#" + sDest + "#g", sString);

		return sString;
	}

	/**
	 * 
	 * <p>
	 * 
	 * <pre>
	 * 
	 * String sString = &quot;par1=value1;par2=value2;par3=value3;&quot;;
	 * 
	 * String sValue = StringUtils.parseParValue(sString, &quot;par2&quot;, &quot;;&quot;);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param sString
	 *            String
	 * @param sParName
	 *            String
	 * @param sSeperator
	 *            String
	 * 
	 * @return String
	 */
	public static final String parseParValue(final String sString, final String sParName, final String sSeperator) {
		if (isEmptyAfterTrim(sString))
			return null;

		if (isEmptyAfterTrim(sParName))
			return null;

		String sPattern = "/" + sParName + "=([\\w.]+)(" + sSeperator + "|$)/";

		Perl5Util utilPerl = new Perl5Util();

		if (utilPerl.match(sPattern, sString))
			return utilPerl.group(1);
		else
			return null;
	}

	/**
	 * 
	 * <p>
	 * 
	 * <pre>
	 * String sString = &quot;par1=value1;par2=value20;par2=value21;par3=value3;&quot;;
	 * 
	 * List listValue = StringUtils.parseParValues(sString, &quot;par2&quot;, &quot;;&quot;);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param sString
	 *            String
	 * @param sParName
	 *            String
	 * @param sSeperator
	 *            String
	 * 
	 * @return String
	 */
	public static List parseParValues(final String sString, final String sParName, final String sSeperator) {
		if (isEmptyAfterTrim(sString))
			return null;

		if (isEmptyAfterTrim(sParName))
			return null;

		String sPattern = "/" + sParName + "=([\\w.]+)(" + sSeperator + "|$)/";

		Perl5Util utilPerl = new Perl5Util();
		PatternMatcherInput inputSrc = new PatternMatcherInput(sString);

		List listValue = new ArrayList();
		while (utilPerl.match(sPattern, inputSrc)) {
			listValue.add(utilPerl.group(1));
		}

		if (listValue.size() > 0)
			return listValue;
		else
			return null;
	}

	/**
	 * 
	 * <p>
	 * 
	 * <pre>
	 * String sString = &quot;par1=value1;par2=value2;par3=value3;&quot;;
	 * 
	 * Hashtable hashValue = StringUtils.parseParValues(sString, &quot;;&quot;);
	 * </pre>
	 * 
	 * </p>
	 * 
	 * @param sString
	 *            String
	 * @param sSeperator
	 *            String
	 * 
	 * @return Hashtable
	 */
	public static Hashtable parseParValues(final String sString, final String sSeperator) {
		if (isEmptyAfterTrim(sString))
			return null;

		Hashtable hashParValue = new Hashtable();

		// pari=value
		List listParValue = split(sString, sSeperator);

		if (listParValue != null) {
			// pari, value
			List listPar;
			String sValue;
			for (int i = 0; i < listParValue.size(); i++) {
				listPar = split((String) listParValue.get(i), "=");

				if (listPar != null && listPar.size() == 2) {
					sValue = (String) listPar.get(1);
					if (!isEmptyAfterTrim(sValue))
						hashParValue.put(listPar.get(0), listPar.get(1));
				}
			}
		}

		return hashParValue;
	}

	/**
	 * 
	 * 
	 * @param sString
	 *            String
	 * 
	 * @return String
	 */
	public static final String upperCaseTheFirstChar(final String sString) {
		if (isEmpty(sString))
			return null;

		if (sString.length() > 1)
			return sString.toUpperCase().charAt(0) + sString.substring(1);
		else
			return sString.toUpperCase();
	}

	public static final String lowerCaseTheFirstChar(final String str) {
		if (isEmpty(str))
			return null;

		if (str.length() > 1)
			return str.toLowerCase().charAt(0) + str.substring(1);
		else
			return str.toLowerCase();
	}

	/**
	 * <code>true</code>
	 * <p>
	 * <code>true</code>
	 * </p>
	 * <ul>
	 * <li>true
	 * <li>yes
	 * <li>1
	 * </ul>
	 * 
	 * @param sValue
	 *            String
	 * 
	 * @return boolean
	 */
	static public boolean equalsTrue(String sValue) {
		if (sValue != null) {
			sValue = sValue.toLowerCase();

			if (sValue.equals("true") || sValue.equals("true") || sValue.equals("yes"))
				return true;
		}

		return false;
	}

	/**
	 * 
	 * 
	 * @param sValue
	 *            String
	 * 
	 * @return String
	 */
	static public final String quote(final String sValue) {
		if (sValue == null)
			return sValue;
		else
			return "'" + sValue + "'";
	}

	/**
	 * 
	 * 
	 * @param source
	 * 
	 * @param length
	 * 
	 * @return
	 */
	public static String leftFillZero(String source, int length) {
		source = (null == source) ? "" : source.trim();
		int len = source.length();
		for (int i = 0; i < length - len; i++) {
			source = "0" + source;
		}
		return source;
	}

	/**
	 * 
	 * @param s
	 * @param params
	 * @return
	 */
	static public final String replace(String s, List<?> params) {
		if (params == null)
			return s;
		return replace(s, params.toArray());
	}

	/**
	 * 
	 * @param s
	 * @param params
	 * @return
	 */
	static public final String replace(String s, Object[] args) {
		char[] c = s.toCharArray();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++) {
			if (c[i] != '$') {
				sb.append(c[i]);
			} else {
				String sParamIdx = "";
				int idx = 0;
				int j = i + 1;
				for (; j < c.length; j++) {
					if (c[j] == ' ')
						break;
					sParamIdx += c[j];
				}
				j--;

				try {
					idx = Integer.parseInt(sParamIdx);

					if (idx > 0) {
						sb.append(args[idx - 1].toString());
						i = j;
					} else
						sb.append(c[i]);
				} catch (Exception e) {
					sb.append(c[i]);
					continue;
				}
			}
		}
		return sb.toString();
	}

	public static final String substringBetween(String str, String pos1, String pos2) {
		int p1 = str.indexOf(pos1);
		String sub = str.substring(p1 + pos1.length());

		int p2 = sub.indexOf(pos2);
		sub = sub.substring(0, p2);
		return sub;
	}

	public static final String defaultIfEmpty(String str, String defaultStr) {
		return StringUtils.isEmptyAfterTrim(str) ? defaultStr : str;
	}

	/**
	 * 替换所有全角半角英文字符
	 * 
	 * @param str
	 * @param replacement
	 * @return
	 */
	public static String replaceSymbol(String str, String replacement) {
		String s = str.replaceAll("[,|;\\s*]", replacement);
		s = s.replaceAll("[\uFE30-\uFFA0|、]", replacement);
		return s;
	}

	/**
	 * 格式化字符串
	 * 
	 * @param str
	 * @param params
	 * @return
	 */
	public static String format(String str, Map<String, Object> params) {
		if (StringUtils.isEmpty(str))
			return str;

		String p = str;
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			if (str.contains("{{" + entry.getKey() + "}}")) {
				if (entry.getValue() instanceof String)
					p = p.replaceAll("\\{\\{" + entry.getKey() + "\\}\\}", (String) entry.getValue());
			}
		}
		return p;
	}

	public static String array2String(String[] p, String spliter) {
		if (p == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < p.length; i++) {
			if (i == 0)
				sb.append(p[i]);
			else
				sb.append(spliter).append(p[i]);
		}
		return sb.toString();
	}

	/**
	 * 检验手机号码
	 * 
	 * @param phone
	 * @return
	 */
	public static boolean isValidMobile(String phone) {
		Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(14[0-9])|(15[0-9])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(phone);
		return m.matches();
	}

	/**
	 * 取得hash
	 * 
	 * @param str
	 * @param max
	 * @return
	 */
	public static int hash(String str, int max) {
		int hash = str.hashCode();
		return hash % max;
	}
}
