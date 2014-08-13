package com.iambookmaster.client.iurq;

import com.iambookmaster.client.iurq.logic.Btn;
import com.iambookmaster.client.iurq.logic.Pause;
import com.iambookmaster.client.iurq.logic.Play;
import com.iambookmaster.client.iurq.logic.URQImage;


public interface URQUI {

	public void resizeItems();

	public void doExit();

	public void print(String s, int i);

	public void clear();

	public void invRefresh();

	public void addButton(Btn btn);

	public void enableInput();

	public void disableInput();

	public String getInput();

	public String loadFile(String s);

	public void end();

	public void showImage(URQImage image);

	public void play(Play operator);

	public void anykey();

	public void save(String location);

	public void pause(Pause pause);

}