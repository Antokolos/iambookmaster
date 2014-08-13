package com.iambookmaster.client.common;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TagCloudDropdown extends TextBox implements KeyboardListener, ChangeListener {
	   
	private PopupPanel choicesPopup = new PopupPanel(true);
	private ListBox choices = new ListBox();
	private ArrayList<String> items;
	private boolean popupAdded = false;
	private boolean visible = false;
	   
	  /**
	   * Default Constructor
	   *
	   */
	  public TagCloudDropdown(ArrayList<String> items)  {
		this.items = items;
	    this.addKeyboardListener(this);
	    choices.addChangeListener(this);
	    this.setStyleName("AutoCompleteTextBox");
	       
	    choicesPopup.add(choices);
	    choicesPopup.addStyleName("AutoCompleteChoices");
	       
	    choices.setStyleName("list");
	  }

	  /**
	   * Not used at all
	   */
	  public void onKeyDown(Widget arg0, char arg1, int arg2) {
	  }

	  /**
	   * Not used at all
	   */
	  public void onKeyPress(Widget arg0, char arg1, int arg2) {
	  }

	  /**
	   * A key was released, start autocompletion
	   */
	  public void onKeyUp(Widget arg0, char arg1, int arg2) {
	    if(arg1 == KEY_DOWN)
	    {
	      int selectedIndex = choices.getSelectedIndex();
	      selectedIndex++;
	      if(selectedIndex > choices.getItemCount())
	      {
	        selectedIndex = 0;
	      }
	      choices.setSelectedIndex(selectedIndex);
	           
	      return;
	    }
	       
	    if(arg1 == KEY_UP)
	    {
	      int selectedIndex = choices.getSelectedIndex();
	      selectedIndex--;
	      if(selectedIndex < 0)
	      {
	        selectedIndex = choices.getItemCount();
	      }
	      choices.setSelectedIndex(selectedIndex);
	           
	      return;        
	    }
	       
	    if(arg1 == KEY_ENTER)
	    {
	      if(visible)
	      {
	        complete();
	      }
	           
	      return;
	    }
	       
	    if(arg1 == KEY_ESCAPE)
	    {
	      choices.clear();
	      choicesPopup.hide();
	      visible = false;
	           
	      return;
	    }
	       
	    String text = this.getText();
	    boolean first=true;
	    if(text.length() > 2) {
	    	for (String tag : items) {
				if (tag.startsWith(text)) {
					if (first) {
					    choices.clear();
					    first = false;
					}
			        choices.addItem(tag);
				}
			}
	    }
      // if there is only one match and it is what is in the
      // text field anyways there is no need to show autocompletion
	    if (first == false) {
	        choices.setSelectedIndex(0);
	        choices.setVisibleItemCount(choices.getItemCount());
	        if(!popupAdded) {
	        	RootPanel.get().add(choicesPopup);
	        	popupAdded = true;
	        }
	        choicesPopup.show();
	        visible = true;
	        choicesPopup.setPopupPosition(this.getAbsoluteLeft(),
	        this.getAbsoluteTop() + this.getOffsetHeight());
	        //choicesPopup.setWidth(this.getOffsetWidth() + "px");
	        choices.setWidth(this.getOffsetWidth() + "px");
	    } else {
	    	visible = false;
	    	choicesPopup.hide();
	    }
	  }

	  /**
	   * A mouseclick in the list of items
	   */
	  public void onChange(Widget arg0) {
	    complete();
	  }
	 
	  public void onClick(Widget arg0) {
	    complete();
	  }
	   
	  // add selected item to textbox
	  protected void complete()
	  {
	    if(choices.getItemCount() > 0)
	    {
	      this.setText(choices.getItemText(choices.getSelectedIndex()));
	    }
	       
	    choices.clear();
	    choicesPopup.hide();
	  }
}
	 
