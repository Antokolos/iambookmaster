package com.iambookmaster.client.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;

import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public class TextBookImporter {

	private boolean paragraphNumberInSeparateLine;
	private char paragraphNumberSeparator='.';
	private char paragraphReferenceStart='<';
	private char paragraphReferenceEnd='>';
	private char secretKeyStart='(';
	private char secretKeyEnd=')';
	private boolean secretKeyDetection;
	
	private TextBookImporterListener listener;
	private ArrayList<Paragraph> paragraphs;
	private Model model;
	private boolean success;
	private AppConstants appConstants;
	private AppMessages appMessages;
	public TextBookImporter(TextBookImporterListener listener,AppConstants appConstants,AppMessages appMessages){
		this.listener = listener;
		this.appConstants = appConstants;
		this.appMessages = appMessages;
	}

	public void importBook(Model mod, String text) {
		paragraphs = new ArrayList<Paragraph>();
		model = mod;
		listener.startStage(appConstants.importStartImporting());
		success = false;
		int nextPos=0;
		int nextNumber=-1;
		StringBuffer buffer = new StringBuffer();
		boolean nextIsNumber=true;
		while (true) {
			int pos = text.indexOf('\n',nextPos);
			String nextLine;
//			if (pos==nextPos) {
//				nextLine = "";
//			} else if (pos>nextPos) {
			if (pos>=nextPos) {
				nextLine = text.substring(nextPos,pos).trim();
			} else {
				//last
				nextLine = text.substring(nextPos).trim();
			}
			//check for paragraph number
			if (nextIsNumber) {
				//can be number
				int sep = nextLine.indexOf(paragraphNumberSeparator);
				if (sep<0 && paragraphNumberInSeparateLine) {
					//check for just number in line
					sep = nextLine.length();
				} 
				if (sep>0) {
					String nm = nextLine.substring(0,sep);
					if (paragraphNumberInSeparateLine) {
						nm = nextLine;
					} else {
						nm = nextLine.substring(0,sep);
					}
					int num=-1;
					try {
						num = Integer.parseInt(nm);
						if (String.valueOf(num).equals(nm)) {
							//yes!!!
						} else {
							//wrong
							num=-1;
						}
					} catch (NumberFormatException e) {
						//error in number
						num=-1;
					}
					if (num>=0) {
						//next paragraph number found
						if (buffer.length()>0) {
							Paragraph paragraph = model.addNewParagraph(null);
							paragraph.setDescription(buffer.toString());
							paragraph.setNumber(nextNumber);
							paragraphs.add(paragraph);
						}
						buffer = new StringBuffer();
						if (paragraphNumberInSeparateLine) {
							nextIsNumber = false;
						} else {
							buffer.append(nextLine.substring(sep+1).trim());
						}
						nextNumber = num;
					} else {
						//just text
						buffer.append('\n');
						buffer.append(nextLine);
					}
				} else {
					//just text
					buffer.append('\n');
					buffer.append(nextLine);
				}
			} else {
				//just text
				buffer.append(nextLine);
				nextIsNumber = true;
			}
			if (pos<0) {
				break;
			} else {
				nextPos = pos+1;
			}
		}
		if (nextNumber>0) {
			Paragraph paragraph = model.addNewParagraph(null);
			paragraph.setDescription(buffer.toString());
			paragraph.setNumber(nextNumber);
			paragraphs.add(paragraph);
			success = true;
			listener.info(appMessages.importParagraphWasImported(paragraphs.size()));
			listener.endStage(appConstants.importEndImportingParagraphs());
			listener.startStage(appConstants.importStartParsingParagraphs());
			
			for (int i = 0; i < paragraphs.size(); i++) {
				Paragraph parapraphText = paragraphs.get(i);
				String desc = parapraphText.getDescription();
				//try to detect name
				int pos = desc.indexOf('\n');
				if (pos<=0) {
					parapraphText.setName(appConstants.importNoNameParagraph());
				} else if (pos<30) {
					parapraphText.setName(desc.substring(0,pos-1));
				} else {
					parapraphText.setName(desc.substring(0,30));
				}
			}
			
			listener.endStage(appConstants.importEndParsingParagraphs());
		} else {
			listener.error((appConstants.importNoParagraphsParsed()));
		}
	}	
	
	
	public boolean isSuccess() {
		return success;
	}

	public boolean isParagraphNumberInSeparateLine() {
		return paragraphNumberInSeparateLine;
	}

	public void setParagraphNumberInSeparateLine(
			boolean paragraphNumberInSeparateLine) {
		this.paragraphNumberInSeparateLine = paragraphNumberInSeparateLine;
	}

	public char getParagraphNumberSeparator() {
		return paragraphNumberSeparator;
	}

	public void setParagraphNumberSeparator(char paragraphNumberSeparator) {
		this.paragraphNumberSeparator = paragraphNumberSeparator;
	}

	public char getParagraphReferenceEnd() {
		return paragraphReferenceEnd;
	}

	public void setParagraphReferenceEnd(char paragraphReferenceEnd) {
		this.paragraphReferenceEnd = paragraphReferenceEnd;
	}

	public char getParagraphReferenceStart() {
		return paragraphReferenceStart;
	}

	public void setParagraphReferenceStart(char paragraphReferenceStart) {
		this.paragraphReferenceStart = paragraphReferenceStart;
	}

	public ArrayList<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void parseParagraphsText() {
		//check integrity
		HashMap<Integer,Paragraph> nums = new HashMap<Integer,Paragraph>(paragraphs.size());
		boolean critical=false;
		listener.startStage(appConstants.importStartParsingParagraphs());
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph paragraph = paragraphs.get(i);
			Integer key = new Integer(paragraph.getNumber());
			if (nums.containsKey(key)) {
				//error
				listener.error(appMessages.importDuplicateParagraphNumbers(paragraph.getNumber()));
				critical=true;
			} else {
				nums.put(key,paragraph);
			}
		}
		if (critical) {
			listener.endStage(appMessages.importParsingTerminated());
			return;
		}
		//parse all paragraphs
		model.getParagraphConnections().clear();
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph paragraph = paragraphs.get(i);
			parseParagraph(paragraph,nums);
		}
		listener.info(appMessages.importTotalConnectionsFound(model.getParagraphConnections().size()));
		//convert double connections to two-way
		LinkedHashMap<Paragraph, ArrayList<ParagraphConnection>> list = new LinkedHashMap<Paragraph, ArrayList<ParagraphConnection>>(model.getParagraphs().size());
		Iterator<ParagraphConnection> conns = model.getParagraphConnections().iterator();
		next_con:
		while (conns.hasNext()) {
			ParagraphConnection connection = conns.next();
			ArrayList<ParagraphConnection> curr;
			if (list.containsKey(connection.getFrom())) {
				curr = list.get(connection.getFrom());
			} else {
				//new one
				curr = new ArrayList<ParagraphConnection>();
				list.put(connection.getFrom(), curr);
			}
			for (int j = 0; j < curr.size(); j++) {
				ParagraphConnection cn = curr.get(j);
				if (cn.getTo()==connection.getTo()) {
					//just duplicate
					listener.warning(appMessages.importDuplicateConnectionRemoved(connection.getFrom().getNumber(),connection.getTo().getNumber()));
					conns.remove();
					continue next_con;
				}
			}
			curr.add(connection);
			curr = list.get(connection.getTo());
			if (curr != null) {
				//check for two way connection
				for (int i = 0; i < curr.size(); i++) {
					ParagraphConnection cn = curr.get(i);
					if (cn.getTo()==connection.getFrom()) {
						//duplicate
						cn.setBothDirections(true);
						listener.info(appMessages.importTwoWayConnectionFound(connection.getFrom().getNumber(),connection.getTo().getNumber()));
						conns.remove();
						continue next_con;
					}
				}
			}
			
		}
		//look for start
		list = null;
		HashSet<Paragraph> noInput = new HashSet<Paragraph>(paragraphs.size());
		noInput.addAll(paragraphs);
		conns = model.getParagraphConnections().iterator();
		while (conns.hasNext()) {
			ParagraphConnection connection = conns.next();
			noInput.remove(connection.getTo());
			if (connection.isBothDirections()) {
				noInput.remove(connection.getFrom());
			}
		}
		if (noInput.size()==1) {
			Paragraph paragraph = noInput.iterator().next();
			if (paragraph.isFail()) {
				listener.warning(appConstants.importCannotDetectStart());
			} else {
				listener.info(appMessages.importDetectedStartParagraph(paragraph.getNumber()));
				model.makeParagraphAsStart(paragraph);
			}
		} else if (noInput.size()>1) {
			Paragraph paragraph=null;
			for (Iterator<Paragraph> iter = noInput.iterator();iter.hasNext();) {
				paragraph = iter.next();
				if (paragraph.isFail()==false) {
					break;
				}
			}
			if (paragraph==null) {
				listener.warning(appConstants.importCannotDetectStart());
			} else {
				listener.warning(appConstants.importCannotDetectStartProperly());
				model.makeParagraphAsStart(paragraph);
			}
		} else {
			listener.warning((appConstants.importCannotDetectStartProperlyAllHave()));
		}
		listener.endStage(appConstants.importEndParsingParagraphs());
	}

	private void parseParagraph(Paragraph paragraph, HashMap<Integer, Paragraph> nums) {
		listener.startParseParagraph(paragraph);
		boolean hasOutPut=false;
		if (paragraphReferenceStart==' ') {
			//no marks, extract all numbers
			//add 1 more character to avoid last digit problem
			String descr = paragraph.getDescription()+" ";
			StringBuffer buffer = new StringBuffer();
			char[] text = descr.toCharArray();
			int pos=-1;
			int start=0;
			for (int i = 0; i < text.length; i++) {
				if (Character.isDigit(text[i])) {
					if (pos<0) {
						//start
						pos = i;
					}
				} else if (pos>0){
					//we had digits
					String nm = descr.substring(pos, i);
					int num = Integer.parseInt(nm);
					if (num != paragraph.getNumber()) {
						Integer key = num;
						if (nums.containsKey(key)) {
							//found link
							hasOutPut = true;
							Paragraph link = nums.get(key);
							ParagraphConnection connection = new ParagraphConnection();
							connection.setFrom(paragraph);
							connection.setTo(link);
							connection.setFromId(String.valueOf(paragraph.getNumber()));
							connection.setToId(String.valueOf(num));
							model.addParagraphConnection(connection, null);
							String add = descr.substring(start, pos);
							buffer.append(add);
							listener.parseParagraphAddText(add);
							String addDel = Model.CONNECTION_DELIMETER_FROM+descr.substring(pos, i)+Model.CONNECTION_DELIMETER_TO;
							buffer.append(addDel);
							listener.parseParagraphAddLink(addDel,link);
							start = i;
							pos = -1;
							continue;
						}
					}
					pos = -1;
				}
			}
			String add = descr.substring(start);
			buffer.append(add);
			listener.parseParagraphAddText(add);
			paragraph.setDescription(buffer.toString());
		} else {
			int pos=0;
			String text = paragraph.getDescription();
			StringBuffer result = null;
			String start = String.valueOf(paragraphReferenceStart);
			String end = String.valueOf(paragraphReferenceEnd);
			while (true) {
				int nextPos = text.indexOf(start,pos);
				if (nextPos>=0) {
					int endPos = text.indexOf(end,nextPos);
					if (endPos>0) {
						//remember next position
						String er = text.substring(nextPos, endPos+1);
						if (pos<nextPos) {
							listener.parseParagraphAddText(text.substring(pos,nextPos));
						}
						pos = endPos+1;
						if (endPos>nextPos) {
							String val = text.substring(nextPos+1, endPos).trim();
							try {
								int num = Integer.parseInt(val);
								Integer key = num;
								if (nums.containsKey(key)) {
									//found connection!!!
									hasOutPut = true;
									Paragraph link = nums.get(key);
									ParagraphConnection connection = new ParagraphConnection();
									connection.setFrom(paragraph);
									connection.setTo(link);
									connection.setFromId(String.valueOf(paragraph.getNumber()));
									connection.setToId(String.valueOf(num));
									model.addParagraphConnection(connection, null);
									if (result==null) {
										result = new StringBuffer(text);
									}
									result.setCharAt(nextPos, Model.CONNECTION_DELIMETER_FROM);
									result.setCharAt(endPos, Model.CONNECTION_DELIMETER_TO);
									listener.parseParagraphAddLink(er,link);
								} else {
									listener.error(appMessages.importLinkToUnknowParagraph(paragraph.getNumber(),er));
									listener.parseParagraphAddError(er);
								}
							} catch (NumberFormatException e) {
								listener.error(appMessages.importIncorrectLink(paragraph.getNumber(),er));
								listener.parseParagraphAddError(er);
							}
						} else {
							listener.error(appMessages.importEmptyLink(paragraph.getNumber(),er));
							listener.parseParagraphAddError(start+" "+end);
						}
					} else {
						listener.error(appMessages.importParagraphDoesNotHaveCloseTag(paragraph.getNumber()));
						break;
					}
				} else {
					break;
				}
			}
			if (result != null) {
				paragraph.setDescription(result.toString());
			}
			if (pos>0) {
				listener.parseParagraphAddText(text.substring(pos));
			} else {
				listener.parseParagraphAddText(text);
			}
		}
		if (hasOutPut) {
			paragraph.setType(Paragraph.TYPE_NORMAL);
		} else {
			paragraph.setType(Paragraph.TYPE_FAIL);
		}
		listener.endParseParagraph(paragraph);
	}

	public void traseMap() {
		ArrayList<Paragraph> list = model.getParagraphs();
		int colMax = (int)(1+Math.round(Math.sqrt(list.size())));
		int row=0;
		int col=0;
		for (int i = 0; i < list.size(); i++) {
			Paragraph paragraph = list.get(i);
			paragraph.setX(10+row*150);
			paragraph.setY(10+col*40);
			col++;
			if (col>=colMax) {
				col=0;
				row++;
			}
		}
	}

	public char getSecretKeyStart() {
		return secretKeyStart;
	}

	public void setSecretKeyStart(char secretKeyStart) {
		this.secretKeyStart = secretKeyStart;
	}

	public char getSecretKeyEnd() {
		return secretKeyEnd;
	}

	public void setSecretKeyEnd(char secretKeyEnd) {
		this.secretKeyEnd = secretKeyEnd;
	}

	public boolean isSecretKeyDetection() {
		return secretKeyDetection;
	}

	public void setSecretKeyDetection(boolean secretKeyDetection) {
		this.secretKeyDetection = secretKeyDetection;
	}

}
