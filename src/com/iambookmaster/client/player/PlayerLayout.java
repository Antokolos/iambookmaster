package com.iambookmaster.client.player;

import com.google.gwt.dom.client.Element;



public interface PlayerLayout {
	
	String ABOUT_BUTTON = "iambookmaster_button_about";
	String TITLE = "iambookmaster_label_title";
	String AUTHORS = "iambookmaster_lable_autors";
	String HELP_BUTTON = "iambookmaster_button_help";
	String MAIN = "iambookmaster_player_main";
	String ITEMS_BUTTON = "iambookmaster_button_items";
	String SAVE_BUTTON = "iambookmaster_button_save";
	String LOAD_BUTTON = "iambookmaster_button_load";
	String RESTART_BUTTON = "iambookmaster_button_restart";
	String DISABLE_IMAGES_BUTTON = "iambookmaster_button_images";
	String DISABLE_SOUND_BUTTON = "iambookmaster_button_sounds";
	String FEEDBACK_BUTTON = "iambookmaster_button_feedback";
	String BAG = "iambookmaster_player_bag";
	String IMAGE_TOP = "iambookmaster_player_image_top";
	String IMAGE_BOTTOM = "iambookmaster_player_image_bottom";
	String IMAGE = "iambookmaster_player_image";
	
	String BUTTON_ON_STYLE = "ext_button_on";
	String BUTTON_OFF_STYLE = "ext_button_off";
	String BUTTON_DISABLED_STYLE = "ext_button_disabled";

	public Element getElement(String id);

	public void addStyle(String id, String style);
	
	public void removeStyle(String id, String style);
}
