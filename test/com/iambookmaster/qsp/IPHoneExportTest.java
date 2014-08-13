package com.iambookmaster.qsp;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.model.Model;


public class IPHoneExportTest extends QSPExportTest {

	private static final String PREFIX_PARAGRAPH = "text";
	private static final String OUTPUT_PATH = "content/iphone/game";
	
	public void test1() throws Exception { 
		
		IPhoneExportCommand command = new IPhoneExportCommand();
//		perform("attack2attack");
//		perform("attack2defence");
//		perform("attack2attackWeak");
//		perform("attack2attackDeath");
//		perform("attack2defenceFatal");
//		perform("attack2defenceFatalMax");
//		perform("1round");
//		perform("3npc");
		
//		perform("Sprites");
//		perform("Kererleys");
		
//		perform("Dragon");
//		perform("DragonFull");
//		perform("IvanDurak");
		perform(command);
		
	}
/*
package com.iambookmaster.client.iphone;

public class IPhoneModelLoader {

	public static void loadModel(IPhoneModelLoaderListener listener) {
		
	}

}
 */
	public class IPhoneExportCommand {
		public String alterLogo;
		public String alterVersionScu;
		private String model;
		private String scu;
		private String path;
		private String donationId;//In App Purchase Apple ID
//		private String lightId;//Light version ID
//		private String fullId;//Full version ID
	}
	public void perform(IPhoneExportCommand command) throws Exception {
		ModelPersist model = createModelFromJSON(command.model+".xml");
		clearFolder(OUTPUT_PATH);
		StringBuilder builder = new StringBuilder();
		//create module
		builder.append("package com.iambookmaster.client.iphone;\n");
		builder.append("import com.iambookmaster.client.common.JSONParser;\n");
		builder.append("import com.iambookmaster.client.exceptions.JSONException;\n");
		builder.append("import com.iambookmaster.client.model.Model;\n");
		builder.append("public class IPhoneModelLoader extends IPhoneModelLoaderStub{\n");
		builder.append("//");
		builder.append(command.model);
		builder.append("\npublic void loadModel(IPhoneModelLoaderListener listener,Model model) {\n");
		//fill model
		populateModel(builder,model);
		builder.append("}\n");
		populateMethods(builder,model,command.path,OUTPUT_PATH);
		builder.append("}\n");
		//save result
		String module = builder.toString();
		FileOutputStream fileWriter = new FileOutputStream("src/com/iambookmaster/client/iphone/IPhoneModelLoader.java");
		try {
			fileWriter.write(module.getBytes("UTF-8"));
		} finally {
			fileWriter.close();
		}
		builder.setLength(0);
		builder.append("//");
		builder.append(model.getSettings().getBookTitle());
		builder.append("\n//");
		builder.append(model.getSettings().getBookAuthors());
		builder.append("\n//GameId=");
		builder.append(model.getGameId());
		builder.append("\n//GameKey=");
		builder.append(model.getGameKey());
		builder.append("\n#define URL_MY_SCHEME @\"iambm");
		builder.append(command.scu);
		if (command.alterVersionScu == null) {
			appendSuffix(builder,model.getSettings().isDemoVersion());
		}
		builder.append("\";\n//remove : when insert into INFO.PLIST\n#define URL_ALTER_SCHEME @\"iambm");
		if (command.alterVersionScu == null) {
			builder.append(command.scu);
			appendSuffix(builder,model.getSettings().isDemoVersion()==false);
		} else {
			builder.append(command.alterVersionScu);
			builder.append(':');
		}
		builder.append("\";\n");
		builder.append("#define IN_APP_PURACHASE @\"");
		if (command.donationId==null) {
			builder.append("com.iambookmaster.");
			builder.append(command.scu);
			builder.append(".donate");
		} else {
			builder.append(command.donationId);
		}
		builder.append("\"\n");
		
		if (model.getSettings().isDemoVersion()) {
			builder.append("#define DEMO_VERSION\n");
			builder.append("#define PAID_GAME\n");
		} else {
			for (Paragraph paragraph : model.getParagraphs()) {
				if (paragraph.isCommercial() || paragraph.getType()==Paragraph.TYPE_COMMERCIAL) {
					//has full/lighs versions
					builder.append("#define PAID_GAME\n");
					break;
				}
			}
		}
		
		module = builder.toString();
		fileWriter = new FileOutputStream("content/iphone/global/GameDefenitions.h");
		try {
			fileWriter.write(module.getBytes("UTF-8"));
		} finally {
			fileWriter.close();
		}
		builder.setLength(0);
		builder.append(command.path);
		builder.append(File.separator);
		int len = builder.length();
		builder.append("Default.png");
		copyFileToFolder(builder.toString(),OUTPUT_PATH);
		builder.setLength(len);
		builder.append("Default2.png");
		copyFileToFolder(builder.toString(),OUTPUT_PATH);
		builder.setLength(len);
		if (command.alterLogo == null) {
			builder.append("logo_small.png");
			copyFileToFolder(builder.toString(),OUTPUT_PATH);
		} else {
			builder.append(command.alterLogo);
			copyFileToFolder(builder.toString(),OUTPUT_PATH,"logo_small.png");
		}
		//help file
		builder.setLength(len);
		builder.append("help.txt");
		copyFileToFolder(builder.toString(),"src/images");
	}
	
	private void copyFileToFolder(String source, String folder, String newName) throws Exception {
		File file = new File(source);
		copyFile(file,new File(folder.concat(File.separator).concat(newName)));
	}

	private void copyFileToFolder(String source, String folder) throws Exception {
		File file = new File(source);
		copyFile(file,new File(folder.concat(File.separator).concat(file.getName())));
		
	}
	private void appendSuffix(StringBuilder builder, boolean demoVersion) {
		if (demoVersion) {
			builder.append("l:");
		} else {
			builder.append("f:");
		}
	}
	private void populateMethods(StringBuilder builder, ModelPersist model,String content,String output) throws Exception {
		ArrayList<Paragraph> list = model.getParagraphs();
		for (Paragraph paragraph : list) {
			builder.append("public String ");
			builder.append(PREFIX_PARAGRAPH);
			builder.append(paragraph.getId());
			builder.append("(){\nreturn \"");
			builder.append(toText(paragraph.getDescription()));
			builder.append("\";\n}\n");
			paragraph.setDescription("");
		}
		ArrayList<Picture> images = model.getPictures();
		for (Picture picture : images) {
			picture.setUrl(copyContent(picture.getUrl(),content,output));
		}
		ArrayList<Sound> sounds = model.getSounds();
		for (Sound sound : sounds) {
			sound.setUrl(copyContent(sound.getUrl(),content,output));
		}
		JSONBuilder result = JSONBuilder.getStartInstance();
		model.toJSON(Model.EXPORT_PLAY, result);
		builder.append("public String ");
		builder.append(PREFIX_PARAGRAPH);
		builder.append("(){\n");
		FileOutputStream fileWriter = new FileOutputStream("src/images/model.txt");
		try {
			fileWriter.write(result.toString().getBytes("UTF-8"));
		} finally {
			fileWriter.close();
		}
		builder.append("return com.iambookmaster.client.iphone.images.IPhoneImages.INSTANCE.model().getText();\n}\n");
	}
	
	protected String normalizeURL(String url) {
		int i = url.lastIndexOf("/");
		if (i>0) {
			url = url.substring(i+1);
		}
		i = url.lastIndexOf("\\");
		if (i>0) {
			url = url.substring(i+1);
		}
		return url;
	}
	
	private String toText(String text) {
		return text.replace("\n", "\\n").replace("\"", "\\\"");
	}
	private void populateModel(StringBuilder builder, ModelPersist model) {
		builder.append("try {\n");
		builder.append("model.restore(JSONParser.eval(");
		builder.append(PREFIX_PARAGRAPH);
		builder.append("()),JSONParser.getInstance());\n");
		
		ArrayList<Paragraph> list = model.getParagraphs();
		int i=0;
		for (Paragraph paragraph : list) {
			builder.append("model.getParagraphs().get(");
			builder.append(i++);
			builder.append(").setDescription(");
			builder.append(PREFIX_PARAGRAPH);
			builder.append(paragraph.getId());
			builder.append("());\n");
		}
		
		builder.append("listener.success(model);\n");
		builder.append("} catch (JSONException e) {\n");
		builder.append("listener.error(e);\n");
		builder.append("}\n");
	}
	
	
	
	
}
