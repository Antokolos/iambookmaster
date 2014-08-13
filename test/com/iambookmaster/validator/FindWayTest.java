package com.iambookmaster.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.exceptions.JSONException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.PathFinder;
import com.iambookmaster.client.paragraph.PathFinderErrorListener;
import com.iambookmaster.server.LocalMessages;
import com.iambookmaster.server.XMLModelParser;

public class FindWayTest extends TestCase {

	private static final String[] PATTERN = new String[0];
	private AppConstants appConstants;
	private AppMessages appMessages;
	private static String locale="ru";
	
	public void test1() throws Exception {
		perform("IvanDurak");
	}
	public void test2() throws Exception {
		perform("Dragon");
	}
	
	@Override
	protected void setUp() throws Exception {
		LocalMessages messages = new LocalMessages();
		messages.initialize(locale);
		appConstants = LocalMessages.getInstance(AppConstants.class, locale);
		appMessages = LocalMessages.getInstance(AppMessages.class, locale);
	}
	
	public void perform(String name) throws Exception {
		perform(name,false);
	}
	public void perform(String name,boolean conditionalChainAllowed) throws Exception {
		Model model = createModelFromJSON(name+".xml");
		ArrayList<String> res = new ArrayList<String>();
		InputStream stream = getClass().getResourceAsStream(name+".txt");
		String[] results;
		if (stream==null) {
			results = new String[0];
		} else {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = br.readLine()) != null) {
				res.add(line);
			}
			results = res.toArray(PATTERN);
		}
		PathFinderErrorListener listener = createListener(results,conditionalChainAllowed);
		PathFinder finder = new PathFinder(model);
		finder.validate(listener);
	}
	
	private PathFinderErrorListener createListener(String[] results,boolean conditionalChainAllowed) {
		return (PathFinderErrorListener)java.lang.reflect.Proxy.newProxyInstance(
				PathFinderErrorListener.class.getClassLoader(),
				new Class[]{PathFinderErrorListener.class},
			    new ListenerProxy(results,conditionalChainAllowed)); 
	}
	
	public class ListenerProxy implements java.lang.reflect.InvocationHandler{

		private final String[] results;
		private int counter;
		private long timeout;
		private boolean conditionalChainAllowed;
		
		public ListenerProxy(String[] results, boolean conditionalChainAllowed) {
			this.results = results;
			this.conditionalChainAllowed = conditionalChainAllowed;
			//3 min
			timeout = System.currentTimeMillis()+1000*60*2;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("updateStatus".equals(method.getName())) {
				return null;
			} else if ("checkTimeout".equals(method.getName())) {
				return false;
			} else if ("canContinue".equals(method.getName())) {
				return timeout>System.currentTimeMillis();
			} else if ("conditionalChain".equals(method.getName())) {
				if (conditionalChainAllowed) {
					return null;
				}
			} else if ("canBePassed".equals(method.getName())) {
				return true;
			}
			StringBuilder builder = new StringBuilder(method.getName());
			for (Object object : args) {
				builder.append(',');
				if (object instanceof Paragraph) {
					Paragraph paragraph = (Paragraph) object;
					builder.append("p:");
					builder.append(paragraph.getName());
				} else if (object instanceof ParagraphConnection) {
					ParagraphConnection connection = (ParagraphConnection) object;
					builder.append("c:");
					builder.append(connection.getFrom().getName());
					builder.append("->");
					builder.append(connection.getTo().getName());
				} else if (object instanceof ObjectBean) {
					ObjectBean bean = (ObjectBean) object;
					builder.append("o:");
					builder.append(bean.getName());
				} else if (object instanceof AbstractParameter) {
					AbstractParameter parameter = (AbstractParameter) object;
					builder.append("a:");
					builder.append(parameter.getName());
				} else {
					builder.append(String.valueOf(object));
				}
			}
			String out = builder.toString();
			assertTrue("More messages than expected:"+out,results.length>counter);
			assertEquals(results[counter++],out);
			return null;
		}
	}

	private Model createModelFromJSON(String file) throws JSONException, ParserConfigurationException, SAXException, IOException {
		InputStream stream = getClass().getResourceAsStream(file);
		assertNotNull("Cannot load file "+file,stream);
		Model model;
		try {
			model = new Model(appConstants,appMessages);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setNamespaceAware(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource inputSource = new InputSource(stream);
			Document result = db.parse(inputSource);
			JSONParser parser = new XMLModelParser();
			model.restore(result,parser);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return model;
	}
	
}
