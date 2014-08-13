package com.iambookmaster.client.locale;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;


@DefaultLocale("ru")
public interface AppConstants extends com.google.gwt.i18n.client.Constants {
  
  @DefaultStringValue("(c) 2008-2011 <a href=\"http://www.iambookmaster.com\" target=\"_blank\">www.iambookmaster.com</a>")
  String copyright();

  @DefaultStringValue("Paragraphs")
  String ParagraphsMapEditorTitle();
  
  @DefaultStringValue("Plot")
  String PlotEditor();

  @DefaultStringValue("Server")
  String serverPanelTitle();
  
  @DefaultStringValue("Editor")
  String ParagraphEditorTitle();
  
  @DefaultStringValue("The following error(s) detected:\n\n")
  String TheFollowinErrorsWereDetect();
  
  @DefaultStringValue("Player")
  String PlayerTitle();
  
  @DefaultStringValue("Validator")
  String ValidatorTitle();
  
  @DefaultStringValue("Collecting ALL possible success stories can take A LOT of time and resourses.\n Show ALL possible success stories?")
  String WarininCollectionAllStories();

  @DefaultStringValue("Story reader")
  String StoryReaderTitle();
  
  @DefaultStringValue("Close this tab?")
  String CloseTabConfirm();
  
  @DefaultStringValue("Close")
  String closeButton();

  @DefaultStringValue("You lost all unsaved changes")
  String lostAllUnsavedAlert();
  
  @DefaultStringValue("Copy and Save text")
  String copyAndSaveModelTitle();
  
  @DefaultStringValue("Copy text to JavaScript file")
  String copyAndSavePlayerTitle();
  
  @DefaultStringValue("Load book, insert saved text")
  String loadSavedModelTitle();
  
  @DefaultStringValue("Validate map")
  String menuValidateMap();
  
  @DefaultStringValue("Validate text")
  String menuValidateText();
  
  @DefaultStringValue("Validate all")
  String menuValidateAll();
  
  @DefaultStringValue("Read whole")
  String menuWholeRead();
  
  @DefaultStringValue("External correction")
  String menuExportForExternalCorrection();
  
  @DefaultStringValue("All success")
  String menuAllSuccessStories();

  @DefaultStringValue("Longest and Shortes success")
  String menuLongAndShortSuccess();
  
  @DefaultStringValue("Import from Text")
  String menuImportFromText();
  
  @DefaultStringValue("Join other project")
  String menuJoinOtherModel();
  
  @DefaultStringValue("You have to select paragraph to join")
  String joinHaveToSelectParagraph();
  
  @DefaultStringValue("Insert project to join")
  String joinModelTitle();

  @DefaultStringValue("Export Game for Player")
  String menuExportForPlayer();

  @DefaultStringValue("Re-export Text book")
  String menuReExportText();

  @DefaultStringValue("Copy and save it")
  String exportTextTitle();

  @DefaultStringValue("Connect")
  String menuServerLogin();

  @DefaultStringValue("Save book")
  String menuServerSaveBook();

  @DefaultStringValue("Publish book")
  String menuServerPublishBook();

  @DefaultStringValue("All paragraph numbers will be regenerated")
  String confirmRenumeration();

  @DefaultStringValue("Re-Publish book")
  String menuServerRePublish();

  @DefaultStringValue("Play")
  String menuPlayer();

  @DefaultStringValue("Server")
  String menuServer();

  @DefaultStringValue("Save book")
  String menuSave();

  @DefaultStringValue("Load book")
  String menuLoad();

  @DefaultStringValue("Import book")
  String menuImport();

  @DefaultStringValue("Validation")
  String menuValidation();

  @DefaultStringValue("Paragraph")
  String menuNewParagraph();

  @DefaultStringValue("Object")
  String menuNewObject();

  @DefaultStringValue("Sound")
  String menuNewSound();

  @DefaultStringValue("Image")
  String menuNewImage();

  @DefaultStringValue("Add new")
  String menuAddNew();

  @DefaultStringValue("Local")
  String menuLocal();
	
  @DefaultStringValue("Project")
  String maneProject();

  @DefaultStringValue("Paragraphs")
  String quickParagraphs();

  @DefaultStringValue("List of paragraphs")
  String quickParagraphsTitle();
  
  @DefaultStringValue("Objects")
  String quickObjects();

  @DefaultStringValue("List of objects")
  String quickObjectsTitle();

	@DefaultStringValue("Images")
	String quickImages();

	@DefaultStringValue("List of images")
	String quickImagesTitle();

	@DefaultStringValue("Sounds")
	String quickSounds();

	@DefaultStringValue("List of sounds")
	String quickSoundsTitle();

	@DefaultStringValue("Settings")
	String quickSettings();

	@DefaultStringValue("Settings")
	String quickSettingsTitle();

	@DefaultStringValue("Exchanging data with the server")
	String serverExchangeProcess();

	@DefaultStringValue("Done")
	String serverExchangeDoneTitle();

	@DefaultStringValue("Uploading...")
	String serverExchangeUploading();

	@DefaultStringValue("Successful")
	String serverExchangeSuccessful();

	@DefaultStringValue("Uploading")
	String serverExchangeUploadingTitle();
	
	@DefaultStringValue("You have to")
	String jspMainHaveTo();
	
	@DefaultStringValue("Sign-in")
	String jspMainSignIn();
	
	@DefaultStringValue("to work with \"I am book master editor\"")
	String jspMainToWorkWithEditor();
	
	@DefaultStringValue("You can also login in external browser window using the link below")
	String jspExternalLogin();
	
	@DefaultStringValue("External login")
	String jspExternalLoginLink();
	
	@DefaultStringValue("Administrative console")
	String jspMainAdminLink();
	
	@DefaultStringValue("Log Out")
	String jspMainLogOut();
	
	@DefaultStringValue("Your account is locked. Contact administrator for details")
	String jspMainAccountLocked();
	
	@DefaultStringValue("Editor 'I am Book Master' is not detected. It is recommended to use this site only from the editor")
	String jspMainEditorNotDetected();
	
	@DefaultStringValue("I am Book Master")
	String jspMainTitle();
	
	@DefaultStringValue("You do not have game-books")
	String jspMainNoGameBooks();
	
	@DefaultStringValue("Your books")
	String jspMainYourBooks();
	
	@DefaultStringValue("This book is locked")
	String jspMainBookIsLocked();
	
	@DefaultStringValue("Published book")
	String jspMainBookIsPublished();
	
	@DefaultStringValue("Not published book")
	String jspMainBookIsNotPublished();
	
	@DefaultStringValue("Load")
	String jspLoadLinkEditor();

	@DefaultStringValue("Project")
	String jspLoadLinkProject();
	
	@DefaultStringValue("Text")
	String jspLoadLinkText();
	
	@DefaultStringValue("HTML")
	String jspLoadLinkHTML();
	
	@DefaultStringValue("URQ")
	String jspLoadLinkURQ();
	
	@DefaultStringValue("URQ Light")
	String jspLoadLinkURQShort();
	
	@DefaultStringValue("Publish book")
	String jspMainRePublich();
	
	@DefaultStringValue("Publish")
	String jspMainPublichButton();
	
	@DefaultStringValue("(Re)generate numbers")
	String jspMainRegenerateNumbers();
	
	@DefaultStringValue("If set - all paragraph and other number will be regenerated")
	String jspMainRegenerateNumbersTitle();

	@DefaultStringValue("New Paragraph")
	String modelNewParagraphName();

	@DefaultStringValue("New image")
	String modelNewImageName();
	
	@DefaultStringValue("New sound")
	String modelNewSoundName();
	
	@DefaultStringValue("Paragraph has wrong structure of description, not end '>'")
	String modelWrongParagraphStructure();

	@DefaultStringValue("This item cannot be used here")
	String getDefaumtMissuseMessage();

	@DefaultStringValue("Default")
	String colorDefault();
	
	@DefaultStringValue("Load")
	String buttonLoad();

	@DefaultStringValue("Close")
	String buttonClose();
	
	@DefaultStringValue("Add object to list")
	String titleAddObjectToList();

	@DefaultStringValue("Remove object from list")
	String titleRemoveObjectFromList();
	
	@DefaultStringValue("Proposal")
	String statusProposal();

	@DefaultStringValue("Draft")
	String statusDraft();

	@DefaultStringValue("Final")
	String statusFinal();

	@DefaultStringValue("Import game-book from text file")
	String importPanelTitle();

	@DefaultStringValue("Parse original text again?")
	String importParseOriginalAgain();

	@DefaultStringValue("Parse paragraphs again?")
	String importParseParagraphAgain();

	@DefaultStringValue("Original")
	String importTabOriginal();

	@DefaultStringValue("Options")
	String importTabOptions();

	@DefaultStringValue("Paragraphs")
	String importTabParagraphs();

	@DefaultStringValue("Connections")
	String importTabConnections();

	@DefaultStringValue("Prev")
	String buttonPrev();

	@DefaultStringValue("Next")
	String buttonNext();

	@DefaultStringValue("Finish")
	String buttonFinish();

	@DefaultStringValue("Insert text here")
	String importTextTitle();

	@DefaultStringValue("Paragraph number in separate line")
	String importNumberInSeparateLine();

	@DefaultStringValue("Paragraph reference")
	String importParagraphReference();

	@DefaultStringValue("This paragraph is the first")
	String importParagraphIsFirst();

	@DefaultStringValue("This paragraph is the last")
	String importParagraphIsLast();

	@DefaultStringValue("Merge with prev paragraph")
	String importMergePrevParagraph();

	@DefaultStringValue("Merge with next paragraph")
	String importMergeNextParagraph();

	@DefaultStringValue("Split paragraph")
	String importSplitParagraph();

	@DefaultStringValue("Click to edit")
	String importClickToEdit();

	@DefaultStringValue("Type number of paragraph to add connection")
	String importTypeToAddConnections();

	@DefaultStringValue("Add connection to paragraph")
	String importAddConnection();

	@DefaultStringValue("Make paragraph as Normal")
	String importMakeNormal();

	@DefaultStringValue("Make paragraph as Star")
	String importMakeStart();

	@DefaultStringValue("Make paragraph as Success")
	String importMakeSuccess();

	@DefaultStringValue("Make paragraph as Fail")
	String importMakeFail();

	@DefaultStringValue("Select other paragraph as Start")
	String importSelectOtherAsStart();

	@DefaultStringValue("Cannot add link to the same paragraph")
	String importCannotLinkToItself();

	@DefaultStringValue("This connection is already exists")
	String importConnectionExists();

	@DefaultStringValue("Back connection is already exist\nCreate two-way connection?")
	String importCretateBiConnection();

	@DefaultStringValue("Remove this connection")
	String importRemoveConnection();

	@DefaultStringValue("Start importing paragraphs")
	String importStartImporting();

	@DefaultStringValue("End importing paragraphs")
	String importEndImportingParagraphs();

	@DefaultStringValue("Start parsing paragraphs")
	String importStartParsingParagraphs();

	@DefaultStringValue("NO NAME")
	String importNoNameParagraph();

	@DefaultStringValue("End parsing paragraphs")
	String importEndParsingParagraphs();

	@DefaultStringValue("No paragraphs were detected")
	String importNoParagraphsParsed();

	@DefaultStringValue("Cannot detect Start paragraph")
	String importCannotDetectStart();

	@DefaultStringValue("Cannot detect star paragraph properly, some paragraphs do not have income connections")
	String importCannotDetectStartProperly();

	@DefaultStringValue("Cannot detect star paragraph, all paragraphs have income connections")
	String importCannotDetectStartProperlyAllHave();

	@DefaultStringValue("This paragraph is marked as Fail, make it Normal first")
	String modelParagraphIsFailMakeNormal();

	@DefaultStringValue("This paragraph is marked as Start, is cannot be Fail")
	String modelParagraphIsStartCannotBeFail();

	@DefaultStringValue("This paragraph is marked as Start, is cannot be Success")
	String modelParagraphIsStartCannotBeSuccess();

	@DefaultStringValue("Start paragraph cannot be deleted")
	String modelParagraphIsStartCannotBeDeleted();

	@DefaultStringValue("Paragraph has two '<>' in sequence")
	String modelParagraphHave2DelimSiquence();

	@DefaultStringValue("Paragraph has reference to Item in text, but does not have set Item")
	String modelParagraphUnknownReferenceItemText();

	@DefaultStringValue("Paragraph has reference to other Item in text, that set in the paragraph")
	String modelParagraphHasReferenceToOtherItemText();

	@DefaultStringValue("Text for using object is empty (<>text MUST be here<...>)")
	String modelTextOfUsingIsEmpty();

	@DefaultStringValue("Differen quantity objects in text and Paragraph properties")
	String modelDiffentQuantitiesItemsInTextAndData();

	@DefaultStringValue("Name")
	String paragraphName();

	@DefaultStringValue("Status")
	String paragraphStatus();

	@DefaultStringValue("Found")
	String paragraphFoundItems();

	@DefaultStringValue("Lost")
	String paragraphLostItems();

	@DefaultStringValue("Select a picture in list of images")
	String paragraphSelectImageInList();

	@DefaultStringValue("Select a sound in list of sounds")
	String paragraphSelectSoundInList();

	@DefaultStringValue("Add selected content")
	String paragraphAddSelectedContenr();

	@DefaultStringValue("Top images")
	String paragraphTopImage();
	
	@DefaultStringValue("Sprites")
	String paragraphSprites();

	@DefaultStringValue("Bottom images")
	String paragraphBottomImage();

	@DefaultStringValue("Background images")
	String paragraphBackgroundImage();

	@DefaultStringValue("Sound effects")
	String paragraphSoundEffects();

	@DefaultStringValue("Background sounds")
	String paragraphBackgroundSound();

	@DefaultStringValue("Click to preview")
	String clickToPreview();

	@DefaultStringValue("Remove")
	String buttonRemove();

	@DefaultStringValue("Click to listen")
	String clickToHear();

	@DefaultStringValue("Remove?")
	String confirmRemove();

	@DefaultStringValue("Remove")
	String titleRemove();

	@DefaultStringValue("Paragraph can be connected to Paragraph only")
	String cannotConnectParagraphToNonParagraph();

	@DefaultStringValue("Click for open context menu")
	String titleContextMenu();

	@DefaultStringValue("Rename")
	String buttonRename();

	@DefaultStringValue("Edit")
	String buttonEdit();

	@DefaultStringValue("Check availability")
	String buttonCheckAvailability();

	@DefaultStringValue("Start paragraph is not set")
	String modelStartParagraphNotSet();

	@DefaultStringValue("Paragraph CANNOT be reached")
	String modelCannotReachParagraph();

	@DefaultStringValue("Paragraph can be reached")
	String modelCanReachParagraph();

	@DefaultStringValue("Make as Normal")
	String buttonMakeParagraphNormal();

	@DefaultStringValue("Make as Start")
	String buttonMakeParagraphStart();

	@DefaultStringValue("Make as Fail")
	String buttonMakeParagraphFail();

	@DefaultStringValue("Make as Success")
	String buttonMakeParagraphSuccess();

	@DefaultStringValue("Bi-direction")
	String modelBiDirection();

	@DefaultStringValue("To here")
	String modelToHere();

	@DefaultStringValue("From here")
	String modelFromHere();

	@DefaultStringValue("Text for correction")
	String modelTextForCorrection();

	@DefaultStringValue("Success paragraph(s) was not set")
	String modelSuccessParagraphsNotSet();

	@DefaultStringValue("Success paragraph(s) cannot be reached")
	String modelSuccessParagraphsCannotBeReached();

	@DefaultStringValue("Mark Final")
	String buttonMarkFinal();

	@DefaultStringValue("Mark Draft")
	String buttonMarkDraft();

	@DefaultStringValue("Mark Proposal")
	String buttonMarkProposal();

	@DefaultStringValue("Book generation")
	String validatorBookGeneration();

	@DefaultStringValue("All attepts to generate the book failed")
	String validatorAllAttempsFailed();

	@DefaultStringValue("Validation in progress")
	String validatorInProgress();

	@DefaultStringValue("Validation Map of paragraphs")
	String validatorMapOfParagraphs();

	@DefaultStringValue("Start paragraph is not defined")
	String validatorStartParagraphIsNotSet();

	@DefaultStringValue("Start paragraph has income connection")
	String validatorStartParagraphHasIncomeConnections();

	@DefaultStringValue("Content of paragraphs validation")
	String validatorContenValidation();

	@DefaultStringValue("Done")
	String validatorDone();

	@DefaultStringValue("Save game. Copy this text to any text file and store it.")
	String playerSavedGame();

	@DefaultStringValue("Restore game. Paste the text from stored file into text area")
	String playerRestoreGame();

	@DefaultStringValue("Restart game?")
	String playerRestartGame();

	@DefaultStringValue("Player list")
	String playerTitlePlayerList();

	@DefaultStringValue("Help")
	String playerTitleHelp();

	@DefaultStringValue("Save game")
	String playerTitleSaveGame();

	@DefaultStringValue("Load game")
	String playerTitleLoadGame();

	@DefaultStringValue("Restart game")
	String playerTitleRestartGame();

	@DefaultStringValue("Edit paragraph")
	String playerTitleEditParagraph();

	@DefaultStringValue("Images is disabled in this version of the game")
	String playerTitleImagesIsDisabledForTheGame();

	@DefaultStringValue("Click to Disable images")
	String playerTitleDisableImages();

	@DefaultStringValue("Click to Enable images")
	String playerTitleEnableImages();

	@DefaultStringValue("Audio is disabled in this version of the game")
	String playerAudioIsDisableInGame();

	@DefaultStringValue("Click to Disable audio")
	String playerTitleAudioDisable();

	@DefaultStringValue("Click to Enable audio")
	String playerTitleAudioEnable();

	@DefaultStringValue("Adeventure List")
	String playerPlayerList();

	@DefaultStringValue("Click to use")
	String playerClickToUse();

	@DefaultStringValue("Different versions of saved and current games")
	String playerDifferentVersionsOfGame();

	@DefaultStringValue("Remove object")
	String quickRemoveObject();

	@DefaultStringValue("Remove this object ?")
	String quickRemoveObjectTitle();

	@DefaultStringValue("Remove this picture ?")
	String quickConfirmRemoveImage();

	@DefaultStringValue("Remove picture")
	String quickRemoveImage();

	@DefaultStringValue("Text book generation")
	String quickTextBookGeneration();

	@DefaultStringValue("Control nearest paragraphs")
	String quickNearestParagraphControl();

	@DefaultStringValue("Distance between nearest paragraphs")
	String quickNearestParagraphDistance();

	@DefaultStringValue("Max. quantity of generation attempts")
	String quickMaxQuantityOfGenerationAttempts();

	@DefaultStringValue("Any number")
	String quickSmartSecretKeyAny();

	@DefaultStringValue("mod 5")
	String quickSmartSecretKey5();

	@DefaultStringValue("mod 10")
	String quickSmartSecretKey10();

	@DefaultStringValue("Secret keys")
	String quickSmartSecretKey();

	@DefaultStringValue("How smart Secret keys should be")
	String quickSmartSecretKeyTitle();

	@DefaultStringValue("Book info")
	String quickBookInfoTitle();

	@DefaultStringValue("Title")
	String quickBookTitle();

	@DefaultStringValue("Authors")
	String quickBookAuthors();

	@DefaultStringValue("Description")
	String quickBookDescription();

	@DefaultStringValue("General settings")
	String quickSettingsGeneralTitle();

	@DefaultStringValue("Create one way connections")
	String quickSettingsOneWayConnections();

	@DefaultStringValue("Show paragraph numbers")
	String quickSettingsShowParagraphNumbers();

	@DefaultStringValue("Game version")
	String quickSettingsGameVersion();

	@DefaultStringValue("Increase version")
	String quickSettingsIncreaseVersion();

	@DefaultStringValue("Greetings")
	String quickGreetingsTitle();

	@DefaultStringValue("Click to add a new greeting")
	String quickGreetingsAddTitle();

	@DefaultStringValue("Click to preview all greetings")
	String quickGreetingsPreviewAll();

	@DefaultStringValue("Remove this greeting?")
	String quickGreetingsRemoveConfirm();

	@DefaultStringValue("Name")
	String quickGreetingsName();

	@DefaultStringValue("URL/Email")
	String quickGreetingsURL();

	@DefaultStringValue("Icon")
	String quickGreetingsIcon();

	@DefaultStringValue("Preview greeting")
	String quickGreetingsPreview();

	@DefaultStringValue("Text of greeting")
	String quickGreetingsText();

	@DefaultStringValue("Player settings")
	String quickPlayerTitle();

	@DefaultStringValue("Hidden using objects")
	String quickPlayerHiddenItems();

	@DefaultStringValue("Show Player list")
	String quickPlayerShowList();

	@DefaultStringValue("Always")
	String quickPlayerShowListAlways();

	@DefaultStringValue("On Demand")
	String quickPlayerShowListPopup();

	@DefaultStringValue("NO")
	String quickPlayerShowListNo();

	@DefaultStringValue("Text color")
	String quickPlayerTextColor();

	@DefaultStringValue("Text background")
	String quickPlayerTextBackground();

	@DefaultStringValue("Player list background")
	String quickPlayerListBackground();

	@DefaultStringValue("Application background")
	String quickPlayerApplicationBackground();

	@DefaultStringValue("Disable audio")
	String quickPlayerDisableAudio();

	@DefaultStringValue("Disable images")
	String quickPlayerDisableImages();

	@DefaultStringValue("Show 'About' window on start")
	String quickPlayerShowAbout();

	@DefaultStringValue("Object quick edit")
	String quickItemTitle();

	@DefaultStringValue("Name")
	String quickItemName();
	
	@DefaultStringValue("Icon")
	String quickItemIcon();
	

	@DefaultStringValue("Secret key")
	String qucikItemSecretKey();

	@DefaultStringValue("Master description")
	String quickItemMasterDescription();

	@DefaultStringValue("Player never sees this text")
	String quickItemPlayerNeverSee();

	@DefaultStringValue("Incorrect use description")
	String quickItemPlayerMissuse();

	@DefaultStringValue("Add text which Player sees when tried incorrect using the item\nYou can add some texts in different lines")
	String qucikItemPlayerMissuseTitle();

	@DefaultStringValue("Connection quick edit")
	String quickConnectionTitle();

	@DefaultStringValue("Remove this connection?")
	String quickConnectionRemoveConfirm();

	@DefaultStringValue("Remove connection")
	String quickConnectionRemoveTitle();

	@DefaultStringValue("Sequence of character, which is used as link in text, like '<c0>'")
	String quickConnectionTextLinkDesciption();

	@DefaultStringValue("ID")
	String quickConnectionID();

	@DefaultStringValue("Type")
	String quickConnectionType();

	@DefaultStringValue("Select Object which Hero has to have to meet the condition")
	String quickConnectionConditionObjectTitle();

	@DefaultStringValue("Object")
	String quickConnectionConditionObject();

	@DefaultStringValue("Condition description")
	String quickConnectionConditionDescription();

	@DefaultStringValue("Bi-directional")
	String quickConnectionBiDirection();

	@DefaultStringValue("One way")
	String quickConnectionOneWay();

	@DefaultStringValue("The following error(s) detected:\n")
	String quickParagraphErrors();

	@DefaultStringValue("Regenerate text of location?")
	String regenerateParagraphTextConfirm();

	@DefaultStringValue("No errors")
	String quickParagraphNoErrors();

	@DefaultStringValue("N")
	String quickParagraphN();

	@DefaultStringValue("Paragraph number")
	String quickParagraphNTitle();

	@DefaultStringValue("Edit paragraph")
	String quickParagraphEditTitle();

	@DefaultStringValue("Regenerate paragraph text")
	String quickParagraphRegenerateTextTitle();

	@DefaultStringValue("Validate paragraph")
	String quickParagraphValidate();

	@DefaultStringValue("Paragraph")
	String quickParagraph();

	@DefaultStringValue("Picture edit")
	String quickImageEditTitle();

	@DefaultStringValue("Name")
	String quickImageEditName();

	@DefaultStringValue("URL")
	String quickImageEditURL();

	@DefaultStringValue("No repeat in background")
	String quickImageEditNoRepeat();

	@DefaultStringValue("Scale image size")
	String quickImageEditScale();

	@DefaultStringValue("New width")
	String quickImageEditWidth();

	@DefaultStringValue("New height")
	String quickImageEditHeight();

	@DefaultStringValue("Preview")
	String buttonPreview();

	@DefaultStringValue("Sound edit")
	String quickSoundEditTitle();

	@DefaultStringValue("Name")
	String quickSoundEditName();

	@DefaultStringValue("Value")
	String quickSoundEditURL();

	@DefaultStringValue("Play")
	String buttonPlay();

	@DefaultStringValue("Stop")
	String buttonStop();

	@DefaultStringValue("Book info")
	String quickSettingsBookInfo();

	@DefaultStringValue("General")
	String quickSettingsGeneral();

	@DefaultStringValue("Player")
	String quickSettingsPlayer();

	@DefaultStringValue("Text book generation")
	String quickSettingsGeneration();

	@DefaultStringValue("Greetings")
	String quickSettingsGreetings();

	@DefaultStringValue("Remove this sound ?")
	String removeSoundConfirm();

	@DefaultStringValue("Remove sound")
	String quickRemoveSoundTitle();

	@DefaultStringValue("Model cannot be loaded. Unknown error")
	String remoteCannotLoadModel();

	@DefaultStringValue("Loading...")
	String playerLoading();

	@DefaultStringValue("Error during loading book:")
	String playerErrorLoading();

	@DefaultStringValue("Many thanks for:")
	String playerGreetingsFor();

	@DefaultStringValue("You are not logged in")
	String serverNotLoggedIn();

	@DefaultStringValue("You account is locked. You can do nothing")
	String serverAccountLocked();

	@DefaultStringValue("Model has to be uploaded before")
	String serverUploadModelBefore();

	@DefaultStringValue("Book ID was not sent")
	String serverNoBookID();

	@DefaultStringValue("Book ID and uploaded Model ID are different")
	String serverBookIDAndModelAreDifferent();

	@DefaultStringValue("This Book is locked. You cannot change it.")
	String serverBookIsLocked();

	@DefaultStringValue("Invalid book version ID")
	String serverInvalidBookVersionID();

	@DefaultStringValue("Requested book version was not found")
	String serverUnknownBookVersion();

	@DefaultStringValue("Invalid book ID")
	String serverInvalidBookID();

	@DefaultStringValue("Book was not found")
	String serverBookNotFound();

	@DefaultStringValue("You are not owner of the book")
	String serverNotOwnerOfBook();

	@DefaultStringValue("Unknown book or book was removed")
	String serverUnknownBook();

	@DefaultStringValue("Error(s) were detected")
	String serverErrorsWereDetected();

	@DefaultStringValue("Model was not sent")
	String serverModelWasNotSent();

	@DefaultStringValue("Model is too big (10,000,000 characters is limit)")
	String serverModelIsTooBig();

	@DefaultStringValue("Fail")
	String serverParagraphFail();

	@DefaultStringValue("Success")
	String serverParagraphSuccess();

	@DefaultStringValue("")
	String serverParagraphNormal();

	@DefaultStringValue("Two way ")
	String serverErrorConnectionTwoWay();

	@DefaultStringValue("One way")
	String serverErrorConnectionOneWay();

	@DefaultStringValue("Start")
	String decoratorStart();

	@DefaultStringValue("Many thanks for:")
	String decoratorStartGreetings();

	@DefaultStringValue(" does not have number")
	String serverBookGenerationNoNumber();

	@DefaultStringValue(" and ")
	String serverBookGenerationParagraphAndParagraph();

	@DefaultStringValue(" have the same number")
	String serverBookGenerationTheSameNumber();

	@DefaultStringValue(" does not have secret key")
	String serverBookGenerationNoSecretKey();

	@DefaultStringValue(" can be got more than 1 time in ")
	String serverValidatorObjectCanBeGotManyTimes();

	@DefaultStringValue(" has a Condition ")
	String serverValidatorConnectionHasCondition();

	@DefaultStringValue("Duplicate connection between ")
	String serverValidatorDuplicateConnection();

	@DefaultStringValue(" and ")
	String serverValidatorDuplicateConnectionAnd();

	@DefaultStringValue("No way from")
	String serverValidatorNoWayFrom();

	@DefaultStringValue(" cannot be reached")
	String serverValidatorParagraphCannotBeReached();

	@DefaultStringValue(" cannot be found")
	String serverValidatorObjectCannotBeFound();

	@DefaultStringValue(" cannot be used")
	String serverValidatorCannotBeUsed();

	@DefaultStringValue(" has outcome way")
	String serverValidatorParagraphHasOutcome();

	@DefaultStringValue("Game starts from ")
	String serverValidatorStartsFrom();

	@DefaultStringValue("Star paragraph has income connection ")
	String serverValidatorStartsHasIncome();

	@DefaultStringValue(" cannot be used")
	String serverValidatorObjectCannotBeUsed();

	@DefaultStringValue(" has object(s)")
	String serverValidatorParagraphHasObjects();

	@DefaultStringValue("Chain of conditional connections is detected: ")
	String serverValidatorChainDetected();

	@DefaultStringValue(" is lost and got in the same paragraph ")
	String serverValidatorObjectLostAndFoundInTheSamePlace();

	@DefaultStringValue("The Worst")
	String FeedBackBad();

	@DefaultStringValue("Bad")
	String FeedBackSad();

	@DefaultStringValue("Neutral")
	String FeedBackNormal();

	@DefaultStringValue("Good")
	String FeedBackGood();

	@DefaultStringValue("The Best")
	String FeedBackBest();

	@DefaultStringValue("Could you provied your feedback about the game-book")
	String feedbackPleaseProvide();

	@DefaultStringValue("Plot")
	String feedbackStory();

	@DefaultStringValue("Complexity")
	String feedbackComplexity();

	@DefaultStringValue("Design")
	String feedbackView();

	@DefaultStringValue("Submit Feedback")
	String feedbackSubmit();

	@DefaultStringValue("Your comments")
	String feedbackAddNote();

	@DefaultStringValue("Provide Feedback")
	String feedbackProvideTitle();

	@DefaultStringValue("Evaluation game-book")
	String feedbackBodyHead();

	@DefaultStringValue("Feedback about game-book")
	String feedbackSubject();

	@DefaultStringValue("en/back.html")
	String remoteBackPage();

	@DefaultStringValue("en/progress.htm")
	String remoteProgressPage();

	@DefaultStringValue("en/success.html")
	String remoteSuccessPage();

	@DefaultStringValue("Processing...")
	String maskProgress();

	@DefaultStringValue("Click to close this panel in case of error")
	String maskClickToClose();

	@DefaultStringValue("en")
	String locale();

	@DefaultStringValue("en/about.html")
	String pageAbout();

	@DefaultStringValue("en/playerHelp.html")
	String pagePlayerHelp();

	@DefaultStringValue("About")
	String menuAbout();

	@DefaultStringValue("Paragraph Map Editor Width")
	String quickSettingsParagraphMapWidth();
	
	@DefaultStringValue("Paragraph Map Editor Height")
	String quickSettingsParagraphMapHeight();

	@DefaultStringValue("Set size of Paragraph Map Editor")
	String quickSettingsParagraphMapTitle();

	@DefaultStringValue("All feedback from players will be forwaded to this e-mail\nThis e-mail will be visible for all players")
	String quickPlayerFeedbackEmailTitle();

	@DefaultStringValue("Send feedback to")
	String quickPlayerFeedbackEmail();

	@DefaultStringValue("Info")
	String infoPanelTitle();

	@DefaultStringValue("Object")
	String infoPanelObject();

	@DefaultStringValue("Refresh")
	String refreshButton();

	@DefaultStringValue("Object is got in the following paragraphs")
	String infoPanelGotObject();

	@DefaultStringValue("Object is lost in the following paragraphs")
	String infoPanelLostObject();

	@DefaultStringValue("Object is used in the following connections")
	String infoPanelUsedObject();

	@DefaultStringValue("Show Detailed Info")
	String quicShowInfo();

	@DefaultStringValue("Image is used in the following paragraphs")
	String infoPanelImagesUsed();

	@DefaultStringValue("Sound is used in the following paragraphs")
	String infoPanelSoundUsed();

	@DefaultStringValue("Image")
	String infoPanelPicture();

	@DefaultStringValue("Sound")
	String infoPanelSound();

	@DefaultStringValue("Book was created by editor&nbsp;<a href=\"http://www.iambookmaster.com\" target=\"_blank\">I am Book Master</a>&nbsp;editor")
	String bookCreatedBy();

	@DefaultStringValue("Book was created by editor \"I am Book Master\" http://www.iambookmaster.com")
	String bookCreatedByText();

	@DefaultStringValue("Arrange imported paragraphs on the map")
	String menuImportArrange();

	@DefaultStringValue("All coordinates of paragraphs will be changed")
	String menuImportArrangeConfirm();

	@DefaultStringValue("Color")
	String connectionColor();

	@DefaultStringValue("Models")
	String quickModels();

	@DefaultStringValue("Parameters, dices, batles, etc.")
	String quickModelsTitle();

	@DefaultStringValue("NPC")
	String parametersNPCName();

	@DefaultStringValue("Add NPC")
	String parametersNPCAddTitle();

	@DefaultStringValue("Parameters")
	String parametersName();

	@DefaultStringValue("Add a new Parameter")
	String parametersAddTitle();

	@DefaultStringValue("Battle Types")
	String parametersBattleName();

	@DefaultStringValue("Add new Battle type")
	String parametersBattleAddTitle();

	@DefaultStringValue("New Parameter")
	String modelNewParameterName();

	@DefaultStringValue("New Battle Type")
	String modelNewBattleName();

	@DefaultStringValue("New NPC")
	String modelNewNPCName();

	@DefaultStringValue("Parameter")
	String menuNewParameter();

	@DefaultStringValue("NPC")
	String menuNewNPC();

	@DefaultStringValue("Battle")
	String menuNewBattle();

	@DefaultStringValue("Modificators")
	String parametersModificatorName();

	@DefaultStringValue("Add a New Modificator")
	String parametersModificatorAddTitle();

	@DefaultStringValue("Modificator")
	String menuNewModificator();

	@DefaultStringValue("New Modificator")
	String modelNewModificatorName();

	@DefaultStringValue("Modificators:")
	String playerListModificators();
	
	@DefaultStringValue("Items/Information (click to use) :")
	String playerListObjects();

	@DefaultStringValue("Items:")
	String playerListObjectsNoUse();
	
	@DefaultStringValue("You")
	String playerBattleHero();

	@DefaultStringValue("Modificator Editor")
	String quickModificatorTitle();
	
	@DefaultStringValue("Alchemy Editor")
	String quickAlchemyTitle();

	@DefaultStringValue("NPC Editor")
	String quickNPCTitle();

	@DefaultStringValue("Battle Type Editor")
	String quickBattleEditor();

	@DefaultStringValue("Parameter Editor")
	String quickParameterTitle();

	@DefaultStringValue("Vital")
	String quickParameterVital();

	@DefaultStringValue("This parameter is vital for a character. If values becames zero or less - chararacter dies")
	String quickParameterVitalTitle();

	@DefaultStringValue("Can be negative")
	String quickParameterNegative();

	@DefaultStringValue("This parameter can have negative value. If the flag not set - onle zero of positive value are acceptable")
	String quickParameterNegativeTitle();
	
	@DefaultStringValue("Hero only")
	String quickParameterHeroOnly();

	@DefaultStringValue("NPCs do not have this parameter")
	String quickParameterHeroOnlyTitle();
	
	@DefaultStringValue("Absolute")
	String quickModificatorAbsolute();

	@DefaultStringValue("This modificator forwards Player to absolute paragraph\nModificator with this flag can forward to only one paragraph")
	String quickModificatorAbsoluteTitle();

	@DefaultStringValue("Battle takes only ONE turn")
	String quickBattleOneTurnTitle();

	@DefaultStringValue("One turn battle")
	String quickBattleOneTurn();

	@DefaultStringValue("Attac and Defence are calculated differently")
	String quickBattleAttacDefenceTitle();

	@DefaultStringValue("Attac/Defence")
	String quickBattleAttacDefence();

	@DefaultStringValue("This Parameter is reduced in the Battle")
	String quickBattleVitalTitle();

	@DefaultStringValue("Resource")
	String quickBattleVital();

	@DefaultStringValue("Damage is Attack minus Defence")
	String quickBattleDefferenceIsDamageTitle();

	@DefaultStringValue("Difference is Damage")
	String quickBattleDefferenceIsDamage();

	@DefaultStringValue("Attack")
	String battleAttack();

	@DefaultStringValue("Defence")
	String battleDefence();

	@DefaultStringValue("Damage")
	String battleDamage();

	@DefaultStringValue("Add Parameter to calculation")
	String battlePlusParameter();

	@DefaultStringValue("Minus Parameter from calculation")
	String battleMinusParameter();

	@DefaultStringValue("Remove Parameter from calculation")
	String battleRemoveParameter();

	@DefaultStringValue("This parameter is already in calculation")
	String battleParameterAlreadyAdded();

	@DefaultStringValue("None")
	String battleFatalNone();

	@DefaultStringValue("Kill (Hero only)")
	String battleFatalDead();

	@DefaultStringValue("Max.Damage")
	String battleFatalNormal();

	@DefaultStringValue("If Dice(s) gets the maximum value is it Fatal strike\nKill - NPC dies immediatelly\nDamage - NPC or Hero gets maximum damage")
	String battleFatalTitle();

	@DefaultStringValue("Fatal strike")
	String quickBattleFatal();

	@DefaultStringValue("Wizards")
	String menuWizards();

	@DefaultStringValue("Create \"classic\" battle system (hits, power of attack, luck, etc)")
	String menuWizardsClassicBattle();

	@DefaultStringValue("You already have some parameters in your book\nDo you really like to add classic battle system?")
	String wizardClassicBattleExists();

	@DefaultStringValue("Do you really like to add classic battle system?")
	String wizardClassicBattleNew();

	@DefaultStringValue("Hits")
	String wizardClassicBattleLive();

	@DefaultStringValue("Hits is live of Character")
	String wizardClassicBattleLiveDescription();

	@DefaultStringValue("Power")
	String wizardClassicBattlePower();

	@DefaultStringValue("Power of strike of Character")
	String wizardClassicBattlePowerDescription();

	@DefaultStringValue("Luck")
	String wizardClassicBattleLuck();

	@DefaultStringValue("Luck of Hero")
	String wizardClassicBattleLuckDescription();

	@DefaultStringValue("Classic battle")
	String wizardClassicBattleBattle();

	@DefaultStringValue("2D6+Power of Character, compare with the opponet and reduce hits of looser")
	String wizardClassicBattleBattleDescription();

	@DefaultStringValue("Battle")
	String paragraphBattle();

	@DefaultStringValue("Enemies")
	String paragraphBattleWithNPC();

	@DefaultStringValue("Hero must follow this connection in a specific condition")
	String connectionTypeTitle();

	@DefaultStringValue("Condition")
	String connectionType();

	@DefaultStringValue("")
	String connectionTypeNormal();

	@DefaultStringValue("Object")
	String connectionTypeObject();

	@DefaultStringValue("Parameter >")
	String connectionTypeParameterIsMore();

	@DefaultStringValue("Parameter <")
	String connectionTypeParameterIsLess();

	@DefaultStringValue("Modificator")
	String connectionTypeModificatorPresent();

	@DefaultStringValue("NO Modificator")
	String connectionTypeModificatorNotPresent();

	@DefaultStringValue("Select Parameter for checking condition")
	String connectionParameterTitle();

	@DefaultStringValue("Select Modificator for checking")
	String connectionModificatorTitle();

	@DefaultStringValue("Modificator")
	String connectionModificator();

	@DefaultStringValue("Parameter")
	String connectionParameter();

	@DefaultStringValue("Value")
	String connectionParameterValue();

	@DefaultStringValue("Hero Vital <")
	String connectionTypeVitalLess();

	@DefaultStringValue("Enemy vital <")
	String connectionTypeEnemyVitalLess();
	
	@DefaultStringValue("Battle round counter >")
	String connectionTypeBattleRoundsMore();

	@DefaultStringValue("Paragraph Text")
	String ParagraphsEditorMainTab();

	@DefaultStringValue("Extended parameters")
	String ParagraphsEditorSecondTab();

	@DefaultStringValue("Change Hero Parameter")
	String paragraphEditorChangeParameters();

	@DefaultStringValue("Add Parameter to change")
	String ParagraphEditorAddParameterTitle();

	@DefaultStringValue("Select Parameter for changing")
	String ParagraphEditorSelectParameterTitle();

	@DefaultStringValue("Set Modificator")
	String ParagraphEditorSetModificator();

	@DefaultStringValue("Clear Modificator")
	String ParagraphEditorClearModificator();

	@DefaultStringValue("Select Modificator for set/clear")
	String paragraphEditorChangeModificator();

	@DefaultStringValue("Can follow")
	String connectionStrictnessCan();

	@DefaultStringValue("Must follow")
	String connectionStrictnessMust();

	@DefaultStringValue("Must NOT follow")
	String connectionStrictnessMustNot();

	@DefaultStringValue("Strictness")
	String connectionStrictness();

	@DefaultStringValue("How strict this condition to Hero.\nCAN - Hero can select this connection\nMUST - Hero must select only this connection\nMUST NOT - Hero cannot select this connection")
	String connectionStrictnessTitle();

	@DefaultStringValue("Limit")
	String quickParameterLimit();

	@DefaultStringValue("Value of parameter cannot exceed value of Parameter-Limit")
	String quickParameterLimitTitle();

	@DefaultStringValue("Remove Parameter from Calculation")
	String calculationRemoveParameter();

	@DefaultStringValue("Alchemy")
	String parametersConvertersName();

	@DefaultStringValue("Add new Alchemy")
	String parametersConverteAddTitle();

	@DefaultStringValue("New Alchemy")
	String modelNewAlchemyName();

	@DefaultStringValue("Alchemy")
	String menuNewAlchemy();

	@DefaultStringValue("Convert 1 item of of this Parameter")
	String alchemyFromTitle();

	@DefaultStringValue("From")
	String quickAlchemyFrom();

	@DefaultStringValue("To N items of this Parameter")
	String alchemyToTitle();

	@DefaultStringValue("To")
	String quickAlchemyTo();

	@DefaultStringValue("This number is added to value of 'To' Parameter")
	String alchemyValueTitle();

	@DefaultStringValue("From Value")
	String quickAlchemyFromValue();

	@DefaultStringValue("To Value")
	String quickAlchemyToValue();
	
	@DefaultStringValue("When this convertation is acceptable")
	String quickAlchemyBattleTitle();

	@DefaultStringValue("Battle time")
	String quickAlchemyBattle();

	@DefaultStringValue("Peace time")
	String quickAlchemyPeace();

	@DefaultStringValue("Anytime")
	String quickAlchemyBoth();
	
	@DefaultStringValue("Acceptable in")
	String quickAlchemyPlace();

	@DefaultStringValue("Food")
	String wizardClassicBattleFood();

	@DefaultStringValue("Simple food, gives 3 hits to Hero")
	String wizardClassicBattleFoodDescription();

	@DefaultStringValue("Eat food (+3 hits)")
	String wizardClassicBattleFoodConvetion();

	@DefaultStringValue("Each pease of food gives +3 hits to Hero. Cannot be used in Battles")
	String wizardClassicBattleFoodConvetionDescription();

	@DefaultStringValue("Max.Power")
	String wizardClassicBattleMaxPower();

	@DefaultStringValue("Power of Hero cannot be more than this parameter")
	String wizardClassicMaxBattlePowerDescription();

	@DefaultStringValue("Hero value")
	String quickParameterInitialHeroValue();

	@DefaultStringValue("This values has to be defined before Game")
	String quickParameterHeroHasInitialValueTitle();

	@DefaultStringValue("Show Modificators to Player")
	String quickPlayerShowModificators();

	@DefaultStringValue("Modificators will be shown in Player List")
	String quickPlayerShowModificatorsTitle();

	@DefaultStringValue("Attack!")
	String playerButtonAttack();

	@DefaultStringValue("Click to next turn of the battle")
	String playerButtonAttackTitle();

	@DefaultStringValue("Auto")
	String playerButtonAutoBattle();

	@DefaultStringValue("Click to finish battle automatically")
	String playerButtonAutoBattleTitle();

	@DefaultStringValue("Click to remove")
	String paragraphRemoveModificator();

	@DefaultStringValue("Remove this calculation from Paragraph")
	String removeParameterCalculationFromParagraph();

	@DefaultStringValue("No Ememies are set for the Battle")
	String playerBattleNoEmenies();

	@DefaultStringValue("Modificator is not set for the condition")
	String modificatorNotSetInParagraph();

	@DefaultStringValue("Parameter is not set for the condition")
	String parameterNotSetInParagraph();

	@DefaultStringValue("Value for comarision is not set for the condition")
	String parameterValueNotSetInParagraph();

	@DefaultStringValue("Hide non-matched connections")
	String quickPlayerHideNonMathedParametersConnections();

	@DefaultStringValue("Hide Connection if Parameter does not match or Modificator is not set")
	String quickPlayerHideNonMathedParametersConnectionsTitle();

	@DefaultStringValue("Show Console in Battles")
	String quickPlayerShowBattleConsole();

	@DefaultStringValue("Console with history of a Battle will be shown in Editor Player. It does not work in stand-alone Player")
	String quickPlayerShowBattleConsoleTitle();

	@DefaultStringValue("Only when allowed")
	String quickAlchemyOnDemand();

	@DefaultStringValue("This alchemy can be performed only where it explicitly set")
	String quickAlchemyOnDemandTitle();

	@DefaultStringValue("Against NPC")
	String quickAlchemyWeapond();

	@DefaultStringValue("This alchemy is worked as weapon again NPC")
	String quickAlchemyWeapondTitle();

	@DefaultStringValue("This alchemy is always available. No reason to add to the Paragraph")
	String ParagraphEditorNonDemandAlchemy1();
	
	@DefaultStringValue("This alchemy is not available by default. No reason to add to the Paragraph")
	String ParagraphEditorNonDemandAlchemy2();

	@DefaultStringValue("Alchemy")
	String paragraphEditorAlchemy();

	@DefaultStringValue("Select Alchemy for enable/disable")
	String ParagraphEditorSelectAlchemyTitle();

	@DefaultStringValue("Enable this Alchemy")
	String ParagraphEditorEnableAlchemy();

	@DefaultStringValue("Disable this Alchemy")
	String ParagraphEditorDisableAlchemy();

	@DefaultStringValue("Add On-Demand alchemy to text")
	String quickSettingsAddAlchemyToText();

	@DefaultStringValue("Value of on-Demand Alchemy will be inclued into text of the Paragraph")
	String quickSettingsAddAlchemyToTextTitle();

	@DefaultStringValue("Paragraph has reference to non-existed Alchemy, or this Alchemy is not available for the Paragraph")
	String modelParagraphUnknownReferenceAlchemyText();

	@DefaultStringValue("In-text references to Alchemy is disabled in the settings of the project")
	String modelParagraphReferenceAlchemyDisabled();

	@DefaultStringValue("This paramete restricts using Alchemy in a Battle")
	String alchemyBattleLimitTitle();

	@DefaultStringValue("Battle use Limit")
	String alchemyBattleLimit();

	@DefaultStringValue("One time per battle round")
	String quickAlchemyOneTimePerRound();

	@DefaultStringValue("This Alchemy can be used just one time per Battle round")
	String quickAlchemyOneTimePerRoundTitle();

	@DefaultStringValue("Paragraph has a Battle, but does not have a reference in the text")
	String modelParagraphNoBattleRef();

	@DefaultStringValue("Paragraph does not have a Bettle, but has a reference in the text")
	String modelParagraphNoBattleSet();

	@DefaultStringValue("Paragraph has some references to the Battle in the text")
	String modelParagraphDoubleBattleRef();

	@DefaultStringValue("Only connection between paragraphs can have conditions prefix (<>) in a paragraph text")
	String modelParagraphObjectWithConditionPrefix();

	@DefaultStringValue("Paragraph has a battle, but does not have Enemies")
	String modelParagraphBattleNoEnemies();

	@DefaultStringValue("Paragraph should not have Modificators in the text")
	String modelParagraphMustNotHaveModificatorsInText();

	@DefaultStringValue("Some Modificators of the Paragraph or Paragraph Connections do not have a reference in the text")
	String modelParagraphUnreferredModificators();

	@DefaultStringValue("Add Modificators to text")
	String quickSettingsAddModificatorNamesToText();

	@DefaultStringValue("A special tag <m...> will be added into Paragraph text if the Paragraph has Modificators.\nDuring generation of the book this tag will be replaced to tne name of Modificator\nName and value of an Absolut Modificato is always added where the Modificator is set")
	String quickSettingsAddModificatorNamesToTextTitle();

	@DefaultStringValue("Killed.")
	String battleKill();

	@DefaultStringValue("Died.")
	String battleDied();

	@DefaultStringValue("Fatal strike.")
	String battleFatalStrike();

	@DefaultStringValue("but got Fatal strike.")
	String battleFatalStrikeBack();

	@DefaultStringValue("Condition that connection must not be used when a vital parameter is less that a value makes not sence")
	String modelMustNotVitalConnectionMakesNoSense();

	@DefaultStringValue("Limit of Luck")
	String wizardClassicBattleMaxLuck();

	@DefaultStringValue("Luck can be from 6 to 0. 6 - lucky, 0 - no luck at all")
	String wizardClassicBattleMaxLuckDescription();

	@DefaultStringValue("Find&Replace")
	String menuWizardsFindAndReplace();

	@DefaultStringValue("Find&Replace")
	String FindAndReplaceTitle();

	@DefaultStringValue("Find")
	String findReplaceFindText();
	
	@DefaultStringValue("Replace")
	String findReplaceReplaceText();

	@DefaultStringValue("Find")
	String findReplaceFindButton();

	@DefaultStringValue("Replace")
	String findReplaceReplaceButton();

	@DefaultStringValue("Case Sencetive")
	String findReplaceCaseSencetive();

	@DefaultStringValue("Whole word/phrase")
	String findReplaceWholeWord();

	@DefaultStringValue("Search phrase is empty")
	String findReplaceEmptyString();

	@DefaultStringValue("Not Found")
	String findReplaceNotFound();

	@DefaultStringValue("Edit Rules")
	String menuRules();

	@DefaultStringValue("Rules")
	String EditRulesTitle();

	@DefaultStringValue("Rules for paper book")
	String rulesBookRules();

	@DefaultStringValue("Rules for Player")
	String rulesPlayerRules();

	@DefaultStringValue("Re-create Rules for paper Book")
	String rulesConfirmRecreate();

	@DefaultStringValue("Rules for paper Book will be recreated according to your current settings of the project")
	String rulesRecreateButtonTitle();

	@DefaultStringValue("Rules of the Game")
	String bookRules();

	@DefaultStringValue("Into in Action-Fiction")
	String rulesTemplateIntro();

	@DefaultStringValue("Absolute Modificators")
	String rulesTemplateAbsoluteModificators();

	@DefaultStringValue("Normal Modificators")
	String rulesTemplateNormalModificators();

	@DefaultStringValue("Your Hero has parameters")
	String rulesTemplateHeroHasParameters();

	@DefaultStringValue(" If value of this parameter becames zero or less - a Character died")
	String rulesTemplateParameterVital();

	@DefaultStringValue(" If value of this parameter becames zero or less - your Hero is die")
	String rulesTemplateParameterVitalHero();

	@DefaultStringValue("Battles:")
	String rulesTemplateBattles();

	@DefaultStringValue("Can be negative.")
	String rulesTemplateParameterNegative();

	@DefaultStringValue("The parameter cannot has negative value")
	String rulesTemplateParameterPositive();

	@DefaultStringValue("You can find an Object. Each Object has a secret key (like \"Silver sword +10\"). Write the name of the Object and the key to your Player List. When you need to use this Object - add the ket to the number of the paragraph and open paragraph, but do not close the current one. If you see number of the current paragraph in the first list of a new paragraph - the usege was correct, close the current paragraph and start reading the new")
	String rulesTemplateHiddenObjects();

	@DefaultStringValue("You can find an Object. Write the name of the Object into your Player List.")
	String rulesTemplateObjects();

	@DefaultStringValue("You can do it just one time per battle round and if you did it you do not have time for other similar actions")
	String rulesTemplateAlchemyOneTimePerRound();

	@DefaultStringValue("Use")
	String urqUseObjectCommand();

	@DefaultStringValue("Cannot be used here")
	String urqCannotUseObject();

	@DefaultStringValue("no modificators")
	String urqNoModificators();

	@DefaultStringValue("Your parameters")
	String urlListParameters();

	@DefaultStringValue("Save by Clipboard")
	String menuClipboardSave();

	@DefaultStringValue("Load by Clipboard")
	String menuClipboardLoad();

	@DefaultStringValue("Select file to load project")
	String menuLoadProjectFile();

	@DefaultStringValue("Select existed file to save project")
	String menuSaveProjectFile();

	@DefaultStringValue("Save")
	String buttonSave();

	@DefaultStringValue("Direct access to files is not available")
	String fileDirectAccessNotAvailable();

	@DefaultStringValue("The \"Include local directory path when uploading files\" option is set to \"Disable\" for the current zone. Please, enable it for using direct files access")
	String fileDirectAccessNoFullPathIE8();

	@DefaultStringValue("Save this text to text file")
	String fileDirectAccessSaveScreen();

	@DefaultStringValue("Attack:")
	String urqHeroAttack();

	@DefaultStringValue("Your Attack:")
	String qspHeroAttack();
	
	@DefaultStringValue("Defense:")
	String urqHeroDefense();

	@DefaultStringValue("Your Defense:")
	String qspHeroDefense();

	@DefaultStringValue("Show ID of connections in Paragraph Editor")
	String quickSettingsShowConnectionIDs();

	@DefaultStringValue("ID of each connection will be shown in the header of the linked paragraph in  Paragraph Editor")
	String quickSettingsShowConnectionIDsTitle();

	@DefaultStringValue("Killed")
	String urqKilled();

	@DefaultStringValue("Select Target for next battle rounds")
	String urqSelectTarget();

	@DefaultStringValue("Select target")
	String urqSelectTargetBtn();

	@DefaultStringValue("><")
	String urqTarget();

	@DefaultStringValue("Cancel")
	String urqCancelTargetSelection();

	@DefaultStringValue("Cannot save game in battle time")
	String urqCannotSaveInBattle();

	@DefaultStringValue("State_Game_Save")
	String urqSaveGame();

	@DefaultStringValue("State_Game")
	String urqSave();

	@DefaultStringValue("Kill by Fatal strike")
	String urqKillByFatalStrike();

	@DefaultStringValue("About")
	String urqAbout();

	@DefaultStringValue("Many thanks to:")
	String urqGreetings();

	@DefaultStringValue("New Item")
	String newObjectName();

	@DefaultStringValue("auto-pass paragraphs with must-go condition")
	String quickPlayerSkipMustGoParagraphs();

	@DefaultStringValue("If paragraph is connected to other paragraph, the connection has 'must go' strictness and meats the condition - this paragraph will not be shown in Player")
	String quickPlayerSkipMustGoParagraphsTitle();

	@DefaultStringValue("Name of this connection for URQ or other external menu-bases interactive player. Each connection can have some names, separated by comma")
	String quickConnectionNameTitle();


	@DefaultStringValue("Click to select Connection")
	String ParagraphEditorClick2Connection();

	@DefaultStringValue("Click to select Paragraph")
	String ParagraphEditorClick2Paragraph();

	@DefaultStringValue("Name")
	String quickConnectionNameFrom();
	
	@DefaultStringValue("Reverse name")
	String quickConnectionNameTo();

	@DefaultStringValue("Show connection names")
	String quickSettingsShowConnectionNames();

	@DefaultStringValue("Connection names will be shown in Paragraph Editor")
	String quickSettingsShowConnectionNamesTitle();

	@DefaultStringValue("Fill empty paragraph connection names")
	String menuCreateConnectionNames();

	@DefaultStringValue("Find Paragraph connection by numbers")
	String menuConnectionByNumbers();

	@DefaultStringValue("Find Paragraph by number")
	String menuParagraphNumber();

	@DefaultStringValue("Enter Number of Paragraph")
	String paragraphByNumberPrompt();

	@DefaultStringValue("Enter Number of the linked Paragraph")
	String paragraphByNumberLinkedPrompt();

	@DefaultStringValue("Create one way connection?")
	String paragraphConnectionCreateTwoWays();

	@DefaultStringValue("At least one success paragraph must be defined")
	String noSuccessParagraphsDefined();

	@DefaultStringValue("No applet detected. Access to local files is not available")
	String noApplet();

	@DefaultStringValue("Uncountable")
	String quickSettingsMultyObjects();

	@DefaultStringValue("Object can be found some times. But objects cannot be counted, so if Player finds the object two times - he still has 1 object. Usually it is something non-material, like Informaton")
	String quickSettingsMultyObjectsTitle();

	@DefaultStringValue("Create a new Paragraph and connect to the current")
	String createParagraphAndConnection();

	@DefaultStringValue("The same paragraph are selected")
	String paragraphsTheSame();

	@DefaultStringValue("These paragraphs are already connected")
	String paragraphsAlreadyConnected();

	@DefaultStringValue("Insert a new Paragraph in this Connection")
	String splitParagraphConnection();

	@DefaultStringValue("Select Paragraph")
	String openParagraph();

	@DefaultStringValue("Add Income connection to paragraph")
	String importAddInConnection();

	@DefaultStringValue("Add Outcome connection to paragraph")
	String importAddOutConnection();

	@DefaultStringValue("Your browser does not have Java Runtime Environment (JRE).\nJRE is used for storing you current game to the local disk and restoring it back.\nWithout JRE you are still able to save/load your game using Clipboard\nInstall JRE now?")
	String installJRE();

	@DefaultStringValue("Show Player list items like a column")
	String quickPlayerVerticalObject();

	@DefaultStringValue("By default all components of Player List are shown in a line. If this flag is set the components are shown in a column")
	String quickPlayerVerticalObjectTitle();

	@DefaultStringValue("How to show connections in Player")
	String quickPlayerConnectionsType();

	@DefaultStringValue("Default")
	String quickPlayerConnectionTypeDefault();

	@DefaultStringValue("Prev. word")
	String quickPlayerConnectionTypeLastWord();

	@DefaultStringValue("<...>")
	String quickPlayerConnectionTypeBrackets();

	@DefaultStringValue("Connecton name")
	String quickPlayerConnectionTypeName();

	@DefaultStringValue("Back")
	String buttonBackToGame();

	@DefaultStringValue("Start")
	String buttonStartGame();

	@DefaultStringValue("Bulk import of Corrected text?")
	String modelBulkCorrection();

	@DefaultStringValue("Insert the Corrected text")
	String modelBulkCorrectionLoad();

	@DefaultStringValue("Images&Sounds")
	String ParagraphsEditorImagesTab();

	@DefaultStringValue("Send Sprite to Back (z-index)")
	String paragraphMoveSpriteUp();

	@DefaultStringValue("Send Sprite to Front (z-index)")
	String paragraphMoveSpriteDown();

	@DefaultStringValue("Cannot load data")
	String iphoneErrorLoadingHTML();

	@DefaultStringValue("dd/MMM/y hh:mm")
	String fullDateFormat();

	@DefaultStringValue("Game saved")
	String iphoneGameSaved();

	@DefaultStringValue("<p>Your Choice:<p/>")
	String iphoneYourChoice();

	@DefaultStringValue("Continue")
	String iphoneContinue();

	@DefaultStringValue("Start New Game")
	String iphoneStartNewGame();

	@DefaultStringValue("Continue Game")
	String iphoneContinueGame();

	@DefaultStringValue("Start Game")
	String iphoneStartGame();

	@DefaultStringValue("Read Help")
	String iphoneHelp();

	@DefaultStringValue("Greetings")
	String iphoneGreetings();

	@DefaultStringValue("Back")
	String iphoneBack();

	@DefaultStringValue("Leave feedback")
	String iphoneLeaveFeedback();

	@DefaultStringValue("Please Leave your feedback")
	String iphoneFeedbackTitle();

	@DefaultStringValue("You will need an access to the Internet to use these functions.") 
	String iphoneFeedbackInstructions();

	@DefaultStringValue("Feedback/Questions")
	String iphoneFeedbackGo();

	@DefaultStringValue("Find similar games")
	String iphoneViewOthers();

	@DefaultStringValue("More...")
	String iphoneViewMore();

	@DefaultStringValue("Slide left to veiw Adventure List")
	String iphoneYouHaveAdventureList();

	@DefaultStringValue("Introduction")
	String iphoneHelpIntro();

	@DefaultStringValue("How to play")
	String iphoneHelpPlay();

	@DefaultStringValue("Adventure List")
	String iphoneHelpAdventureList();

	@DefaultStringValue("Save/Load")
	String iphoneHelpSave();

	@DefaultStringValue("Continue")
	String iphoneHelpNext();

	@DefaultStringValue("Image-filler")
	String quickImageEditFiller();

	@DefaultStringValue("Image-filler is an image randomly shown on pages whithout own images")
	String quickImageEditFillerTitle();

	@DefaultStringValue("Restore Game")
	String iphoneRestoreFailedGame();

	@DefaultStringValue("Close")
	String iphoneCloseMessage();

	@DefaultStringValue("Make Own Game")
	String iphoneCreateOwnGame();

	@DefaultStringValue("Have a Question")
	String iphoneQuestionGo();

	@DefaultStringValue("Web Player")
	String menuPlayerNormal();

	@DefaultStringValue("iPhone Player (320x480)")
	String menuPlayerIPhone();

	@DefaultStringValue("iPad Player (1024x768)")
	String menuPlayerIPad();

	@DefaultStringValue("Tablet 800x600 Player")
	String menuPlayer800X600();

	@DefaultStringValue("Table 1024x600 Player")
	String menuPlayer1024X600();
	
	@DefaultStringValue("Rotate device")
	String playerIphoneRotate();

	@DefaultStringValue("Edit the current paragraph")
	String playerIphoneEditParagraph();

	@DefaultStringValue("Click to Load/Save/Restart/Rotate...")
	String playerIphoneOptions();

	@DefaultStringValue("Restart Game")
	String playerIphoneRestartGame();

	@DefaultStringValue("Save Game")
	String playerIphoneSaveGame();

	@DefaultStringValue("Load Game")
	String playerIphoneLoadGame();

	@DefaultStringValue("Available actions:")
	String iphoneAvailableAlchemy();

	@DefaultStringValue("Applied")
	String iphoneAlchemyApplied();

	@DefaultStringValue("Tap Enemy to change target")
	String iphoneButtleInstructions();

	@DefaultStringValue("Adventures list is not available in a Battle")
	String iphoneBattleNoBag();

	@DefaultStringValue("Click to select this NPC as a Target")
	String battleClickToSelectTarget();

	@DefaultStringValue("Click to scroll Down")
	String playerIphoneScrollDown();

	@DefaultStringValue("Click to scroll Up")
	String playerIphoneScrollUp();

	@DefaultStringValue("Click to scroll Right")
	String playerIphoneScrollRight();

	@DefaultStringValue("Click to scroll Left")
	String playerIphoneScrollLeft();

	@DefaultStringValue("")
	String quickNPCGenativeName();

	@DefaultStringValue("")
	String quickNPCGenativeNameTitle();

	@DefaultStringValue("")
	String battleDamageToNPC();

	@DefaultStringValue("Gets")
	String battleDamageToVictim();
	
	@DefaultStringValue("Gets")
	String battleDamageToVictimFromNPC();
	
	@DefaultStringValue("Successful, ")
	String battleDamageToNPCFromVictim();

	@DefaultStringValue("Your")
	String battleDamageToHero();

	@DefaultStringValue("Regenerate text of ALL paragraphs")
	String menuRefreshParagraphsText();

	@DefaultStringValue("Regenerate")
	String buttonRegenerate();

	@DefaultStringValue("Warning! Text of all paragraphs of the selected type will be regenerated. Save your project before performing this operation.")
	String menuRefreshParagraphsTextWarning();

	@DefaultStringValue("Select type of paragraphs:")
	String menuRefreshParagraphsStatus();

	@DefaultStringValue("Go to the selected Paragraph")
	String playerIphoneStartCurrent();

	@DefaultStringValue("Import Game")
	String iphoneImportGame();

	@DefaultStringValue("Invisible")
	String quickParameterInvisibleTitle();
	
	@DefaultStringValue("This parameter is Invisible in players")
	String quickParameterInvisibleFullTitle();

	@DefaultStringValue("Start paragraph cannot be marked as Commercial")
	String modelParagraphIsStartCannotBeCommercial();

	@DefaultStringValue("Mark as Commercial")
	String importMakeCommecial();

	@DefaultStringValue("This paragraph is available in commercial version only")
	String quickParagraphCommercialTitle();

	@DefaultStringValue("Find all commercial-only paragraphs")
	String menuDetectAllCommercials();

	@DefaultStringValue("Re-mark all commercial-only paragrapghs?")
	String confirmFindCommercial();

	@DefaultStringValue("Re-mark all commercial-only paragrapghs")
	String validatorCommercialParagraphs();

	@DefaultStringValue("Non-commercial version")
	String quickPlayerLightVersion();

	@DefaultStringValue("Generate/Play in Non-commercial version")
	String quickPlayerLightVersionTitle();

	@DefaultStringValue("Save light-version only")
	String menuSaveLight();

	@DefaultStringValue("Select files to save LIGHT version of the project")
	String menuSaveProjectFileLight();

	@DefaultStringValue("Play non-commecrial version only")
	String quickPlayerNonCommercialOny();

	@DefaultStringValue("Only non-commercial paragraphs will be shown to a Gamer")
	String quickPlayerNonCommercialOnyTitle();

	@DefaultStringValue("Commercial text")
	String commercialWelcomeText();

	@DefaultStringValue("Text about the full version in the light version")
	String commercialTextDefaultTitle();

	@DefaultStringValue("Congratulations! You have successfuly finished the light version of the game. Please purchase a full version for continue")
	String commercialTextDefault();

	@DefaultStringValue("Buy Full version")
	String iphoneBuyFullVersion();

	@DefaultStringValue("Deme verion info on the first page")
	String commercialTextMainTitle();

	@DefaultStringValue("Import Saved Game")
	String iphoneImportLightVersion();

	@DefaultStringValue("Switch to Full Version")
	String iphoneGoToFullVersion();

	@DefaultStringValue("You should switch to the Full Version to continue the game")
	String iphoneGoToFullVersionText();

	@DefaultStringValue("View all paragraph connection names")
	String menuViewAllParagraphTransitions();

	@DefaultStringValue("If you like this game please encourage the author to write more games by a small donation.")
	String iphoneDonationText();

	@DefaultStringValue("Donate")
	String iphoneDonation();

	@DefaultStringValue("View books")
	String iphoneBuyPaperBook();

	@DefaultStringValue("New and re-printed paper game-books")
	String iphoneBuyPaperBookText();

	@DefaultStringValue("Thank you for you generous donation")
	String iphoneThankyouForDonation();

	@DefaultStringValue("Connecting to App Store...")
	String iphoneThankyouRetrievingPrice();

	@DefaultStringValue("Processing the transaction")
	String iphoneThankyouPaying();

	@DefaultStringValue("Cancel")
	String iphoneThankyouCancel();

	@DefaultStringValue("Try Again")
	String iphoneThankyouTryAgain();

	@DefaultStringValue("Connection to App Store was stablished")
	String iphoneThankyouGotPrice();

	@DefaultStringValue("We appricate you willing to support our project. Could you please try again?")
	String iphoneThankyouDonateError();

	@DefaultStringValue("Order by name")
	String sort();

	@DefaultStringValue("Round when joins to the battle")
	String paragraphEditorEnemyRoundsTitle();

	@DefaultStringValue("Supports Hero")
	String paragraphEditorEnemyFriendTitle();

	@DefaultStringValue("Hero fights after Friends")
	String ParagraphEditorFightTogether();

	@DefaultStringValue("Auto-overflow control")
	String quickPlayerOverflowControl();

	@DefaultStringValue("A parameter cannot excide the own limit by default")
	String quickPlayerOverflowControlTitle();

	@DefaultStringValue("Do not control Limit overflow")
	String calculationNoOverflowControl();

	@DefaultStringValue("Control Limit overflow")
	String calculationOverflowControl();

	@DefaultStringValue("A parameter CAN excide the own limit")
	String quickAlchemyNoOverflowControlTitle();

	@DefaultStringValue("A parameter cannot excide the own limit")
	String quickAlchemyOverflowControlTitle();

	@DefaultStringValue("Cannot retrieve product info from AppStore. The most common reason of this error is problems with connection to Internet")
	String iphoneCannotConnectAppStore();

	@DefaultStringValue("Sort by Name")
	String sortByName();

	@DefaultStringValue("Sort by Number")
	String sortByNumber();

	@DefaultStringValue("Unfortunatelly, the product cannot be retrieved from AppStore.")
	String iphoneThankyouErrorNoItem();

	@DefaultStringValue("If value is 1 just name of the Parameter should be shown")
	String quickParameterSupressOneValueTitle();

	@DefaultStringValue("Do not show 1 (One) value")
	String quickParameterSupressOneValue();

	@DefaultStringValue("Validate connection names")
	String menuValidateConnectionNames();

	@DefaultStringValue("Validation connection names")
	String validateConnectionsStart();

	@DefaultStringValue("Reverse hidden usage")
	String quickReverseHiddenUsage();

	@DefaultStringValue("This connection revers hidden rules")
	String quickReverseHiddenUsageTitle();

	@DefaultStringValue("Icon")
	String quickImageEditRoleIcon();
	
	@DefaultStringValue("Icons can be used for Parameters/Object/Alchemy/etc.")
	String quickImageEditRoleIconTitle();

	@DefaultStringValue("Big Image URL")
	String quickImageEditBigURL();
	
	@DefaultStringValue("Image for high resolution")
	String quickImageEditBigURLTitle();

	@DefaultStringValue("Size")
	String quickImageEditSize();

	@DefaultStringValue("Cannot load image")
	String quickImageEditWrongURL();

	@DefaultStringValue("Image")
	String quickObjectImage();

	@DefaultStringValue("Image for Player List")
	String quickObjectImageTitle();

	@DefaultStringValue("Click to slide Right")
	String playerIphonePageLeft();

	@DefaultStringValue("Click to slide Left")
	String playerIphonePageRight();

	@DefaultStringValue("iURQ")
	String menuURQ();

	@DefaultStringValue("Select QST file")
	String loadURQ();

	@DefaultStringValue("Load module")
	String loadModule();

}