package com.iambookmaster.qsp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookDecorator;
import com.iambookmaster.server.logic.URQBookDecrator;


public class URQExportTest extends QSPExportTest {

	public void test1() throws Exception { 
		
//		perform("attack2attack");
//		perform("attack2defence");
//		perform("attack2attackWeak");
//		perform("attack2defenceFatal");
//		perform("attack2defenceFatalMax");
//		perform("1round");
//		perform("3npc");
		
//		perform("Sprites");
		perform("Dragon");
		
	}
	
	protected FileOutputStream getOutputStream() throws FileNotFoundException {
		return new FileOutputStream("urq/module.qst");
	}

	protected BookDecorator getBookDecorator(Model model) {
		return new URQBookDecrator(model,appConstants,appMessages);
	}
	
}
