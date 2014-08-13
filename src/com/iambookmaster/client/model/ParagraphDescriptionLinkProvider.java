package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;

public interface ParagraphDescriptionLinkProvider {

	public String getLinkTo(Paragraph from, Paragraph to, ParagraphConnection connection);

	public String getModificatorValue(Modificator modificator);

}
