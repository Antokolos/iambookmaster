package com.iambookmaster.server;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.common.XMLBuilder;

public class XMLModelParser extends JSONParser {

	@Override
	public Object getRow(Object o, int r) {
		return ((Node) o).getChildNodes().item(r);
	}

	@Override
	public int length(Object o) {
		return ((Node)o).getChildNodes().getLength();
	}

	@Override
	public Object property(Object o, String f) {
		Node node = getChildNodeByName(o,f,false);
		if (node==null) {
			throw new IllegalArgumentException(f);
		}
		return node; 
	}

	private Node getChildNodeByName(Object o, String f,boolean direct) {
		Node item = ((Node) o).getFirstChild();
		if (item==null) {
			return null;
		}
		if (XMLBuilder.FIELD_OBJECT.equals(item.getNodeName())) {
			return directChildNodeByName(item,f);
		} else if (direct){
			return directChildNodeByName(o,f);
		} else {
			//wrong XML
			throw new IllegalArgumentException("Wrong XML struture");
		}
	}
	private Node directChildNodeByName(Object o, String f) {
		NodeList list = ((Node)o).getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			if (f.equals(node.getNodeName())) {
				return node;
			}
		}
		return null;
	}

	@Override
	public boolean propertyBoolean(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		return Boolean.parseBoolean(node.getTextContent());
	}

	@Override
	public int propertyInt(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		return Integer.parseInt(node.getTextContent());
	}

	@Override
	public Object propertyNoCheck(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		return node;
	}

	@Override
	public boolean propertyNoCheckBoolean(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		if (node==null) {
			return false;
		} else {
			return Boolean.parseBoolean(node.getTextContent());
		}
	}

	@Override
	public int propertyNoCheckInt(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		if (node==null) {
			return 0;
		} else {
			return Integer.parseInt(node.getTextContent());
		}
	}

	@Override
	public String propertyNoCheckString(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		if (node==null) {
			return null;
		} else {
			return node.getTextContent();
		}
	}

	@Override
	public String propertyString(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		return node.getTextContent();
	}

	@Override
	public Object propertyDirect(Object o, String f) {
		Node node = getChildNodeByName(o,f,true);
		if (node==null) {
			throw new IllegalArgumentException(f);
		}
		return node; 
	}

	@Override
	public Object propertyDirectNoCheck(Object o, String f) {
		return getChildNodeByName(o,f,true);
	}
	
}
