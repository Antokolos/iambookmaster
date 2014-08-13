package com.iambookmaster.client.iphone;

import com.iambookmaster.client.beans.Paragraph;

public interface IPhonePlayerListener {

	boolean onParagraph(Paragraph paragraph);

	boolean onOpenPlayerList();

	boolean onOpenFeedback();

}
