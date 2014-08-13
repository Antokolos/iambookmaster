package com.iambookmaster.qsp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.BookDecorator;
import com.iambookmaster.server.logic.MOBIBookDecrator;



public class MOBIExportTest extends QSPExportTest {

	public void test1() throws Exception { 
		
//		perform("attack2attack");
//		perform("attack2defence");
//		perform("attack2attackWeak");
//		perform("attack2attackDeath");
//		perform("attack2defenceFatal");
//		perform("attack2defenceFatalMax");
//		perform("1round");
//		perform("3npc");
		
//		perform("Sprites");
		perform("Dragon");
//		perform("IvanDurak");
		
	}


	@Override
	protected BookDecorator getBookDecorator(Model model) {
		return new MOBIBookDecrator(model,appConstants,appMessages);
	}

	@Override
	protected FileOutputStream getOutputStream() throws IOException {
		File file = new File("ebooks/output.zip");
		if (file.exists()==false) {
			file.createNewFile();
		}
		return new FileOutputStream(file);
	}
	
	
	
}
