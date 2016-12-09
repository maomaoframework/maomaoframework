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

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

/**
 * XML工具类
 * 
 * @author maomao
 * 
 */
public final class XmlUtils {
	public static final EntityResolver NONE_DTD_RESOLVER = new EntityResolver() {
		public InputSource resolveEntity(String publicId, String systemId) {
			return new InputSource(new StringReader(""));
		}
	};

	private DOMReader domReader;

	private SAXReader saxReader;

	@SuppressWarnings("rawtypes")
	public SAXReader createSAXReader(String file, List errorsList, EntityResolver entityResolver) {
		if (saxReader == null)
			saxReader = new SAXReader();
		saxReader.setEntityResolver(NONE_DTD_RESOLVER);
		saxReader.setErrorHandler(new ErrorLogger(file, errorsList));
		saxReader.setMergeAdjacentText(true);
		saxReader.setValidation(false);
		return saxReader;
	}

	public String Dom2String(Document doc) {

		XMLWriter writer = null;
		try {
			StringWriter sw = new StringWriter();
			writer = new XMLWriter(sw);
			writer.write(doc);
			return sw.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != writer)
				try {
					writer.close();
				} catch (Exception ie) {
				}
		}
		return null;
	}

	public String Dom2String(Document doc, String encoding) {

		XMLWriter writer = null;
		try {
			StringWriter sw = new StringWriter();
			writer = new XMLWriter(sw);
			writer.write(doc);
			String xml = sw.toString();

			String pattern = ">";
			int idx = xml.indexOf(pattern);
			String last = xml.substring(idx + 1);
			xml = "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>" + last;
			return xml;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != writer)
				try {
					writer.close();
				} catch (Exception ie) {
				}
		}
		return null;
	}

	public void saveXml2File(String xml, String fileName) {
		File f = new File(fileName);
		saveXml2File(xml, f);
	}

	public void saveXml2File(String xml, File file) {
		try {
			Document doc = load(xml);
			saveXml2File(doc, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveXml2File(Document doc, File file) {
		XMLWriter writer = null;
		try {
			FileWriter fw = new FileWriter(file);
			writer = new XMLWriter(fw);
			writer.write(doc);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != writer)
				try {
					writer.close();
				} catch (Exception ie) {
				}
		}
	}

	/**
	 * @author lizhi 向xml中写入元素数据
	 * @param doc
	 * @param file
	 * @param encoding
	 */
	public void saveXml2File(Document doc, File file, String encoding) {
		XMLWriter writer = null;
		try {
			String sXMLContent = Dom2String(doc, "GBK");
			FileWriter filer = new FileWriter(file);
			filer.write(sXMLContent);
			filer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != writer)
				try {
					writer.close();
				} catch (Exception ie) {
				}
		}
	}

	/**
	 * Document
	 * 
	 * @param in
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Document load(InputStream in) throws DocumentException {
		return createSAXReader("XML InputStream", new ArrayList(), null).read(new InputSource(in));
	}

	/**
	 * Document
	 * 
	 * @param in
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Document load(String xml) throws DocumentException {
		return createSAXReader("XML InputStream", new ArrayList(), null).read(new StringReader(xml));
	}

	/**
	 * Create a dom4j DOMReader
	 */
	public DOMReader createDOMReader() {
		if (domReader == null)
			domReader = new DOMReader();
		return domReader;
	}

	public static class ErrorLogger implements ErrorHandler {
		@SuppressWarnings("rawtypes")
		private List errors;

		@SuppressWarnings("rawtypes")
		ErrorLogger(String file, List errors) {
			this.errors = errors;
		}

		@SuppressWarnings("unchecked")
		public void error(SAXParseException error) {
			errors.add(error);
		}

		public void fatalError(SAXParseException error) {
			error(error);
		}

		public void warning(SAXParseException warn) {
		}
	}

	public static Element generateDom4jElement(String elementName) {
		return DocumentFactory.getInstance().createElement(elementName);
	}

	@SuppressWarnings("unchecked")
	public static void removeChildren(Element el) {
		for (Iterator<Element> iter = el.elements().iterator(); iter.hasNext();) {
			el.remove(iter.next());
		}
	}

}
