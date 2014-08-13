package com.iambookmaster.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.model.Model;

public class ModelOptimizer {

	public static void arrange(Model model) {
		new ModelArrange(model);
	}
	
	public static class ModelArrange {

		private Model model;
		private HashMap<Paragraph, ArrayList<ParagraphConnection>> links;
		private Paragraph[][] map;
		private HashSet<Paragraph> unused;
		private int widht;
		private int height;
		private boolean removed;
		private int strategy;
		public ModelArrange(Model mod) {
			model = mod; 
			unused = new HashSet<Paragraph>();
			unused.addAll(model.getParagraphs());
			if (unused.size() < 21) {
				//nothing to do
				return;
			}
			for (Paragraph paragraph : unused) {
				paragraph.setX(0);
				paragraph.setY(0);
			}
			ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
			links = new HashMap<Paragraph, ArrayList<ParagraphConnection>>(unused.size());
			for (ParagraphConnection connection : connections) {
				ArrayList<ParagraphConnection> list = links.get(connection.getFrom());
				if (list==null) {
					list = new ArrayList<ParagraphConnection>();
					links.put(connection.getFrom(), list);
				}
				list.add(connection);
			}
			height = unused.size() / 3;
			widht = Math.max(height/ 2 ,1)+1;
			height = height + 1;
			connections = null;
			map = new Paragraph[widht][height];
			Paragraph start = model.getStartParagraph();
			//put from Start
			placeParagraph(0,0,start,true);
			//put unused paragraphs
			while (unused.size()>0) {
				Iterator<Paragraph> iterator = unused.iterator();
				removed = false;
				while (iterator.hasNext()) {
					Paragraph paragraph = iterator.next();
					//max deep
					findPlaceAndPlace(0, 0, paragraph,false);
					if (removed) {
						//children were removed, restart
						break;
					} else {
						//just remove
						iterator.remove();
					}
				}
			}
			//read data
			int maxX = 0;
			int maxY = 0;
			for (int i = 0; i < widht; i++) {
				for (int j = 0; j < height; j++) {
					if (map[i][j] != null) {
						Paragraph paragraph = map[i][j];
						paragraph.setX(i*150+500);
						paragraph.setY(j*60+300);
						if (paragraph.getX()>maxX) {
							maxX = paragraph.getX();
						}
						if (paragraph.getY()+100>maxY) {
							maxX = paragraph.getY();
						}
					}
				}
			}
			if (maxX + 200 > model.getSettings().getMaxDimensionX()) {
				model.getSettings().setMaxDimensionX(maxX+200);
			}
			if (maxY + 100 > model.getSettings().getMaxDimensionY()) {
				model.getSettings().setMaxDimensionY(maxY+100);
			}
		}

		private void placeParagraph(int x, int y, Paragraph paragraph,boolean remove) {
			strategy++;
			if (strategy>3) {
				strategy = 0;
			}
			if (remove) {
				unused.remove(paragraph);
				removed = true;
			}
			map[x][y]=paragraph;
			paragraph.setX(x);
			paragraph.setY(y);
			ArrayList<ParagraphConnection> list = links.get(paragraph);
			boolean child=false;
			if (list !=null && list.size()>0) {
				//can place more
				Iterator<ParagraphConnection> iterator = list.iterator();
				while (iterator.hasNext()) {
					ParagraphConnection connection = iterator.next();
					if (unused.contains(connection.getTo())) {
						//not in map yet
						findPlaceAndPlace(x,y,connection.getTo(),true);
					}
				}
			}
		}

		private void findPlaceAndPlace(int x, int y, Paragraph paragraph,boolean remove) {
			int step=1;
			while (true) {
				switch (strategy) {
				case 0:
					if (placeTop(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeBottom(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeLeft(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeRight(x,y,step,paragraph,remove)) {
						return;
					}
					break;
				case 1:
					if (placeLeft(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeBottom(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeRight(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeTop(x,y,step,paragraph,remove)) {
						return;
					}
					break;
				case 2:
					if (placeBottom(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeLeft(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeRight(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeTop(x,y,step,paragraph,remove)) {
						return;
					}
					break;

				default:
					if (placeRight(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeTop(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeLeft(x,y,step,paragraph,remove)) {
						return;
					}
					if (placeBottom(x,y,step,paragraph,remove)) {
						return;
					}
					break;
				}
				//extend step
				step++;
			}
		}

		private boolean placeRight(int x, int y, int step, Paragraph paragraph,	boolean remove) {
			int y1 = x+step;
			if (y1<widht) {
				//rigth line
				int from = Math.max(y-step,0);
				int to = Math.min(y+step,height);
				for (int i = from; i < to; i++) {
					if (map[y1][i]==null) {
						//found place
						placeParagraph(y1, i, paragraph,remove);
						return true;
					}
				}
			}
			return false;
		}

		private boolean placeLeft(int x, int y, int step, Paragraph paragraph,boolean remove) {
			int y1 = x-step;
			if (y1>=0) {
				//left line
				int from = Math.max(y-step,0);
				int to = Math.min(y+step,height);
				for (int i = from; i < to; i++) {
					if (map[y1][i]==null) {
						//found place
						placeParagraph(y1, i, paragraph,remove);
						return true;
					}
				}
			}
			return false;
		}

		private boolean placeBottom(int x, int y, int step,	Paragraph paragraph, boolean remove) {
			int y1 = y + step;
			if (y1<height) {
				//bottom line
				int from = Math.max(x-step,0);
				int to = Math.min(x+step,widht);
				for (int i = from; i < to; i++) {
					if (map[i][y1]==null) {
						//found place
						placeParagraph(i, y1, paragraph,remove);
						return true;
					}
				}
			}
			return false;
		}

		private boolean placeTop(int x, int y, int step, Paragraph paragraph, boolean remove) {
			int y1 = y-step;
			if (y1>=0) {
				//top line
				int from = Math.max(x-step,0);
				int to = Math.min(x+step,widht);
				for (int i = from; i < to; i++) {
					if (map[i][y1]==null) {
						//found place
						placeParagraph(i, y1, paragraph,remove);
						return true;
					}
				}
			}
			return false;
		}
		
	}


}
