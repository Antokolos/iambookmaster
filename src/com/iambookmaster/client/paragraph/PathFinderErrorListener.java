package com.iambookmaster.client.paragraph;

import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;

public interface PathFinderErrorListener {

	void bothDirConnectionHasObject(ParagraphConnection connection);

	void duplicateConnectionBetweenParagraphs(ParagraphConnection connection);

	void outwayFromFialOrSuccessParagraph(Paragraph paragraph);

	void noWayFromNormalParagraph(Paragraph paragraph);

	void startFromFialOrSuccessParagraph(Paragraph paragraph);

	void noWayToSuccess(Paragraph paragraph);

	void alreadyHaveThatObject(Paragraph current, ObjectBean object);

	void uselessObjectInFailOrSuccess(Paragraph paragraph);

	void objectCannotBeFound(ObjectBean object);

	void objectCannotBeUsed(ObjectBean bean);

	void unriachebleParagraph(Paragraph bean);

	void unusedParagraphConnection(ParagraphConnection bean);

	void startLocationIsNotDefined();

	void startHasIncomeConection(Paragraph paragraph);

	boolean checkTimeout();

	/**
	 * Chain of conditional connections
	 */
	void conditionalChain(ParagraphConnection connection);

	void gotAndLostObjectInTheSameParagraph(Paragraph paragraph, ObjectBean bean);

	void twoOutputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object);

	void twoInputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object);

	void modificatorsInFialOrSuccessParagraph(Paragraph paragraph);

	void parametersInFromFialOrSuccessParagraph(Paragraph paragraph);

	void modificatorIsSetNowhere(Modificator modificator);

	void unusedModificator(Modificator modificator);

	void modificatorNotSetInConnection(ParagraphConnection connection);

	void parameterNotSetInConnection(ParagraphConnection connection);

	void unusedParameter(Parameter parameter);

	void battleIsUsedNowhere(Battle battle);

	void alchemyIsUsedNowhere(Alchemy alchemy);

	void NPCIsUsedNowhere(NPC npc);

	void noSuccessParagraphs();

	void updateStatus(int paragraphs, int connections);

	boolean canContinue();

	void mustGoAndNormaConnectionsInParagraph(Paragraph paragraph);

	void done();

	boolean canBePassed(ParagraphConnection connection, Paragraph paragraph);
}
