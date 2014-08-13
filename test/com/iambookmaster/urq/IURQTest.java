package com.iambookmaster.urq;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import junit.framework.TestCase;

import com.iambookmaster.client.iurq.Core;
import com.iambookmaster.client.iurq.URQParser;
import com.iambookmaster.client.iurq.URQUI;
import com.iambookmaster.client.iurq.logic.Btn;
import com.iambookmaster.client.iurq.logic.Pause;
import com.iambookmaster.client.iurq.logic.Play;
import com.iambookmaster.client.iurq.logic.URQImage;

public class IURQTest extends TestCase {

	private static final String INVENTORY = "Инвентарь";
	
	public void testAll() throws Exception {
		InputStream stream = getClass().getResourceAsStream("IURQTest.properties");
		assertNotNull(stream);
		Reader reader = new InputStreamReader(stream,"UTF-8");
		Properties properties = new Properties();
		properties.load(reader);
		stream.close();
		for (Object obj : properties.keySet()) {
			String key = (String)obj;
			if (key.endsWith(".code")) {
				//check only for .code
				String out = key.substring(0,key.length()-5).concat(".out");
				String code = properties.getProperty(key);
				assertNotNull(code);
				String output = properties.getProperty(out);
				assertNotNull(output);
				System.out.println();
				System.out.println(key);
				System.out.println(code);
				validate(code,output);
			}
		}
	}
	
	private void validate(String code, String output) {
		Core core = new Core(INVENTORY);
		URQUITest test = new URQUITest();
		core.init(test);
		URQParser parser = new URQParser();
		parser.startParse(code, core);
		core.tick();
		assertEquals(output, test.builder.toString().trim());
		
	}

	@Override
	protected void setUp() throws Exception {
	}
	
	public class URQUITest implements URQUI {
		private StringBuilder builder = new StringBuilder();
		public void showImage(URQImage image) {
			builder.append(",img ");
			builder.append(image.getLocation());
		}
		public void save(String location) {
			builder.append(",save ");
			builder.append(location);
		}
		public void resizeItems() {
		}
		public void print(String s, int i) {
			builder.append(",\"");
			builder.append(s);
			builder.append('"');
		}
		public void play(Play operator) {
			builder.append(",play ");
			builder.append(operator.getLocation());
		}
		public void pause(Pause pause) {
			builder.append(",pause ");
			builder.append(pause.getTime());
		}
		public String loadFile(String s) {
			return null;
		}
		public void invRefresh() {
		}
		public String getInput() {
			return null;
		}
		public void end() {
			builder.append(",end ");
		}
		public void enableInput() {
		}
		public void doExit() {
			builder.append(",exit ");
		}
		public void disableInput() {
		}
		public void clear() {
		}
		public void anykey() {
		}
		public void addButton(Btn btn) {
		}
	}
	
}
