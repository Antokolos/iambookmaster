package com.iambookmaster.client.locale;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.iambookmaster.client.beans.Parameter;







@DefaultLocale("ru")
public interface AppMessages extends com.google.gwt.i18n.client.Messages {
  
  @DefaultMessage("'I am book master' editor. Version {0}.{1}")
  String aboutTheProgram(int hiVersion, int loVersion);
  
  @DefaultMessage("'I am book master' editor, new version {0}.{1} is available")
  String getAboutTitle(int min, int maj);

  @DefaultMessage("Join other project to paragraph {0}")
  String confirmJoinOtherProject(String name);

  @DefaultMessage("Welcome, {0}")
  String jspMainToWelcome(String name);

  @DefaultMessage("Start Paragraph: {0}")
  String textDecoratorStartParagraph(int number);
  
  @DefaultMessage("Error parsing data\n {0}")
  String modelErrorParsingData(String message);
  
  @DefaultMessage("Some objects from list of IDs {0} were not found")
  String modelUnknownObjectsIDs(String objectId);

  @DefaultMessage("Paragraph with ID={0} does not exist")
  String modelUnknownParagraphId(String id1);
  
  @DefaultMessage("Object with ID={0} does not exist")
  String modelUnknownObjectId(String id1);

  @DefaultMessage("Remove object {0} from list?")
  String confirmRemoveObjectFromList(String name);

  @DefaultMessage("Paragraph with number {0} not found")
  String importUnknownParagraphNumber(int num);

  @DefaultMessage("Remove this connection to {0}")
  String importRemoveConnectionTo(int number);

  @DefaultMessage("{0} paragraphs were imported")
  String importParagraphWasImported(int size);

  @DefaultMessage("Duplicate paragraph numbers {0}")
  String importDuplicateParagraphNumbers(int number);

  @DefaultMessage("Parsing paragraphs terminated")
  String importParsingTerminated();

  @DefaultMessage("Found connections: {0}")
  String importTotalConnectionsFound(int size);

  @DefaultMessage("Removed duplicate connection from {0} to {1}")
  String importDuplicateConnectionRemoved(int number, int number2);

  @DefaultMessage("Two way connection from {0} to {1}")
  String importTwoWayConnectionFound(int number, int number2);

  @DefaultMessage("Detected star paragraph {0}")
  String importDetectedStartParagraph(int number);

  @DefaultMessage("Paragraph {0} has link to unknown paragraph {1}")
  String importLinkToUnknowParagraph(int number, String er);

  @DefaultMessage("Paragraph {0} has incorrect link {1}")
  String importIncorrectLink(int number, String er);

  @DefaultMessage("Paragraph {0} has empty link {1}")
  String importEmptyLink(int number, String er);

  @DefaultMessage("Paragraph {0} does not have close tag")
  String importParagraphDoesNotHaveCloseTag(int number);

  @DefaultMessage("Paragraph {0} has connections.\nRemove connections before")
  String modelParagraphStillHasConnections(String name);

  @DefaultMessage("Object {0} is used in connection(s)\nFrom: {1}\nTo {2}")
  String modelObjectCannotBeRemoved(String name, String name2, String name3);

  @DefaultMessage("Object {0} is used in paragraph(s)\n{1}")
  String modelObjectCannotBeRemoved2(String name, String name2);

  @DefaultMessage("Secret key is not set for object {0}")
  String modelSecretKeyIsNotSet(String name);

  @DefaultMessage("Condition connection <{0}> does not have description after '<>'")
  String modelConnectionNoCoditionDescriptor(String id);

  @DefaultMessage("Player does not have object {0} for passing requested connection to paragraph {1}")
  String modelPlayerDoesNotHaveRequieredObject(String name, String name2);

  @DefaultMessage("Non-conditional connection <{0}> cannot have '<>' before")
  String modelConnectionNonConditionalHasCondtionDescriptor(String id);

  @DefaultMessage("Unknown connection ID={0}")
  String modelUnknownConnectionId(String id);

  @DefaultMessage("Missed connection from {0} to {1}")
  String modelConnectionMissedInText(String name, String name2);

  @DefaultMessage("Ups. Error in algorighm. Code={0}")
  String validatorAlgorithmError(int code);

  @DefaultMessage("Iteration failed on paragraph {0}/{1}")
  String validatorIterationFailed(int fail, int total);

  @DefaultMessage("{0} - Number is not set")
  String validationParagraphNumberNotSet(String name);

  @DefaultMessage("Paragraph {0} number is out of range (1..{1})")
  String validationParagraphNumberOutOfRange(String name, int max);
  
  @DefaultMessage("Paragraph {0} number {1} is duplicated with paragraph {2}")
  String validationParagraphNameDuplicated(String name, int number, String name2);

  @DefaultMessage("Object {0} does not have Secret key")
  String validationObjectDoesNotHaveSecretKey(String name);

  @DefaultMessage("Object {0} has wrong secret key {1} for connection from paragraph {3}.{2} to paragraph {5}.{4}")
  String validationObjectHasWrongSecretKey(String name, int key, int number,String name2, int number2, String name3);

  @DefaultMessage("Paragraph {0} has object {1} which could be already got")
  String validationObjectCanBeFoundSomeTimes(String name, String name2);

  @DefaultMessage("Two way connection from {0} to {1} has conditional object {2}")
  String validationTwoWayConnectionHasCondition(String name, String name2,String name3);

  @DefaultMessage("Connection from {0} to {1} is duplicated")
  String validationConnectionIsDuplicated(String name, String name2);

  @DefaultMessage("Paragraph {0} is not success or faild but does not have outcome connections")
  String validationParagraphNoOutputConnections(String name);

  @DefaultMessage("Success paragraph {0} cannot be reached")
  String validationParagraphSuccessUnavailable(String name);

  @DefaultMessage("Object {0} cannot be obtained")
  String validationObjectCannotBeGot(String name);

  @DefaultMessage("Object {0} is used nowhere")
  String validationObjectIsNotUsed(String name);

  @DefaultMessage("Success or Fail paragraph {0} cannot has outcome connections")
  String validationParagraphHasOutputConnections(String name);

  @DefaultMessage("Success or Fail paragraph {0} cannot be also a Start paragraph")
  String validationParagraphCannotBeStart(String name);

  @DefaultMessage("Fail or Success paragraph {0} has object(s)")
  String validationParagraphCannotHaveObject(String name);

  @DefaultMessage("Paragraph {0} cannot be riached")
  String validationParagraphCannotBeRiached(String name);

  @DefaultMessage("Connection from {0} to {1} cannot be used")
  String validationConnectionCannotBeUsed(String name, String name2);

  @DefaultMessage("Chain of conditional connections is detected from {0} to {1}")
  String validatorConditionChainDetected(String name, String name2);

  @DefaultMessage("Object {1} is found and lost in the same paragraph {0}")
  String validatorObjectIsLostAndFoundInTheSamePlace(String name, String name2);

  @DefaultMessage("Text of paragraph {0} has error: {1}")
  String validatorParagraphTextError(String name, String string);
  
  @DefaultMessage("You player is too old to play this book</br>\nPlayer version {0}.{1}</br>\nBook version {2}.{3}</br>\nVisit <a href=\"http://www.iambookmaster.com\" target=\"_blank\">www.iambookmaster.com</a><br>\nto get the lates version of Player")
  String playerTooOld(int hiVersion, int loVersion, int versionHi, int versionLo);

  @DefaultMessage("This book was created by \'I am Book Master\' editor, version {0}.{1}")
  String playerBookWasScretedBy(int versionHi, int versionLo);

  @DefaultMessage("Player version {0}.{1}")
  String playerVersion(int hiVersion, int loVersion);

  @DefaultMessage("New version of this game is available. Visit <a href=\"http://www.iambookmaster.com?game={0}\" target=\"_blank\">www.iambookmaster.com</a> to get it")
  String playerNewGameVersionAvailable(String id);
  
  @DefaultMessage("Object {0} ")
  String serverErrorItem(String name);
  
  @DefaultMessage("{0} paragraph {1} ")
  String serverErrorParagraphNoNumber(String string, String name);
  
  @DefaultMessage("{0} paragraph <{1}>.{2} ")
  String serverErrorParagraphWithNumber(String string, int number, String name);

  @DefaultMessage("{0} connection from {1} to {2} ")
  String serverErrorParagraphConnection(String string, String paragraphDescription, String paragraphDescription2);

  @DefaultMessage("Algorithm error :{0}")
  String serverBookGenerationError(int code);

  @DefaultMessage("All attemts to generate numbers failed\n{0}")
  String serverBookGenerationFailed(String string);

  @DefaultMessage("{0}/{1}\n")
  String serverBookGenerationIterationFailed(int fail, int total);

  @DefaultMessage(" has too big number, max={0}")
  String serverBookGenerationTooBig(int max);

  @DefaultMessage(" has incorrect secret key {0} for ")
  String serverBookGenerationWrongSecredKey(int key);

  @DefaultMessage("<a href=\"http://www.iambookmaster.com/books.php?{0}={1}\" target=\"_blank\">View others books of the same Author(s)</a>")
  String feedbackViewOtherBooks(String param, String value);

  @DefaultMessage("<a href=\"{0}?subject={1}&body={2}\" target=\"_blank\">Submit feedback by e-mail</a>")
  String feedbackSubmitMail(String url,String subject,String body);
  
  @DefaultMessage("<a href=\"{0}\" target=\"_blank\">View others game-books</a>")
  String feedbackViewOthersAuthors(String allBooksURL);

  @DefaultMessage("Your book have too many objects ({0} objects and just {1} paragraphs). Disable 'fine secret keys' or add more paragraphs")
  String serverBookGenerationTooManyObjects(int size, int size2);

  @DefaultMessage("Two income condtional connections to paragraph {0} with the same object {1}")
  String twoInputConnectionsWithTheSameObject(String name, String name2);

  @DefaultMessage("Two outcome condtional connections from paragraph {0} with the same object {1}")
  String twoOutputConnectionsWithTheSameObject(String name, String name2);

  @DefaultMessage("{0} is used in {1}")
  String cannotRemoveParameter(String paramName, String dependent);

  @DefaultMessage("if modificator \"{0}\" is present you can go")
  String paragraphTemplateModificatorPresent(String name);
  
  @DefaultMessage("if modificator \"{0}\" is present you can go it.")
  String paragraphTemplateModificatorPresentAbs(String name);
  
  @DefaultMessage("if modificator \"{0}\" is present you must go")
  String paragraphTemplateModificatorPresentMust(String name);
  
  @DefaultMessage("You have to go by modificator \"{0}\" if it is present")
  String paragraphTemplateModificatorPresentMustAbs(String name);
  
  @DefaultMessage("if modificator \"{0}\" is not present you can go")
  String paragraphTemplateModificatorPresentMustNot(String name);

  @DefaultMessage("if no modificator \"{0}\" you can go")
  String paragraphTemplateModificatorNotPresent(String name);
  
  @DefaultMessage("if no modificator \"{0}\" you must go")
  String paragraphTemplateModificatorNotPresentMust(String name);

  @DefaultMessage("if modificator \"{0}\" you can go")
  String paragraphTemplateModificatorNotPresentMustNot(String name);

  @DefaultMessage("if {0} less than {1} you can go")
  String paragraphTemplateParameterLess(String name, String value);

  @DefaultMessage("if {0} less than {1} you must go")
  String paragraphTemplateParameterLessMust(String name, String value);

  @DefaultMessage("if {0} less than {1} you must not go")
  String paragraphTemplateParameterLessMustNot(String name, String value);

  @DefaultMessage("if {0} more than {1} you can go")
  String paragraphTemplateParameterMore(String name, String value);
  
  @DefaultMessage("if {0} more than {1} you must go")
  String paragraphTemplateParameterMoreMust(String name, String value);
  
  @DefaultMessage("if {0} more than {1} you must not go")
  String paragraphTemplateParameterMoreMustNot(String name, String value);

  @DefaultMessage("\nWrite Modificator \"{0}=<m{1}>\" to your Player list") 
  String paragraphTemplateSetAbsoluteModificator(String name,String id);

  @DefaultMessage("\nWrite modificator \"{0}\" to your Player list") 
  String paragraphTemplateSetModificator(String name);
  
  @DefaultMessage("\nWrite modificator \"<m{0}>\" to your Player list") 
  String paragraphTemplateSetModificatorID(String id);
  
  @DefaultMessage("\nRemove modificator \"{0}\" from your Player list") 
  String paragraphTemplateClearModificator(String name);
  
  @DefaultMessage("\nRemove modificator \"<m{0}>\" from your Player list") 
  String paragraphTemplateClearModificatorID(String id);
  
  @DefaultMessage("{0}={1}") 
  String calculationSetParameter(String name, String value);

  @DefaultMessage("add {1} to {0}") 
  String calculationIncParameter(String name, String value);

  @DefaultMessage("sub {1} from {0}") 
  String calculationDecParameter(String name, String value);

  @DefaultMessage("add {1} to {0}") 
  String calculationAddParameter(String name, String value);

  @DefaultMessage("sub {1} from {0}") 
  String calculationSubParameter(String name, String value);
  
  @DefaultMessage("Restore {0} to the maximum") 
  String calculationRestoreToMax(String name);
  
  @DefaultMessage("Remove NPC {0} from list of enemies?")
  String confirmRemoveNPCFromList(String name);

  @DefaultMessage("The Picture is used in Paragraph {0}")
  String pictureIsUsedInParagraph(String name);

  @DefaultMessage("The Sound is used in Paragraph {0}")
  String soundIsUsedInParagraph(String name);

  @DefaultMessage("It is used in Paragraph {0}")
  String parameterIsUsedInParagraph(String name);

  @DefaultMessage("It is used in Paragraph connection from {0} to {1}")
  String parameterIsUsedInParagraphConnection(String name, String name2);

  @DefaultMessage("{0}:{1}")
  String playerParameterValue(String name, int value);

  @DefaultMessage("{0}:{1}/{2}")
  String playerParameterValueLimit(String name, int value, int limit);

  @DefaultMessage("Vital parameter is not set for battle {0}")
  String playerBattleNoVital(String name);

  @DefaultMessage("Vital parameter {1} for battle {0} is not set defined for Hero")
  String playerBattleNotDefinedHeroVital(String name, Parameter vital);

  @DefaultMessage("Vital parameter {0} for NPC {1} is not set defined for Hero")
  String playerBattleNotDefinedNPCVital(String paramName, String npc);

  @DefaultMessage("Set modificator {0}")
  String paragraphEditModificatorSet(String name);

  @DefaultMessage("Clear modificator {0}")
  String paragraphEditModificatorClear(String name);
  
  @DefaultMessage("You attacked {0}")
  String battleHeroAttack(String name);

  @DefaultMessage("{0} attacked You")
  String battleHeroDefense(String name);
  
  @DefaultMessage("Attack: <b>{0}</b>, Contrattack <b>{1}</b>, <i>{3}</i> Damage <b>{2}</b>.")
  String battleEffortAA(int attack, int defense, int damage, String whom);

  @DefaultMessage("Attack: <b>{0}</b>, Defense <b>{1}</b>, Damage <b>{2}</b>.")
  String battleEffortAD(int attack, int defense, int damage);

  @DefaultMessage("Attack: <b>{0}</b>, Defense <b>{1}</b>, no effect.")
  String battleNoEffortAD(int attack, int defense);

  @DefaultMessage("Attack: <b>{0}</b>, Contrattack <b>{1}</b>, no effect.")
  String battleNoEffortAA(int attack, int defense);

  @DefaultMessage("Alchemy \"{0}\" is available here")
  String paragraphEditAlchemyAvailable(String name);

  @DefaultMessage("Alchemy \"{0}\" is disabled here")
  String paragraphEditAlchemyDisabled(String name);

  @DefaultMessage("\"{0}\" cannot be used here")
  String paragraphTemplateAlchemyDisabled(String name);

  @DefaultMessage("\"{0}\": {1} {2} for {3} {4}")
  String paragraphTemplateAlchemyEnabled(String name,String fromValue,String from,String toValue,String to);
  
  @DefaultMessage("\"{0}\": from <af{1}> {2} to <at{1}> {3}")
  String paragraphTemplateAlchemyEnabledInText(String name, String id,String from,String to);

  @DefaultMessage("Paragraph has an explicitly set available anywhere Alchemy {0}")
  String modelParagraphHasNonDemandAlchemy(String name);
  
  @DefaultMessage("Paragraph has an explicitly disabled on-demand Alchemy {0}")
  String modelParagraphHasDisabledOnDemandAlchemy(String name);
  
  @DefaultMessage("Paragraph doesn not have a Battle, but has an explicitly battle Alchemy {0}")
  String modelParagraphPeasfulHasBattleAlchemy(String name);

  @DefaultMessage("Alchemy {0} is not battle-only, but it is marked as a weapon") 
  String modelParagraphHasNonBattleWeaponAlchemy(String name);

  @DefaultMessage("Alchemy {0} has some FROM reference in the Paragraph") 
  String modelParagraphMultyReferenceAlchemyFrom(String name);

  @DefaultMessage("Alchemy {0} has some TO reference in the Paragraph") 
  String modelParagraphMultyReferenceToAlchemyTo(String name);

  @DefaultMessage("\nBattle {0} <b>") 
  String paragraphTemplateBattle(String name);

  @DefaultMessage("Useless absolute Modificator {0}") 
  String modelParagraphHasUnusedAbsModificator(String name);

  @DefaultMessage("Link to unknown Modificator {0} in the text (no Modificator with this id in the paragraph)") 
  String modelParagraphHasUnknownModificator(String idM);

  @DefaultMessage("Connection from {0} to {1} does not have a Modificator, but it must have due to condition type") 
  String modelParagraphHasConnectionWithoutModificator(String name, String name2);
  
  @DefaultMessage("\nЕсли твой параметр {0} станет в битве меньше {1}, то ") 
  String paragraphTemplateVitalLess(String name, String value);
  
  @DefaultMessage("\nЕсли параметр {0} твоего Противника станет в битве меньше {1}, то ") 
  String paragraphTemplateNPCVitalLess(String name, String value);

  @DefaultMessage("Modificator {0} is set nowhere") 
  String modificatorIsSetNowhere(String name);
  
  @DefaultMessage("Connection from {0} to {1} suppouses a Modificator, but it is not set") 
  String modificatorNotSetInConnection(String name, String name2);

  @DefaultMessage("Success/Fail paragraph {0} cannot change Modificators/Parameters") 
  String modificatorsInFialOrSuccessParagraph(String name);
  
  @DefaultMessage("Connection from {0} to {1} suppouses a Parameter, but it is not set") 
  String parameterNotSetInConnection(String name, String name2);

  @DefaultMessage("Modificator {0} is used nowhere") 
  String unusedModificator(String name);

  @DefaultMessage("Parameter {0} is used nowhere") 
  String unusedParameter(String name);

  @DefaultMessage("NPC {0} is used nowhere") 
  String NPCIsUsedNowhere(String name);

  @DefaultMessage("Alchemy {0} is used nowhere") 
  String alchemyIsUsedNowhere(String name);

  @DefaultMessage("Battle {0} is used nowhere") 
  String battleIsUsedNowhere(String name);

  @DefaultMessage("{0} is {1}. Remember initial value of the parameter, it will be the maximum") 
  String rulesTemplateParameterHasLimit(String name, String description);

  @DefaultMessage("{0} is {1}. Value of this parameter cannot exceed {2} ") 
  String rulesTemplateParameterLimitAfter(String name, String description,String limit);

  @DefaultMessage("{0} is {1}") 
  String rulesTemplateParameter(String name, String description);

  @DefaultMessage("{0}\nAttack ={1}") 
  String rulesTemplateBattleOneTurn(String name, String attack);

  @DefaultMessage("{0}\nattack={1}, defense={2}") 
  String rulesTemplateBattleAttackDefense(String name, String attack,	String defense);

  @DefaultMessage("{0}\nattack={1}") 
  String rulesTemplateBattleAttack(String name, String attack);

  @DefaultMessage("If you got {0} you kill your opponent by a fatal strike") 
  String rulesTemplateBattleFatalKill(int n);
  
  @DefaultMessage("If you got {0} you reduse {1}") 
  String rulesTemplateBattleFatalDamage(int n, String vital); 	
  
  @DefaultMessage("\nEnd battle, {0}")
  String rulesTemplateBattleEnd(String vital);

  @DefaultMessage("\nReduce {0} of loozer by difference between Attacks")
  String rulesTemplateBattleDifferenceIsDamage(String name);

  @DefaultMessage("\nReduce {0} of loozer by difference between Attack and Defense")
  String rulesTemplateBattleDifferenceIsDamageAD(String name);
  
  @DefaultMessage("\nReduce {0} of loozer by {1}")
  String rulesTemplateBattleDamage(String name, String damage);
  
  @DefaultMessage("\nReduce {0} of loozer by {1} (user parameters of the winner for calculation")
  String rulesTemplateBattleDamagePar(String name, String string);

  @DefaultMessage(" {1} items can reduce parameter {0} of your opponent to {2}")
  String rulesTemplateAlchemyWeapon(String name, String from, String to);

  @DefaultMessage(" {1} items can increase your parameter {0} to {2} in battle")
  String rulesTemplateAlchemyBattle(String name, String from, String to);

  @DefaultMessage(" {1} items can increase your parameter {0} to {2}, but you cannod do it in a battle")
  String rulesTemplateAlchemyPease(String name, String from, String to);

  @DefaultMessage(" {1} items can increase your parameter {0} to {2}, you can do it any time")
  String rulesTemplateAlchemyBoth(String name, String from, String to);

  @DefaultMessage("<{0}>")
  String urqButtonAction(int id);

  @DefaultMessage("Your Hero died ({0} is Zero or less)")
  String urqHeroDiedByVitalParameter(String name);
  
  @DefaultMessage("Cannot load file\n{0}\n{1}")
  String fileErrorLoading(String name,String res);
  
  @DefaultMessage("Cannot write to file\n{0}\n{1}")
  String fileErrorWriting(String filename, String res);
  
  @DefaultMessage("Cannot load file. Error message:\n{0}")
  String fileErrorLoadingNoName(String res);
  
  @DefaultMessage("Cannot save file. Error message:\n{0}")
  String fileErrorWritingNoName(String res);
  
  @DefaultMessage("Attack {0}")
  String urqButtonAttack(int counter);
  
//  @DefaultMessage("Your Attack {0}")
//  String qspButtonAttack(int counter);
  
  @DefaultMessage("Damaged {1}{0}{2}")
  String urqBattleRoundDamage(String var,String before,String after);

  @DefaultMessage("Fatal strike {1}{0}{2}")
  String urqBattleFatalStrike(String damage,String before,String after);

  @DefaultMessage("Incorrect or Unknown paragraph number {0}")
  String paragraphByNumberWrong(String numStr);

  @DefaultMessage("Entered number {0} address the same paragraph")
  String paragraphConnectionTheSameNumber(String numStr);

  @DefaultMessage("Connection between:\n{0}:{1}\nand\n{2}:{3}\ndoes not exist. Create?")
  String paragraphConnectionCreateConfirm(int numFrom, String name, int numTo,String name2);
  
  @DefaultMessage("{0}:{1}")
  String validationStatus(int paragraphs, int connections);

  @DefaultMessage("Paragraph {0} has must-go and normal outcome connections. It is not correct for the current settings")
  String mustGoAndNormaConnectionsInParagraph(String name);

  @DefaultMessage("Unexpected end of paragraphs after {0}")
  String modelBulkCorrectionLoadUnexpectedEnd(int i);

  @DefaultMessage("Unexpected paragraphs {0} after {1}")
  String modelBulkCorrectionLoadWrongNumber(int next, int i);

  @DefaultMessage("Cannot load Image {0}")
  String paragraphCannotLoadImage(String url);

  @DefaultMessage("Attack <<{0}>>")
  String qspAttackEnemty(String id);

  @DefaultMessage("{0}")
  String iphoneChoice(int counter);

  @DefaultMessage("{0}/{1} paragraphs are available")
  String demoInfoTextDefault(int paragrahps, int max);

  @DefaultMessage("Donate {0}")
  String iphoneDonate(String price);
  
  @DefaultMessage("{0}, you can try {1}")
  String iphoneThankyouError(String message, String message2);

  @DefaultMessage("If you did not finish battle in {0} rounds you must go")
  String paragraphTemplateBattleRoundMoreMust(int round);
	
  @DefaultMessage("During first {0} you can go")
  String paragraphTemplateBattleRoundMoreMustNot(int round);
	
  @DefaultMessage("After {0} rounds you can go ")
  String paragraphTemplateBattleRoundMore(int round);

  @DefaultMessage("Join to the battle after {0} rounds\n")
  String paragraphTemplateJoinAfterRound(int round);

  @DefaultMessage("{0} cooperates with you")
  String paragraphTemplateBattleFriend(String name);

  @DefaultMessage("{0} attacks {1}")
  String battleVictimAtack(String victim, String enemy);

  @DefaultMessage("{1} attacks {0}")
  String battleVictimDefence(String victim, String enemy);

  @DefaultMessage("{0}")
  String playerParameterOneValue(String name);

  @DefaultMessage("Connection from {0} to {1} - empty connection FROM name") 
  String validateConnectionEmptyNameFrom(String name, String name2);
  
  @DefaultMessage("Connection from {0} to {1} - empty connection FROM name") 
  String validateConnectionEmptyNameTO(String name, String name2);

  @DefaultMessage("{0}x{1}")
  String imageSize(int widht, int height);

  @DefaultMessage("Zoom {0}%")
  String playerIphoneScale(int scale);
  
//-------------------------------------------------------  
  


  
}
