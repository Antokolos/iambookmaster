package com.iambookmaster.client.paragraph;

import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;

public interface BookCreatorListener {

	int ERROR_NON_UNIQUE_ABSOLUTE_MODIFICATOR = 10;
	int ERROR_MODIFICATOR_NOT_SET = 9;
	int ERROR_PARAGRAPH_NUMBER_NOT_SET = 2;

	void algorithmError(int code);

	void iterationFailed(int fail, int total);

	void allIterationsFailed();

	void numberNotSet(ObjectBean objectBean);

	void numberTooLarge(Paragraph paragraph, int max);

	void numbersDuplicated(Paragraph paragraph, Paragraph paragraph2);

	void noSupported();

	/**
	 * @return true if we out of time
	 */
	boolean checkTimiout();

	void numberNotSet(Paragraph paragraph);

	void wrongObjectSecretKey(ParagraphConnection connection);

	void tooManyObjects();

}
