package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;

public interface ParagraphParsingHandler {

	void addText(Paragraph paragraph,String text);

	void addObject(Paragraph paragraph, ObjectBean objectBean, String key);

	void addLinkTo(Paragraph paragraph, Paragraph next, ParagraphConnection connection);

	void addAlchemy(Paragraph paragraph, String toValue, Alchemy alchemy);

	void addBattle(Battle battle, Paragraph paragraph);

	void addAlchemyFromValue(Paragraph paragraph, String value);

}
