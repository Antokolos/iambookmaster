package com.iambookmaster.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.StackPanel;
import com.iambookmaster.client.common.TrueStackPanelListener;
import com.iambookmaster.client.locale.AppLocale;

public class TrueStackPanel extends StackPanel {
	private static final String HEADER_NORMAL_STYLE = "notes_panel_header";
	private static final String HEADER_SELECTED_STYLE = "notes_panel_header_sel";
	private static final String HEADER_HIGHLIGHT_STYLE = "notes_panel_header_highlight";
	
	private TrueStackPanelListener listener;
	
	public TrueStackPanel() {
		setSize("100%", "100%");
	}

	//not the best solution...but Google does the same
    public void onBrowserEvent(Event event) {
    	switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
	    	int oldIndex = getSelectedIndex();
        	try {
				super.onBrowserEvent(event);
			} catch (Exception e) {
				//it causes NullPointerException on FF3.x
			}
        	int newIndex = getSelectedIndex();
        	if (oldIndex != newIndex) {
        		selectHeader(oldIndex, false);
        		selectHeader(newIndex, true);
        	}
        	if (listener != null) {
        		listener.activate(newIndex);
        	}
		case Event.ONMOUSEOVER:
			hightlightHeader(event,true);
			break;
		case Event.ONMOUSEOUT:
			hightlightHeader(event,false);
			break;
	    } 
    }
    
    public void setStackHeader(int row, String name, String icon_url, String title) {
    	setStackHeader(row,name,icon_url,title,null);
    }
    public void setStackHeader(int row, String name, String icon_url, String title,EventListener sortListener) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<div class=\"notes_panel_header_left\">");
		buffer.append("<div class=\"notes_panel_header_right\" >");
		buffer.append("<div class=\"notes_panel_header_inside\">");
		buffer.append("<img src=\"");
		buffer.append(icon_url);
		buffer.append("\"/>");
		buffer.append(name);
		if (sortListener != null) {
			buffer.append("<img src=\"");
			buffer.append(Images.SORT);
			buffer.append("\" title=\"");
			buffer.append(AppLocale.getAppConstants().sort());
			buffer.append("\"/>");
		}
		buffer.append("</div></div></div>");
		
		setStackText(row, buffer.toString(), true);
		Element wrapper = getHeaderWrapper(row);
		if (sortListener != null) {
			Element tdWrapper = DOM.getChild(wrapper, 0);
			tdWrapper = DOM.getChild(tdWrapper,0);
			tdWrapper = DOM.getChild(tdWrapper,0);
			tdWrapper = DOM.getChild(tdWrapper,0);
			tdWrapper = DOM.getChild(tdWrapper,1);
			DOM.sinkEvents(tdWrapper,Event.ONCLICK);
			DOM.setEventListener(tdWrapper, sortListener);
		}
		DOM.setStyleAttribute(wrapper, "height", "20px");
		setTitle(row, title);
		
		selectHeader(row, false);
	}

	private Element getHeaderElement(int index) {
    	Element tdWrapper = getHeaderWrapper(index);
    	return DOM.getFirstChild(tdWrapper);
    }
    
	private Element getHeaderWrapper(int index) {
    	Element element = DOM.getChild(getElement(),0);
    	return DOM.getChild(DOM.getChild(element, index * 2), 0);
    }
    
//	private Element getBodyWrapper(int index) {
//    	Element element = DOM.getChild(getElement(),0);
//    	return DOM.getChild(DOM.getChild(element, index * 2+1), 0);
//    }
    
    private void setHeaderStyle(int index,String style) {
    	Element header = getHeaderElement(index);
		DOM.setElementProperty(header, "className",style);
    }
    
    private void hightlightHeader(Event event, boolean highlight) {
    	int index = findDividerIndex(DOM.eventGetTarget(event));
    	if (index >=0 && index != getSelectedIndex()) {
    		if (highlight) {
    			setHeaderStyle(index,HEADER_HIGHLIGHT_STYLE);
    		} else {
    			setHeaderStyle(index, HEADER_NORMAL_STYLE);
    		}
    	}
	}

    public void selectHeader(int index, boolean selected) {
		if (selected) {
			setHeaderStyle(index,HEADER_SELECTED_STYLE);
		} else {
			setHeaderStyle(index, HEADER_NORMAL_STYLE);
		}
	}

    public void setTitle(int index,String title) {
    	Element header = getHeaderElement(index);
		if (title == null || title.length() == 0) {
			DOM.removeElementAttribute(header, "title");
        } else {
        	DOM.setElementAttribute(header, "title", title);
        }
    }
    
    /**
	 * This is a copy of private method in com.google.gwt.user.client.ui.StackPanel
	 * @param elem
	 * @return
	 */
    private int findDividerIndex(Element elem) {
        while (elem != null && elem != getElement()) {
        	String expando = DOM.getElementProperty(elem, "__index");
        	if (expando != null) {
        		// Make sure it belongs to me!
        		int ownerHash = DOM.getElementPropertyInt(elem, "__owner");
        		if (ownerHash == hashCode()) {
        			// Yes, it's mine.
        			return Integer.parseInt(expando);
        		} else {
        			// It must belong to some nested StackPanel.
        			return -1;
        		}
        	}
        	elem = DOM.getParent(elem);
        }
        return -1;
      }

	public TrueStackPanelListener getListener() {
		return listener;
	}

	public void setListener(TrueStackPanelListener listener) {
		this.listener = listener;
	}

}
