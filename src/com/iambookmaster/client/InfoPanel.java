package com.iambookmaster.client;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.beans.AbstractParameter;
import com.iambookmaster.client.beans.Alchemy;
import com.iambookmaster.client.beans.Battle;
import com.iambookmaster.client.beans.Modificator;
import com.iambookmaster.client.beans.NPC;
import com.iambookmaster.client.beans.ObjectBean;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.beans.ParagraphConnection;
import com.iambookmaster.client.beans.Parameter;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.beans.Sound;
import com.iambookmaster.client.common.EditorTab;
import com.iambookmaster.client.common.ScrollContainer;
import com.iambookmaster.client.common.SpanLabel;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;

public class InfoPanel extends VerticalPanel implements EditorTab{
	
	private Model model;
	private final AppConstants appConstants = AppLocale.getAppConstants();
	private boolean activationNeed;
	private Label type;
	private Label name;
	private Button update;
	private ScrollContainer scrollContainer;
	private VerticalPanel info;
	private Object item;
	
	public void activate() {
		if (activationNeed) {
			activationNeed = false;
			onResize();
		}
	}
	
	private void onResize() {
		scrollContainer.resetHeight();
	}
	
	public void deactivate() {
	}
	
	public void activateLater() {
		activationNeed = true;
	}
	
	public InfoPanel(Model mod) {
		model=mod;
		setSize("100%", "100%");
		setStyleName("location_editor_panel");
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(5);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setSize("100%", "100%");
		type = new Label();
		type.setWordWrap(false);
		panel.add(type);
		panel.setCellWidth(type,"1%");
		name = new Label();
		name.setStyleName(Styles.BOLD);
		name.setWordWrap(false);
		panel.add(name);
		panel.setCellWidth(name,"99%");
		update = new Button(appConstants.refreshButton(),new ClickHandler(){
			public void onClick(ClickEvent event) {
				refresh();
			}
		});
		panel.add(update);
		panel.setCellWidth(update,"1%");
		add(panel);
		setCellWidth(panel,"100%");
		setCellHeight(panel,"1%");
		scrollContainer = new ScrollContainer();
		info = new VerticalPanel();
		info.setSize("100%", "100%");
		scrollContainer.setScrollWidget(info);
		add(scrollContainer);
		setCellWidth(scrollContainer,"100%");
		setCellHeight(scrollContainer,"99%");
	}
	
	public void close() {
	}

	public void showInfo(ObjectBean object) {
		type.setText(appConstants.infoPanelObject());
		name.setText(object.getName());
		item = object;
		refresh();
	}

	public void showInfo(Picture object) {
		type.setText(appConstants.infoPanelPicture());
		name.setText(object.getName());
		item = object;
		refresh();
	}

	public void showInfo(Sound object) {
		type.setText(appConstants.infoPanelSound());
		name.setText(object.getName());
		item = object;
		refresh();
	}

	public void showInfo(AbstractParameter parameter) {
		type.setText(getAbstractParameterTypeName(parameter));
		name.setText(parameter.getName());
		item = parameter;
		refresh();
	}
	
	private String getAbstractParameterTypeName(AbstractParameter parameter) {
		if (parameter instanceof Alchemy) {
			return appConstants.menuNewAlchemy();
		} else if (parameter instanceof Parameter) {
			return appConstants.menuNewParameter();
		} else if (parameter instanceof Battle) {
			return appConstants.menuNewBattle();
		} else if (parameter instanceof NPC) {
			return appConstants.menuNewNPC();
		} else if (parameter instanceof Modificator) {
			return appConstants.menuNewModificator();
		} else {
			return "???";
		}
	}
	
	private String getAbstractParameterTypeImageURL(AbstractParameter parameter) {
		if (parameter instanceof Alchemy) {
			return Images.PARAMETER_CONVERTER;
		} else if (parameter instanceof Parameter) {
			return Images.PARAMETER_ICON;
		} else if (parameter instanceof Battle) {
			return Images.BATTLE_ICON;
		} else if (parameter instanceof NPC) {
			return Images.NPC_ICON;
		} else if (parameter instanceof Modificator) {
			return Images.MODIFICATOR_ICON;
		} else {
			return null;
		}
	}

	private void refresh() {
		info.clear();
		if (item instanceof ObjectBean) {
			updateObject((ObjectBean) item);
		}
		if (item instanceof Picture) {
			updatePicture((Picture) item);
		}
		if (item instanceof Sound) {
			updateSound((Sound) item);
		}
		if (item instanceof AbstractParameter) {
			updateAbstractParameter((AbstractParameter) item);
		}
	}
	
	private void updateAbstractParameter(AbstractParameter parameter) {
		ArrayList<Paragraph> paragraphs = model.getParagraphs();
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.dependsOn(parameter)) {
				ParagraphWidget widget = new ParagraphWidget(paragraph,true);
				info.add(widget);
				info.setCellWidth(widget,"100%");
			}
		}
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		for (ParagraphConnection connection : connections) {
			if (connection.dependsOn(parameter)) {
				FlowPanel panel = new FlowPanel();
				panel.setWidth("100%");
				ParagraphWidget from = new ParagraphWidget(connection.getFrom(),true);
				ParagraphWidget to = new ParagraphWidget(connection.getTo(),true);
				Image img = new Image(Images.RIGHT_GREEN);
				panel.add(from);
				panel.add(img);
				panel.add(to);
				info.add(panel);
				info.setCellWidth(panel,"100%");
			}
		}
		ArrayList<AbstractParameter> params = model.getParameters();
		for (AbstractParameter abstractParameter : params) {
			if (abstractParameter.dependsOn(parameter)) {
				FlowPanel panel = new FlowPanel();
				panel.add(new Image(getAbstractParameterTypeImageURL(abstractParameter)));
				SpanLabel label = new SpanLabel(getAbstractParameterTypeName(abstractParameter));
				label.addStyleName(Styles.MARGINE_RIGHT);
				panel.add(label);
				label = new SpanLabel(abstractParameter.getName());
				label.addStyleName(Styles.BOLD);
				panel.add(label);
				panel.setWidth("100%");
				info.add(panel);
				info.setCellWidth(panel,"100%");
			}
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		info.add(html);
		info.setCellHeight(html,"100%");
		info.setCellWidth(html,"100%");
	}

	private void updateSound(Sound sound) {
		VerticalPanel usedPanel = new VerticalPanel();
		usedPanel.setSpacing(5);
		usedPanel.setWidth("100%");
		ArrayList<Paragraph> paragraphs = model.getParagraphs();
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.getBackgroundSounds().contains(sound) ||
				paragraph.getSounds().contains(sound)) {
				ParagraphWidget widget = new ParagraphWidget(paragraph,true);
				usedPanel.add(widget);
				usedPanel.setCellWidth(widget,"100%");
			}
		}
		if (usedPanel.getWidgetCount()>0) {
			Label title = new Label(appConstants.infoPanelSoundUsed());
			info.add(title);
			info.setCellHeight(title,"1%");
			info.setCellWidth(title,"100%");
			usedPanel.setStyleName(Styles.BORDER);
			info.add(usedPanel);
			info.setCellHeight(usedPanel,"1%");
			info.setCellWidth(usedPanel,"100%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		info.add(html);
		info.setCellHeight(html,"100%");
		info.setCellWidth(html,"100%");
	}

	private void updatePicture(Picture picture) {
		VerticalPanel usedPanel = new VerticalPanel();
		usedPanel.setSpacing(5);
		usedPanel.setWidth("100%");
		ArrayList<Paragraph> paragraphs = model.getParagraphs();
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.getBackgroundImages().contains(picture) ||
				paragraph.getBottomImages().contains(picture) ||
				paragraph.getTopImages().contains(picture)) {
				ParagraphWidget widget = new ParagraphWidget(paragraph,true);
				usedPanel.add(widget);
				usedPanel.setCellWidth(widget,"100%");
			}
		}
		if (usedPanel.getWidgetCount()>0) {
			Label title = new Label(appConstants.infoPanelImagesUsed());
			info.add(title);
			info.setCellHeight(title,"1%");
			info.setCellWidth(title,"100%");
			usedPanel.setStyleName(Styles.BORDER);
			info.add(usedPanel);
			info.setCellHeight(usedPanel,"1%");
			info.setCellWidth(usedPanel,"100%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		info.add(html);
		info.setCellHeight(html,"100%");
		info.setCellWidth(html,"100%");
	}

	private void updateObject(ObjectBean object) {
		VerticalPanel gotPanel = new VerticalPanel();
		gotPanel.setSpacing(5);
		gotPanel.setWidth("100%");
		VerticalPanel lostPanel = new VerticalPanel();
		lostPanel.setWidth("100%");
		lostPanel.setSpacing(5);
		FlexTable usedPanel = new FlexTable();
		usedPanel.setWidth("100%");
		ArrayList<Paragraph> paragraphs = model.getParagraphs();
		for (Paragraph paragraph : paragraphs) {
			if (paragraph.getGotObjects().contains(object)) {
				ParagraphWidget widget = new ParagraphWidget(paragraph,false);
				gotPanel.add(widget);
				gotPanel.setCellWidth(widget,"100%");
			}
			if (paragraph.getLostObjects().contains(object)) {
				ParagraphWidget widget = new ParagraphWidget(paragraph,false);
				lostPanel.add(widget);
				lostPanel.setCellWidth(widget,"100%");
			}
		}
		ArrayList<ParagraphConnection> connections = model.getParagraphConnections();
		int i=0;
		for (ParagraphConnection connection : connections) {
			if (connection.getObject()==object) {
				ParagraphWidget from = new ParagraphWidget(connection.getFrom(),true);
				ParagraphWidget to = new ParagraphWidget(connection.getTo(),true);
				Image img = new Image(Images.RIGHT_GREEN);
				i = usedPanel.insertRow(i);
				usedPanel.addCell(i);
				usedPanel.setWidget(i,0,from);
				usedPanel.addCell(i);
				usedPanel.setWidget(i,1,img);
				usedPanel.addCell(i);
				usedPanel.setWidget(i,2,to);
				i++;
			}
		}
		if (gotPanel.getWidgetCount()>0) {
			Label title = new Label(appConstants.infoPanelGotObject());
			info.add(title);
			info.setCellHeight(title,"1%");
			info.setCellWidth(title,"100%");
			gotPanel.setStyleName(Styles.BORDER);
			info.add(gotPanel);
			info.setCellHeight(gotPanel,"1%");
			info.setCellWidth(gotPanel,"100%");
		}
		if (lostPanel.getWidgetCount()>0) {
			Label title = new Label(appConstants.infoPanelLostObject());
			info.add(title);
			info.setCellHeight(title,"1%");
			info.setCellWidth(title,"100%");
			lostPanel.setStyleName(Styles.BORDER);
			info.add(lostPanel);
			info.setCellHeight(lostPanel,"1%");
			info.setCellWidth(lostPanel,"100%");
		}
		if (usedPanel.getRowCount()>0) {
			Label title = new Label(appConstants.infoPanelUsedObject());
			info.add(title);
			info.setCellHeight(title,"1%");
			info.setCellWidth(title,"100%");
			usedPanel.setStyleName(Styles.BORDER);
			usedPanel.getColumnFormatter().setWidth(0, "49%");
			usedPanel.getColumnFormatter().setWidth(1, "1%");
			usedPanel.getColumnFormatter().setWidth(2, "50%");
			info.add(usedPanel);
			info.setCellHeight(usedPanel,"1%");
			info.setCellWidth(usedPanel,"100%");
		}
		HTML html = new HTML("&nbsp;");
		html.setStyleName(Styles.FILLER);
		info.add(html);
		info.setCellHeight(html,"100%");
		info.setCellWidth(html,"100%");
	}

	public class ParagraphWidget extends SpanLabel {
		public ParagraphWidget(final Paragraph paragraph,final boolean openForEdit) {
			super((model.getSettings().isShowParagraphNumbers() ? String.valueOf(paragraph.getNumber())+" ":"")+paragraph.getName());
			setStyleName(Styles.CLICKABLE);
			addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					if (openForEdit) {
						model.editParagraph(paragraph, null);
					} else {
						model.selectParagraph(paragraph, null);
					}
				}
			});
		}
		
	}


}
