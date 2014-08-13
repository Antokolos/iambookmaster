package com.iambookmaster.client.iphone;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.common.SpanHTML;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.model.ParagraphParsingHandler;
import com.iambookmaster.client.player.PlayerState;

public abstract class IPhoneParagraphTextGenerator implements ParagraphParsingHandler {
		
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	protected ArrayList<Link> links = new ArrayList<Link>();
	protected ArrayList<Widget> widgets = new ArrayList<Widget>();
	protected StringBuilder builder=new StringBuilder();
	private int counter;
	protected boolean mustGo;
	private Paragraph next;
	private final PlayerState playerState;
	
	public IPhoneParagraphTextGenerator(PlayerState playerState) {
		this.playerState = playerState;
	}

	public void addBattle(Battle battle, Paragraph paragraph) {
	}

	public void clear(Paragraph currentLocation) {
		builder.setLength(0);
		links.clear();
		counter = 1;
		widgets.clear();
		mustGo = false;
		next = currentLocation;
	}

	protected int getNextCounter() {
		return counter++;
	}

	public Paragraph getNext() {
		return next;
	}

	public boolean isMustGo() {
		return mustGo;
	}

	public void addLinkTo(Paragraph current, final Paragraph next, ParagraphConnection connection) {
		if (mustGo) {
			//no condition check anymore
			return;
		} 
		boolean meet = playerState.meetsCondition(connection);
		if (meet || playerState.alwaysVisible(connection)) {
			if (meet && connection.getStrictness()==ParagraphConnection.STRICTNESS_MUST) {
				mustGo = true;
				//disable all previous links
				for (Link link : links) {
					link.setEnabled(false);
				}
			}
			this.next = next;
			Link link = createTextLink(next,connection,meet);
			widgets.add(link);
			links.add(link);
		} 
	}

	protected abstract Link createTextLink(Paragraph next2,	ParagraphConnection connection, boolean hasBattle);

	public void addObject(Paragraph current,ObjectBean objectBean,String key) {
	}

	public void addAlchemyFromValue(Paragraph paragraph, String value) {
		builder.append(' ');
		builder.append(value);
		builder.append(' ');
		SpanLabel label = new SpanLabel(value);
		label.setStyleName(css.alchemyFromValue());
		widgets.add(label);
	}

	public void addText(Paragraph current,String text) {
		builder.append(text);
		int i = text.indexOf('\n');
		if (i<0) {
			SpanLabel label = new SpanLabel(text.replace('\r',' '));
			widgets.add(label);
		} else {
			int pos = 0;
			while (i>=0) {
				if (pos<i) {
					//add text
					SpanLabel label = new SpanLabel(text.substring(pos,i).replace('\r',' '));
					widgets.add(label);
					pos = i+1;
				} else {
					pos++;
				}
				HTML html = new HTML("<br/>");
				html.getElement().getStyle().setDisplay(Display.INLINE);
				widgets.add(html);
				if (pos<text.length() && text.charAt(pos)=='\r') {
					pos++;
				}
				i = text.indexOf('\n',pos); 
			}
			SpanLabel label = new SpanLabel(text.substring(pos).replace('\r',' '));
			widgets.add(label);
		}
	}

	public void addAlchemy(Paragraph paragraph, String value,Alchemy alchemy) {
		if (playerState.getModel().getSettings().isAddAlchemyToText()) {
			builder.append(' ');
			builder.append(value);
			builder.append(' ');
			SpanLabel label = new SpanLabel(value);
			label.setStyleName(css.alchemyFromValue());
			widgets.add(label);
		}
	}
	
	public String getCurrentText() {
		return builder.toString();
	}
	
	public static abstract class Link extends SpanHTML implements ClickHandler {
//		public abstract void setEnabled(boolean enabled);

		public void addBigWidget() {
		}

		public abstract void setEnabled(boolean enabled);

		public String getNextName() {
			return null;
		}

		public ParagraphConnection getConnection() {
			return null;
		}

	}
	
}

