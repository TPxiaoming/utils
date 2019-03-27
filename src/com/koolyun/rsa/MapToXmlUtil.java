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
 * mapתxml������
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
	 * ��ȡָ����ǩ�ı�ǩ����ֵ
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
	 * MAPתxml�ַ���
	 * @param map
	 * @param parentName
	 * @return
	 * @throws Exception
	 */
	private static String mapToXml(Map<String, String> map, String parentName) throws Exception {
		Document document = newDocument();
		//����ָ�����͵�Ԫ�ء� ��ע�⣬���ص�ʵ��ʵ����Element�ӿڣ���˿���ֱ���ڷ��صĶ�����ָ�����ԡ� 
		Element root = document.createElement(parentName);
		Iterator<String> iterator = map.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			String value = map.get(key);
			if(value != null && value !="") {
				//����һ���ַ�������ֵΪ���ַ�������ɾ���κ�ǰ����β��ո�
				value = value.trim();
				//������Ԫ��
				Element field = document.createElement(key);
				Text node = document.createTextNode(value);
				field.appendChild(document.createTextNode(value));
				root.appendChild(field);
			}
		}
		document.appendChild(root);
		
		//TransformerFactoryʵ�������ڴ���Transformer��Templates���� 
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		//����������һ��ʵ�����Խ�һ��Դ������ת����һ��������� 
		Transformer transformer = transformerFactory.newTransformer();
		
		DOMSource domSource = new DOMSource(document);
		//����һ����ת����Ч��������ԡ�
		transformer.setOutputProperty("encoding", "UTF-8");
		transformer.setOutputProperty("indent", "yes");
		
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		
		//��XML Sourceת��ΪResult �� �����ת����Ϊ��TransformerFactory������TransformerFactory ��
		//��Transformer��ʵ��������Transformerʵ�������κ��޸�ʱ��
		//��Source����Ϊ���ɱ���ʾΪһ�����ļ�DocumentBuilder.newDocument() ��
		//ת����Source�Ľ��ȡ����ת����Ϊ; ����������һ���յ�Result ��
		transformer.transform(domSource, result);
		
		String output = writer.getBuffer().toString().replaceAll("\r\n|\r\n", "");
		writer.close();
		return output;
	}

	/**
	 * ʹ��DocumentBuilder������һ��Document
	 * @return
	 * @throws ParserConfigurationException
	 */
	private static Document newDocument() throws ParserConfigurationException {
		//��ȡDOM Document�������ʵ���Թ���һ��DOM���� 
		Document document = newDocumentBuilder().newDocument();
		return document;
	}

	/**
	 * ʹ�õ�ǰ���õĲ�������һ���µ�DocumentBuilderʵ���� 
	 * @return
	 * @throws ParserConfigurationException
	 */
	private static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
		//���幤��API��ʹӦ�ó����ܹ���XML�ĵ���ȡ����DOM�������Ľ�������
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		//���ô˹���������DocumentBuilderFactory��DocumentBuilder�Ĺ��ܡ�
		//false ��ʵ�ֽ�����XML�淶����XML���������ǿ��ܵ�ʵ�����ơ�
		documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
//		����XInclude�����״̬�� 
//		������ĵ�ʵ�����ҵ�XInclude��ǣ���Ӧ����XML Inclusions (XInclude) Version 1.0�еĹ涨���д��� �� 
		documentBuilderFactory.setXIncludeAware(false);
		documentBuilderFactory.setExpandEntityReferences(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		return documentBuilder;
	}
}
