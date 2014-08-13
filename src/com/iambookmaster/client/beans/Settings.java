package com.iambookmaster.client.beans;

import java.io.Serializable;
import java.util.ArrayList;

import com.iambookmaster.client.common.JSONBuilder;
import com.iambookmaster.client.common.JSONParser;
import com.iambookmaster.client.model.Model;

public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String JSON_BOOK_AUTHORS = "copyright";
	private static final String JSON_BOOK_DESCRIPTION = "description";
	private static final String JSON_BOOK_TITLE = "title";
	
	private static final String JSON_MAX_ATTEMPT = "a";
	private static final String JSON_MIN_SEPARATION = "b";
	private static final String JSON_ONE_WAY_ONLY = "c";
	private static final String JSON_HIDDEN_USING_OBJECTS = "d";
	private static final String JSON_PLAYER_LIST_TYPE = "e";
	private static final String JSON_PLAYER_TEXT_COLOR = "f";
	private static final String JSON_PLAYER_BAG_COLOR = "i";
	private static final String JSON_PLAYER_APP_COLOR = "j";
	private static final String JSON_PLAYER_TEXT_BACKGROUND = "h";
	private static final String JSON_PLAYER_DISABLE_AUDIO = "k";
	private static final String JSON_PLAYER_DISABLE_IMAGES = "l";
	private static final String JSON_GAME_VERSION = "m";
	private static final String JSON_PLAYER_SHOW_ABOUT = "n";
	private static final String JSON_GREETINGS = "o";
	private static final String JSON_SHOW_PARAGRAPH_NUMBERS = "p";
	private static final String JSON_FINE_SECRET_KEYS = "r";
	private static final String JSON_MAX_DIMENSION_X = "s";
	private static final String JSON_MAX_DIMENSION_Y = "t";
	private static final String JSON_FEEDBACK_EMAIL = "y";
	private static final String JSON_SHOW_MODIFICATORS = "z";
	private static final String JSON_SHOW_BATTLE_CONSOLE = "A";
	private static final String JSON_HIDE_NON_MATHED_PARAMETER_CONNECTIOS = "B";
	private static final String JSON_ADD_ALCHEMY_TO_TEXT = "C";
	private static final String JSON_ADD_MODIFICATOR_NAMES_TO_TEXT = "D";
	private static final String JSON_SHOW_CONNECTION_IDS = "E";
	private static final String JSON_SKIP_MUST_GO = "F";
	private static final String JSON_SHOW_CONNECTION_NAMES = "G";
	private static final String JSON_VERTICAL_OBJECTS = "H";
	private static final String JSON_SHOW_CONNECTION_TYPE = "I";
	private static final String JSON_DEMO_VERSION = "J";

	private static final int MIN_X = 2048;
	private static final int MIN_Y = 2048;

	private static final String JSON_OVERFLOW_CONTROL = "K";



	private int gameVersion;
	private boolean showParagraphNumbers;
	private boolean oneWayConnectionsOnly;
	
	/**
	 * Settings for text book generation
	 */
	private int maxAttemptCount=10;
	private int minimalSeparation=1;
	
	private String bookTitle;
	private String bookDescription;
	private String bookAuthors;
	
	/**
	 * Settings for player
	 */
	private boolean hiddenUsingObjects=true;
	private int playerListType;
	private int textColor;
	private int bagColor;
	private int applicationColor;
	private int textBackground;
	private boolean disableAudio;
	private boolean disableImages;
	private boolean showAboutOnStart;
	private int fineSecretKeys;
	private int maxDimensionX;
	private int maxDimensionY;
	private String feedbackEmail;
	private ArrayList<Greeting> greetings = new ArrayList<Greeting>();
	private boolean showModificators;
	private boolean showBattleConsole=true;
	private boolean hideNonMatchedParameterConnections;
	private boolean addAlchemyToText;
	private boolean addModificatorNamesToText=true;
	private boolean showConnectionsIDs;
	private boolean skipMustGoParagraphs;
	private boolean showConnectionNames;
	private boolean verticalObjects;
	private int showConnectionType;
	private boolean demoVersion;
	private boolean overflowControl;
	
	public boolean isOverflowControl() {
		return overflowControl;
	}

	public void setOverflowControl(boolean overflowControl) {
		this.overflowControl = overflowControl;
	}

	public int getShowConnectionType() {
		return showConnectionType;
	}

	public void setShowConnectionType(int showConnectionType) {
		this.showConnectionType = showConnectionType;
	}

	public boolean isVerticalObjects() {
		return verticalObjects;
	}

	public void setVerticalObjects(boolean verticalObjects) {
		this.verticalObjects = verticalObjects;
	}

	public boolean isShowConnectionNames() {
		return showConnectionNames;
	}

	public void setShowConnectionNames(boolean showConnectionNames) {
		this.showConnectionNames = showConnectionNames;
	}

	public boolean isSkipMustGoParagraphs() {
		return skipMustGoParagraphs;
	}

	public void setSkipMustGoParagraphs(boolean skipMustGoParagraphs) {
		this.skipMustGoParagraphs = skipMustGoParagraphs;
	}

	public boolean isShowConnectionsIDs() {
		return showConnectionsIDs;
	}

	public void setShowConnectionsIDs(boolean showConnectionsIDs) {
		this.showConnectionsIDs = showConnectionsIDs;
	}

	public boolean isAddAlchemyToText() {
		return addAlchemyToText;
	}

	public void setAddAlchemyToText(boolean addAlchemyToText) {
		this.addAlchemyToText = addAlchemyToText;
	}

	public boolean isShowBattleConsole() {
		return showBattleConsole;
	}

	public void setShowBattleConsole(boolean showBattleConsole) {
		this.showBattleConsole = showBattleConsole;
	}

	public String getFeedbackEmail() {
		return feedbackEmail;
	}

	public void setFeedbackEmail(String feedbackEmail) {
		this.feedbackEmail = feedbackEmail;
	}

	public int getMaxDimensionX() {
		return maxDimensionX<=MIN_X ? MIN_X : maxDimensionX;
	}

	public void setMaxDimensionX(int maxDimensionX) {
		this.maxDimensionX = maxDimensionX;
	}

	public int getMaxDimensionY() {
		return maxDimensionY<=MIN_Y ? MIN_Y : maxDimensionY;
	}

	public void setMaxDimensionY(int maxDimensionY) {
		this.maxDimensionY = maxDimensionY;
	}

	public String getBookAuthors() {
		return bookAuthors==null ? "" : bookAuthors;
	}

	public void setBookAuthors(String bookAuthors) {
		this.bookAuthors = bookAuthors;
	}

	public String getBookDescription() {
		return bookDescription==null ? "" : bookDescription;
	}

	public void setBookDescription(String bookDescription) {
		this.bookDescription = bookDescription;
	}

	public String getBookTitle() {
		return bookTitle==null ? "" : bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public boolean isShowParagraphNumbers() {
		return showParagraphNumbers;
	}

	public void setShowParagraphNumbers(boolean showLocationId) {
		this.showParagraphNumbers = showLocationId;
	}

	public int getMaxAttemptCount() {
		return maxAttemptCount;
	}

	public void setMaxAttemptCount(int maxAttemptCount) {
		this.maxAttemptCount = maxAttemptCount;
	}

	public void setMinimalSeparation(int minimalSeparation) {
		this.minimalSeparation = minimalSeparation;
	}

	public int getMinimalSeparation() {
		return minimalSeparation;
	}

	public boolean isHideNonMatchedParameterConnections() {
		return hideNonMatchedParameterConnections;
	}

	public void setHideNonMatchedParameterConnections(
			boolean hideNonMatchedParameterConnections) {
		this.hideNonMatchedParameterConnections = hideNonMatchedParameterConnections;
	}

	public static Settings fromJS(Object object,JSONParser parser) {
		Settings settings = new Settings();
		settings.maxAttemptCount =  parser.propertyNoCheckInt(object, JSON_MAX_ATTEMPT);
		settings.minimalSeparation =  parser.propertyNoCheckInt(object, JSON_MIN_SEPARATION);
		settings.oneWayConnectionsOnly = parser.propertyNoCheckBoolean(object, JSON_ONE_WAY_ONLY);
		settings.bookAuthors = parser.propertyNoCheckString(object, JSON_BOOK_AUTHORS);
		settings.bookDescription = parser.propertyNoCheckString(object, JSON_BOOK_DESCRIPTION);
		settings.bookTitle = parser.propertyNoCheckString(object, JSON_BOOK_TITLE);
		settings.hiddenUsingObjects = parser.propertyNoCheckBoolean(object, JSON_HIDDEN_USING_OBJECTS);
		settings.playerListType = parser.propertyNoCheckInt(object, JSON_PLAYER_LIST_TYPE);
		settings.textColor = parser.propertyNoCheckInt(object, JSON_PLAYER_TEXT_COLOR);
		settings.bagColor = parser.propertyNoCheckInt(object, JSON_PLAYER_BAG_COLOR);
		settings.applicationColor = parser.propertyNoCheckInt(object, JSON_PLAYER_APP_COLOR);
		settings.textBackground = parser.propertyNoCheckInt(object, JSON_PLAYER_TEXT_BACKGROUND);
		settings.disableAudio = parser.propertyNoCheckBoolean(object, JSON_PLAYER_DISABLE_AUDIO);
		settings.disableImages = parser.propertyNoCheckBoolean(object, JSON_PLAYER_DISABLE_IMAGES);
		settings.gameVersion = parser.propertyNoCheckInt(object, JSON_GAME_VERSION);
		settings.showAboutOnStart = parser.propertyNoCheckInt(object, JSON_PLAYER_SHOW_ABOUT)>0;
		settings.showParagraphNumbers = parser.propertyNoCheckInt(object, JSON_SHOW_PARAGRAPH_NUMBERS)>0;
		settings.fineSecretKeys = parser.propertyNoCheckInt(object, JSON_FINE_SECRET_KEYS);
		settings.maxDimensionX = parser.propertyNoCheckInt(object, JSON_MAX_DIMENSION_X);
		settings.maxDimensionY = parser.propertyNoCheckInt(object, JSON_MAX_DIMENSION_Y);
		settings.feedbackEmail = parser.propertyNoCheckString(object, JSON_FEEDBACK_EMAIL);
		settings.showModificators = parser.propertyNoCheckInt(object, JSON_SHOW_MODIFICATORS) != 0;
		settings.showBattleConsole = parser.propertyNoCheckInt(object, JSON_SHOW_BATTLE_CONSOLE) != 0;
		settings.hideNonMatchedParameterConnections =  parser.propertyNoCheckInt(object, JSON_HIDE_NON_MATHED_PARAMETER_CONNECTIOS) != 0;
		settings.addAlchemyToText = parser.propertyNoCheckInt(object, JSON_ADD_ALCHEMY_TO_TEXT) == 0;
		settings.addModificatorNamesToText  = parser.propertyNoCheckInt(object, JSON_ADD_MODIFICATOR_NAMES_TO_TEXT) == 0;
		settings.showConnectionsIDs  = parser.propertyNoCheckInt(object, JSON_SHOW_CONNECTION_IDS) == 1;
		settings.skipMustGoParagraphs = parser.propertyNoCheckInt(object, JSON_SKIP_MUST_GO) == 1;
		settings.showConnectionNames = parser.propertyNoCheckInt(object, JSON_SHOW_CONNECTION_NAMES) == 1;
		settings.showConnectionType = parser.propertyNoCheckInt(object, JSON_SHOW_CONNECTION_TYPE);
		Object obj = parser.propertyNoCheck(object, JSON_GREETINGS);
		settings.demoVersion = parser.propertyNoCheckInt(object, JSON_DEMO_VERSION) == 1;
		if (obj == null) {
			settings.greetings.clear();
		} else {
			settings.greetings = Greeting.fromJSArray(obj,parser);
		} 
		settings.verticalObjects = parser.propertyNoCheckInt(object, JSON_VERTICAL_OBJECTS) == 1;
		settings.overflowControl = parser.propertyNoCheckInt(object, JSON_OVERFLOW_CONTROL) == 1;
		return settings;
	}

	public void toJSON(JSONBuilder builder,int export) {
		builder.newRow();
		builder.field(JSON_BOOK_AUTHORS, bookAuthors);
		builder.field(JSON_BOOK_TITLE, bookTitle);
		builder.field(JSON_BOOK_DESCRIPTION, bookDescription);
		builder.field(JSON_HIDDEN_USING_OBJECTS, hiddenUsingObjects);
		builder.field(JSON_PLAYER_LIST_TYPE, playerListType);
		builder.field(JSON_PLAYER_TEXT_COLOR, textColor);
		builder.field(JSON_PLAYER_BAG_COLOR, bagColor);
		builder.field(JSON_PLAYER_APP_COLOR, applicationColor);
		builder.field(JSON_PLAYER_TEXT_BACKGROUND, textBackground);
		builder.field(JSON_PLAYER_DISABLE_AUDIO, disableAudio);
		builder.field(JSON_PLAYER_DISABLE_IMAGES, disableImages);
		builder.field(JSON_GAME_VERSION, gameVersion);
		builder.field(JSON_PLAYER_SHOW_ABOUT, showAboutOnStart ? 1:0);
		if (overflowControl) {
			builder.field(JSON_OVERFLOW_CONTROL, 1);
		}
		if (skipMustGoParagraphs) {
			builder.field(JSON_SKIP_MUST_GO, 1);
		}
		if (showModificators) {
			builder.field(JSON_SHOW_MODIFICATORS, 1);
		}
		if (hideNonMatchedParameterConnections){
			builder.field(JSON_HIDE_NON_MATHED_PARAMETER_CONNECTIOS, 1);
		}
		if (feedbackEmail != null && feedbackEmail.length()>0) {
			builder.field(JSON_FEEDBACK_EMAIL, feedbackEmail);
		}
		if (showBattleConsole) {
			builder.field(JSON_SHOW_BATTLE_CONSOLE,1);
		}
		if (addAlchemyToText==false) {
			builder.field(JSON_ADD_ALCHEMY_TO_TEXT,1);
		}
		if (demoVersion) {
			builder.field(JSON_DEMO_VERSION,1);
		}
		if (export==Model.EXPORT_ALL) {
			builder.field(JSON_ONE_WAY_ONLY, oneWayConnectionsOnly);
			builder.field(JSON_MAX_ATTEMPT, maxAttemptCount);
			builder.field(JSON_MIN_SEPARATION, minimalSeparation);
			builder.field(JSON_SHOW_PARAGRAPH_NUMBERS, showParagraphNumbers? 1:0);
			builder.field(JSON_MAX_DIMENSION_X, getMaxDimensionX());
			builder.field(JSON_MAX_DIMENSION_Y, getMaxDimensionY());
			builder.field(JSON_FINE_SECRET_KEYS, fineSecretKeys);
			if (addModificatorNamesToText==false) {
				builder.field(JSON_ADD_MODIFICATOR_NAMES_TO_TEXT,1);
			}
			if (showConnectionsIDs) {
				builder.field(JSON_SHOW_CONNECTION_IDS,1);
			}
			if (showConnectionNames) {
				builder.field(JSON_SHOW_CONNECTION_NAMES,1);
			}
		}
		builder.field(JSON_SHOW_CONNECTION_TYPE, showConnectionType);
		if (greetings.size()>0) {
			JSONBuilder subBuilder = builder.getInstance();
			for (int i = 0; i < greetings.size(); i++) {
				Greeting greeting = greetings.get(i);
				greeting.toJSON(subBuilder);
			}
			builder.childArray(JSON_GREETINGS, subBuilder);
		}
		if (verticalObjects) {
			builder.field(JSON_VERTICAL_OBJECTS,1);
		}
	}

	public boolean isOneWayConnectionsOnly() {
		return oneWayConnectionsOnly;
	}

	public void setOneWayConnectionsOnly(boolean oneWayConnectionsOnly) {
		this.oneWayConnectionsOnly = oneWayConnectionsOnly;
	}

	public boolean isHiddenUsingObjects() {
		return hiddenUsingObjects;
	}

	public void setHiddenUsingObjects(boolean hiddenUsingObjects) {
		this.hiddenUsingObjects = hiddenUsingObjects;
	}

	public int getPlayerListType() {
		return playerListType;
	}

	public void setPlayerListType(int playerListType) {
		this.playerListType = playerListType;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getBagColor() {
		return bagColor;
	}

	public void setBagColor(int bagColor) {
		this.bagColor = bagColor;
	}

	public int getApplicationColor() {
		return applicationColor;
	}

	public void setApplicationColor(int applicationColor) {
		this.applicationColor = applicationColor;
	}

	public int getTextBackground() {
		return textBackground;
	}

	public void setTextBackground(int textBackground) {
		this.textBackground = textBackground;
	}

	public boolean isDisableAudio() {
		return disableAudio;
	}

	public void setDisableAudio(boolean disableAudio) {
		this.disableAudio = disableAudio;
	}

	public boolean isDisableImages() {
		return disableImages;
	}

	public void setDisableImages(boolean disableImages) {
		this.disableImages = disableImages;
	}

	public int getGameVersion() {
		return gameVersion;
	}

	public void setGameVersion(int gameVersion) {
		this.gameVersion = gameVersion;
	}

	public boolean isShowAboutOnStart() {
		return showAboutOnStart;
	}

	public void setShowAboutOnStart(boolean showAboutOnStart) {
		this.showAboutOnStart = showAboutOnStart;
	}

	public ArrayList<Greeting> getGreetings() {
		return greetings;
	}

	public void setGreetings(ArrayList<Greeting> greetings) {
		this.greetings = greetings;
	}

	public void setFineSecretKeys(int fineSecretKeys) {
		this.fineSecretKeys = fineSecretKeys;
	}

	public int getFineSecretKeys() {
		return fineSecretKeys;
	}

	public boolean isShowModificators() {
		return showModificators;
	}

	public void setShowModificators(boolean showModificators) {
		this.showModificators = showModificators;
	}

	public boolean isAddModificatorNamesToText() {
		return addModificatorNamesToText;
	}

	public void setAddModificatorNamesToText(boolean addModificatorNamesToText) {
		this.addModificatorNamesToText = addModificatorNamesToText;
	}

	public boolean isDemoVersion() {
		return demoVersion;
	}

	public void setDemoVersion(boolean demoVersion) {
		this.demoVersion = demoVersion;
	}



}
