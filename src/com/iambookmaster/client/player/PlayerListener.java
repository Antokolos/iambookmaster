package com.iambookmaster.client.player;

import java.util.ArrayList;

import com.iambookmaster.client.beans.Paragraph;

public interface PlayerListener {

	void edit(Paragraph location);

	void showErrors(Paragraph location,ArrayList<String> errors);

	void emptyDescription(Paragraph currentLocation);

}
