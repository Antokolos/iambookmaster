package com.iambookmaster.client.quick;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.iambookmaster.client.Images;
import com.iambookmaster.client.Styles;
import com.iambookmaster.client.beans.Paragraph;
import com.iambookmaster.client.common.CompactHorizontalPanel;
import com.iambookmaster.client.common.NumberTextBox;
import com.iambookmaster.client.common.StatusPicker;
import com.iambookmaster.client.editor.ModelPersist;
import com.iambookmaster.client.editor.ObjectsList;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.model.ParagraphListener;

public class QuickViewParagraphEditor extends VerticalPanel implements QuickViewWidget {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	
	private Paragraph paragraph;
	private ModelPersist model;
	private ParagraphListener paragraphListener;
	private TextBox name;
	private NumberTextBox number;
	private TextArea description;
	private ObjectsList gotObjects;
	private ObjectsList lostObjects;
	private Image regenerate;
	private Image edit;
	private Image validate;
	private StatusPicker status;

	private CheckBox commercial;
	public QuickViewParagraphEditor(Model mod, Paragraph loc) {
		this.model = (ModelPersist)mod;
		setSize("100%", "100%");
		ClickHandler clickListener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource()==regenerate) {
					if (paragraph.getDescription().trim().length()==0 || Window.confirm(appConstants.regenerateParagraphTextConfirm())) {
						model.regenerateText(paragraph,Model.EXPORT_ALL);
					}
				} else if (event.getSource()==edit) {
					model.editParagraph(paragraph, paragraphListener);
				} else if (event.getSource()==commercial) {
					paragraph.setCommercial(commercial.getValue());
					model.updateParagraph(paragraph, paragraphListener);
				} else if (event.getSource()==validate) {
					ArrayList<String> errors = new ArrayList<String>();
					model.getFullParagraphDescripton(paragraph, null, errors);
					if (errors.size()>0) {
						StringBuffer buffer = new StringBuffer();
						buffer.append(appConstants.quickParagraphErrors());
						for (int i = 0; i < errors.size(); i++) {
							buffer.append('\n');
							buffer.append(errors.get(i));
						}
						Window.alert(buffer.toString());
					} else {
						Window.alert(appConstants.quickParagraphNoErrors());
					}
				}
			}
		};
		ChangeHandler changeListener  = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLocation(event.getSource());
			}
			
		};
		BlurHandler blurHandler  = new BlurHandler() {
			public void onBlur(BlurEvent event) {
				updateLocation(event.getSource());
			}
			
		};
		CompactHorizontalPanel horizontalPanel = new CompactHorizontalPanel();
		horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		horizontalPanel.addText(appConstants.quickParagraphN());
		horizontalPanel.setSpacing(3);
		number = new NumberTextBox();
		number.setVisibleLength(4);
		number.setRange(0,9999);
		number.setTitle(appConstants.quickParagraphNTitle());
		number.addBlurHandler(blurHandler);
		horizontalPanel.addCompactWidget(number);
		status = new StatusPicker();
		status.addChangeHandler(changeListener);
		horizontalPanel.addCompactWidget(status);
		edit = new Image(Images.EDIT);
		edit.setTitle(appConstants.quickParagraphEditTitle());
		edit.addClickHandler(clickListener);
		edit.setStyleName(Styles.CLICKABLE);
		horizontalPanel.addCompactWidget(edit);
		regenerate = new Image(Images.REGENERATE_TEXT);
		regenerate.setTitle(appConstants.quickParagraphRegenerateTextTitle());
		regenerate.addClickHandler(clickListener);
		regenerate.setStyleName(Styles.CLICKABLE);
		horizontalPanel.addCompactWidget(regenerate);
		validate = new Image(Images.VALIDATE);
		validate.setTitle(appConstants.quickParagraphValidate());
		validate.addClickHandler(clickListener);
		validate.setStyleName(Styles.CLICKABLE);
		horizontalPanel.addCompactWidget(validate);
		
		commercial = new CheckBox();
		commercial.setTitle(appConstants.quickParagraphCommercialTitle());
		commercial.addClickHandler(clickListener);
		horizontalPanel.addCompactWidget(commercial);
		Image image = new Image(Images.COMMERCIAL);
		image.setTitle(appConstants.quickParagraphCommercialTitle());
		horizontalPanel.addFullWidget(image);
		
		add(horizontalPanel);
		Grid grid = new Grid(3,2);
		grid.setSize("100%", "100%");
		KeyPressHandler keyboardListener  = new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode()==KeyCodes.KEY_ENTER) {
					updateLocation(event.getSource());
				} else if (event.getCharCode()==KeyCodes.KEY_ESCAPE) {
					open(paragraph);
				}
			}
		};
		int row=0;
		name = new TextBox();
		name.addBlurHandler(blurHandler);
		name.addKeyPressHandler(keyboardListener);
		grid.setWidget(row,0,new Label(appConstants.quickParagraph()));
		grid.setWidget(row,1,name);

		row++;
		gotObjects = new ObjectsList(mod);
		gotObjects.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				updateLocation(gotObjects);
			}
		});
		grid.setWidget(row,0,new Label(appConstants.paragraphFoundItems()));
		grid.setWidget(row,1,gotObjects);
		
		row++;
		lostObjects = new ObjectsList(mod);
		lostObjects.addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				updateLocation(lostObjects);
			}
		});
		grid.setWidget(row,0,new Label(appConstants.paragraphLostItems()));
		grid.setWidget(row,1,lostObjects);
		
		add(grid);
		setCellHeight(grid,"1%");
		setCellWidth(grid,"100%");
		description = new TextArea();
		description.setSize("100%","100%");
		description.addBlurHandler(blurHandler);
		add(description);
		setCellHeight(description,"99%");
		setCellWidth(description,"100%");
		
		paragraphListener = new ParagraphListener(){
			public void addNewParagraph(Paragraph location) {
			}
			public void edit(Paragraph location) {
			}
			public void refreshAll() {
				open(QuickViewParagraphEditor.this.paragraph);
			}
			public void select(Paragraph location) {
			}
			public void unselect(Paragraph location) {
			}
			public void update(Paragraph location) {
				if (location==QuickViewParagraphEditor.this.paragraph) {
					open(QuickViewParagraphEditor.this.paragraph);
				}
			}
			public void remove(Paragraph location) {
			}
		};
		model.addParagraphListener(paragraphListener);
		open(loc);
	}

	private void updateLocation(Object sender) {
		if (sender==name) {
			paragraph.setName(name.getText().trim());
		} else if (sender==description) {
			paragraph.setDescription(description.getText().trim());
		} else if (sender==number) {
			paragraph.setNumber(number.getIntegerValue());
		} else if (sender==gotObjects) {
			paragraph.setGotObjects(gotObjects.getSelectedObjects());
		} else if (sender==lostObjects) {
			paragraph.setLostObjects(lostObjects.getSelectedObjects());
		} else if (sender==status) {
			paragraph.setStatus(status.getSelectedIndex());
		}
		model.updateParagraph(paragraph, paragraphListener);
	}

	public void open(Paragraph location) {
		this.paragraph = location;
		name.setText(location.getName());
		description.setText(location.getDescription());
		gotObjects.setSelectedObjects(paragraph.getGotObjects());
		lostObjects.setSelectedObjects(paragraph.getLostObjects());
		status.setSelectedIndex(location.getStatus());
		number.setValue(location.getNumber());
		if (location.getType()==Paragraph.TYPE_START) {
			commercial.setValue(false);
			location.setCommercial(false);
			commercial.setEnabled(false);
		} else {
			commercial.setEnabled(true);
			commercial.setValue(location.isCommercial());
		}
	}
	
	public void close() {
		model.removeParagraphListener(paragraphListener);
	}

}
