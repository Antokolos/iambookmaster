package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.Paragraph;

public interface ParagraphListener {

	void addNewParagraph(Paragraph location);

	void refreshAll();

	void select(Paragraph location);

	void unselect(Paragraph location);

	void update(Paragraph location);

	void edit(Paragraph location);

	void remove(Paragraph location);

}
