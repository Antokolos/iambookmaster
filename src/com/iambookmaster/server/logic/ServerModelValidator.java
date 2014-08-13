package com.iambookmaster.server.logic;

import java.util.ArrayList;

import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.exceptions.TimeoutException;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.Model.FullParagraphDescriptonBuilder;
import com.iambookmaster.client.paragraph.PathFinder;
import com.iambookmaster.client.paragraph.PathFinderErrorListener;

/**
 * Server-side validator for Game book
 * @author ggadyatskiy
 */
public class ServerModelValidator extends AbstractModelProcessor {

	private PathFinder validator;
	private ServerModelValidatorListener validatorListener;
	private PathFinderErrorListener pathFinderErrorListener;
	private boolean mapValidation;
	public ServerModelValidator(Model model,AppConstants appConstants,AppMessages appMessages) {
		super(model,appConstants,appMessages);
		validator = new PathFinder(model);
		validator.setCheckSuccessOnly(true);
	}


	public boolean validate(ServerModelValidatorListener listener) throws TimeoutException{
		clearErrors();
		validatorListener = listener;
		pathFinderErrorListener = new PathFinderErrorListener() {
			public void alreadyHaveThatObject(Paragraph current, ObjectBean object) {
				if (object.isUncountable()==false) {
					appendObject(object);
					appendErrorText(appConstants.serverValidatorObjectCanBeGotManyTimes());
					appendParagraph(current);
					appendErrorEndLine();
				}
			}

			public void bothDirConnectionHasObject(
					ParagraphConnection connection) {
				appendParagraphConnection(connection);
				appendErrorText(appConstants.serverValidatorConnectionHasCondition());
				appendObject(connection.getObject());
				appendErrorEndLine();
			}

			public void duplicateConnectionBetweenParagraphs(
					ParagraphConnection connection) {
				appendErrorText(appConstants.serverValidatorDuplicateConnection());
				appendParagraph(connection.getFrom());
				appendErrorText(appConstants.serverValidatorDuplicateConnectionAnd());
				appendParagraph(connection.getTo());
				appendErrorEndLine();
			}

			public void noWayFromNormalParagraph(Paragraph paragraph) {
				appendErrorText(appConstants.serverValidatorNoWayFrom());
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void noWayToSuccess(Paragraph paragraph) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverValidatorParagraphCannotBeReached());
				appendErrorEndLine();
			}

			public void objectCannotBeFound(ObjectBean object) {
				appendObject(object);
				appendErrorText(appConstants.serverValidatorObjectCannotBeFound());
				appendErrorEndLine();
			}

			public void objectCannotBeUsed(ObjectBean object) {
//				appendObject(object);
//				appendErrorText(appConstants.serverValidatorCannotBeUsed());
//				appendErrorEndLine();
			}

			public void outwayFromFialOrSuccessParagraph(Paragraph paragraph) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverValidatorParagraphHasOutcome());
				appendErrorEndLine();
			}

			public void startFromFialOrSuccessParagraph(Paragraph paragraph) {
				appendErrorText(appConstants.serverValidatorStartsFrom());
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void startHasIncomeConection(Paragraph paragraph) {
				appendErrorText(appConstants.serverValidatorStartsHasIncome());
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void startLocationIsNotDefined() {
				appendErrorText(appConstants.modelStartParagraphNotSet());
				appendErrorEndLine();
			}

			public void unriachebleParagraph(Paragraph paragraph) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverValidatorParagraphCannotBeReached());
				appendErrorEndLine();
			}

			public void unusedParagraphConnection(ParagraphConnection connection) {
				appendParagraphConnection(connection);
				appendErrorText(appConstants.serverValidatorObjectCannotBeUsed());
				appendErrorEndLine();
			}

			public void uselessObjectInFailOrSuccess(Paragraph paragraph) {
				appendParagraph(paragraph);
				appendErrorText(appConstants.serverValidatorParagraphHasObjects());
				appendErrorEndLine();
			}

			public void conditionalChain(ParagraphConnection connection) {
				appendErrorText(appConstants.serverValidatorChainDetected());
				appendParagraphConnection(connection);
				appendErrorEndLine();
			}
			
			public boolean checkTimeout() {
				return validatorListener.checkTimeout();
			}

			public void gotAndLostObjectInTheSameParagraph(Paragraph paragraph,ObjectBean bean) {
				appendObject(bean);
				appendErrorText(appConstants.serverValidatorObjectLostAndFoundInTheSamePlace());
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void twoInputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
				appendErrorText(appMessages.twoInputConnectionsWithTheSameObject(paragraph.getName(),object.getName()));
				appendErrorEndLine();
			}

			public void twoOutputConnectionsWithTheSameObject(Paragraph paragraph, ObjectBean object) {
				appendErrorText(appMessages.twoOutputConnectionsWithTheSameObject(paragraph.getName(),object.getName()));
				appendErrorEndLine();
			}

			public void modificatorIsSetNowhere(Modificator modificator) {
				appendErrorText(appMessages.modificatorIsSetNowhere(modificator.getName()));
				appendErrorEndLine();
			}

			public void modificatorNotSetInConnection(ParagraphConnection connection) {
				appendErrorText(appMessages.modificatorNotSetInConnection(connection.getFrom().getName(),connection.getTo().getName()));
				appendParagraphConnection(connection);
				appendErrorEndLine();
			}

			public void modificatorsInFialOrSuccessParagraph(Paragraph paragraph) {
				appendErrorText(appMessages.modificatorsInFialOrSuccessParagraph(paragraph.getName()));
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void parameterNotSetInConnection(ParagraphConnection connection) {
				appendErrorText(appMessages.parameterNotSetInConnection(connection.getFrom().getName(),connection.getTo().getName()));
				appendParagraphConnection(connection);
				appendErrorEndLine();
			}

			public void parametersInFromFialOrSuccessParagraph(Paragraph paragraph) {
				appendErrorText(appMessages.modificatorsInFialOrSuccessParagraph(paragraph.getName()));
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void unusedModificator(Modificator modificator) {
				appendErrorText(appMessages.unusedModificator(modificator.getName()));
				appendErrorEndLine();
			}

			public void unusedParameter(Parameter parameter) {
				appendErrorText(appMessages.unusedParameter(parameter.getName()));
				appendErrorEndLine();
			}
			
			public void NPCIsUsedNowhere(NPC npc) {
				appendErrorText(appMessages.NPCIsUsedNowhere(npc.getName()));
				appendErrorEndLine();
			}

			public void alchemyIsUsedNowhere(Alchemy alchemy) {
				appendErrorText(appMessages.alchemyIsUsedNowhere(alchemy.getName()));
				appendErrorEndLine();
			}

			public void battleIsUsedNowhere(Battle battle) {
				appendErrorText(appMessages.battleIsUsedNowhere(battle.getName()));
				appendErrorEndLine();
			}

			public void noSuccessParagraphs() {
				appendErrorText(appConstants.noSuccessParagraphsDefined());
				appendErrorEndLine();
			}

			public void updateStatus(int paragraphs, int connections) {
			}

			public boolean canContinue() {
				return true;
			}

			public void mustGoAndNormaConnectionsInParagraph(Paragraph paragraph) {
				appendErrorText(appMessages.mustGoAndNormaConnectionsInParagraph(paragraph.getName()));
				appendParagraph(paragraph);
				appendErrorEndLine();
			}

			public void done() {
			}

			public boolean canBePassed(ParagraphConnection connection, Paragraph paragraph) {
				return true;
			}
		};
		//engine is ready, start
		mapValidation = true;
		validator.validate(pathFinderErrorListener);
		valiateTextCounter=0;
		mapValidation = false;
		validateText();
		return getErrors()==null;
	}

	private int valiateTextCounter=0;
	private void validateText() throws TimeoutException {
		//validate text
		ArrayList<Paragraph> paragrapghs = getModel().getParagraphs();
		FullParagraphDescriptonBuilder builder = getModel().getFullParagraphDescriptonBuilder();
		builder.setCheckSecretKeys(true);
		builder.setHiddenUsingObjects(false);
		for (; valiateTextCounter < paragrapghs.size(); valiateTextCounter++) {
			Paragraph paragraph = paragrapghs.get(valiateTextCounter);
			ArrayList<String> errors = new ArrayList<String>();
			builder.getFullParagraphDescripton(paragraph, null, errors,null);
			for (int j = 0; j < errors.size(); j++) {
				appendParagraph(paragraph);
				appendErrorText(errors.get(j));
				appendErrorEndLine();
			}
			if (validatorListener.checkTimeout()) {
				throw new TimeoutException();
			}
		}
	}


	public boolean continueValidation(ServerModelValidatorListener validatorListener) throws TimeoutException {
		if (mapValidation) {
			validator.continueProgess();
			valiateTextCounter=0;
			mapValidation = false;
			validateText();
		} else {
			validateText();
		}
		return getErrors()==null;
	}




}
