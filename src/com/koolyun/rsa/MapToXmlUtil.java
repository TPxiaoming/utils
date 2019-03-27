package com.koolyun.rsa;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import com.sun.xml.internal.ws.util.StringUtils;
/**
 * map转xml工具类
 * @author xiaoming
 *
 */
public class MapToXmlUtil {

	public static void main(String[] args) {
		Map<String, String> map = new HashMap<>();
		map.put("NAME", "123456");
		map.put("AGE", "19");
		String parentName = "PEOPLE";
		String string  = null;
		try {
			string = mapToXml(map, parentName);
			System.out.println(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String nameStr = getTagNameAndValue(string, "NAME");
		System.out.println(nameStr);
	}

	/**
	 * 获取指定标签的标签名和值
	 * @param strXml
	 * @param tagName
	 * @return
	 */
	private static String getTagNameAndValue(String strXml, String tagName) {
		if(null != tagName && !"".equals(tagName)) {
			String startTag = "<" + tagName + ">";
			String endTag = "</" + tagName + ">";
			String result = null != strXml && strXml.indexOf(startTag) != -1
					&& strXml.indexOf(endTag) != -1 ?
							strXml.substring(strXml.indexOf(startTag), strXml.indexOf(endTag)
									+ endTag.length()) :"";
			return result;
		}else {
			return null;
		}
	}

	/**
	 * MAP转xml字符串
	 * @param map
	 * @param parentName
	 * @return
	 * @throws Exception
	 */
	private static String mapToXml(Map<String, String> map, String parentName) throws Exception {
		Document document = newDocument();
		//创建指定类型的元素。 请注意，返回的实例实现了Element接口，因此可以直接在返回的对象上指定属性。 
		Element root = document.createElement(parentName);
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			String value = map.get(key);
			if(value != null && value !="") {
				//返回一个字符串，其值为此字符串，并删除任何前导和尾随空格
				value = value.trim();
				//创建子元素
				Element field = document.createElement(key);
				Text node = document.createTextNode(value);
				field.appendChild(document.createTextNode(value));
				root.appendChild(field);
			}
		}
		document.appendChild(root);
		
		//TransformerFactory实例可用于创建Transformer和Templates对象。 
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		//这个抽象类的一个实例可以将一个源代码树转换成一个结果树。 
		Transformer transformer = transformerFactory.newTransformer();
		
		DOMSource domSource = new DOMSource(document);
		//设置一个对转换有效的输出属性。
		transformer.setOutputProperty("encoding", "UTF-8");
		transformer.setOutputProperty("indent", "yes");
		
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		//将XML Source转换为Result 。 具体的转换行为由TransformerFactory的设置TransformerFactory ，
		//当Transformer被实例化并对Transformer实例进行任何修改时。
		//空Source由作为构成被表示为一个空文件DocumentBuilder.newDocument() 。
		//转换空Source的结果取决于转换行为; 它并不总是一个空的Result 。
		transformer.transform(domSource, result);
		
		String output = writer.getBuffer().toString().replaceAll("\r\n|\r\n", "");
		writer.close();
		return output;
	}

	/**
	 * 使用DocumentBuilder来创建一个Document
	 * @return
	 * @throws ParserConfigurationException
	 */
	private static Document newDocument() throws ParserConfigurationException {
		//获取DOM Document对象的新实例以构建一个DOM树。 
		Document document = newDocumentBuilder().newDocument();
		return document;
	}

	/**
	 * 使用当前配置的参数创建一个新的DocumentBuilder实例。 
	 * @return
	 * @throws ParserConfigurationException
	 */
	private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		//定义工厂API，使应用程序能够从XML文档获取生成DOM对象树的解析器。
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		//设置此工厂创建的DocumentBuilderFactory和DocumentBuilder的功能。
		//false ：实现将根据XML规范处理XML，而不考虑可能的实现限制。
		documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
//		设置XInclude处理的状态。 
//		如果在文档实例中找到XInclude标记，则应按照XML Inclusions (XInclude) Version 1.0中的规定进行处理 。 
		documentBuilderFactory.setXIncludeAware(false);
		documentBuilderFactory.setExpandEntityReferences(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder;
	}
}
