package com.iambookmaster.client.iphone;

import java.util.Iterator;

import com.google.code.gwt.database.client.service.DataServiceException;
import com.google.code.gwt.database.client.service.ScalarCallback;
import com.google.code.gwt.database.client.service.VoidCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.iambookmaster.client.beans.Picture;
import com.iambookmaster.client.iphone.common.IPhoneButton;
import com.iambookmaster.client.iphone.data.IPhoneDataService;
import com.iambookmaster.client.iphone.images.IPhoneImages;
import com.iambookmaster.client.iphone.images.IPhoneStyles;
import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppLocale;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;

public class IPhoneThankyouPanel {

	private static final AppConstants appConstants = AppLocale.getAppConstants();
	private static final AppMessages appMessages = AppLocale.getAppMessages();
	private static final IPhoneStyles css = IPhoneImages.INSTANCE.css();
	private static final int MODE_GET_PRODUCTS = 0;
	private static final int MODE_GOT_PRICE =  1;
	private static final int MODE_DONATION = 2;
	private static final int MODE_THANKS = 3;
	protected static final int MODE_PRICE_ERROR = 4;
	
	private Model model;
	private IPhoneThankyouPanelListener owner;
	private IPhoneCanvas canvas;
	private ClickHandler back;
	private IPhoneViewListenerAdapter listener;
	private IPhoneDataService ds;
	private int mode=MODE_GET_PRODUCTS;
	private String priceValue;
	private ClickHandler donateHandler;
	private ScalarCallback<String> priceCallback;
	private VoidCallback donateCallback;
	private String errorMessage;
	private ClickHandler tryAgain;
	private boolean performAction;
	private Timer timer;
	private Image titleImage;
	private LoadHandler imageLoadHandler;
	private ErrorHandler imageErrorHandler;
	
	public IPhoneThankyouPanel(Model md, IPhoneDataService dbService) {
		this.model = md;
		this.ds = dbService;
		listener = new IPhoneViewListenerAdapter(){

			@Override
			public void back() {
			}

			@Override
			public void redraw(IPhoneCanvas viewer) {
				draw(viewer,false,false);
			}

			
			@Override
			public void forward() {
			}

			@Override
			public void drawn() {
				if (performAction) {
					performAction = false;
					switch (mode) {
					case MODE_GET_PRODUCTS:
						ds.calculateDonate(priceCallback);
						break;
					case MODE_GOT_PRICE:
						break;
					case MODE_DONATION:
						ds.donate(donateCallback);
						break;
					case MODE_THANKS:
						break;
					case MODE_PRICE_ERROR:
						break;
					}
				}
			}

		};
		imageLoadHandler = new LoadHandler() {
			public void onLoad(LoadEvent event) {
				titleImage.setVisible(true);
				int w = canvas.getClientWidth() - titleImage.getWidth();
				if (w>20) {
					titleImage.getElement().getStyle().setMarginLeft((w-20)/2, Unit.PX);
				} else {
					titleImage.getElement().getStyle().setMarginLeft(0, Unit.PX);
				}
			}
		};
		imageErrorHandler = new ErrorHandler() {
			public void onError(ErrorEvent event) {
				titleImage.setVisible(false);
			}
		};
		priceCallback = new ScalarCallback<String>() {
			public void onFailure(DataServiceException error) {
				processError(error);
				mode = MODE_PRICE_ERROR;
				draw(canvas,false,false);
			}
			public void onSuccess(String result) {
				errorMessage = null;
				priceValue = result;
				mode = MODE_GOT_PRICE;
				draw(canvas,false,false);
			}
		};
		donateCallback = new VoidCallback() {
			public void onFailure(DataServiceException error) {
				processError(error);
				mode = MODE_GOT_PRICE;
				draw(canvas,false,false);
			}
			public void onSuccess() {
				errorMessage = null;
				mode = MODE_THANKS;
				draw(canvas,false,false);
			}
		};
		back = new ClickHandler() {
			public void onClick(ClickEvent event) {
				ds.cancelRequest();
				timer.cancel();
				owner.close();
			}
		};
		donateHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				mode = MODE_DONATION;
				performAction = true;
				draw(canvas,false,false);
			}
		};
		tryAgain = new ClickHandler() {
			public void onClick(ClickEvent event) {
				mode = MODE_GET_PRODUCTS;
				performAction = true;
				draw(canvas,true,false);
			}
		};
		timer = new Timer() {
			Iterator<Picture> iterator=model.getPictures().iterator();
			@Override
			public void run() {
				if (iterator.hasNext()==false) {
					iterator=model.getPictures().iterator();
				}
				if (titleImage != null && iterator.hasNext()) {
					if (mode==MODE_THANKS) {
						//show all images
						titleImage.setUrl(iterator.next().getUrl());
					} else {
						//show fillers only
						Picture picture;
						do {
							picture = iterator.next();
						} while (picture.isFiller()==false && iterator.hasNext());
						if (picture.isFiller()) {
							titleImage.setUrl(picture.getUrl());
						}
					}
				}
			}
		};
		timer.scheduleRepeating(3000);
	}
	
	private void processError(DataServiceException error) {
		switch (error.getCode()) {
		case -1:
			errorMessage = appConstants.iphoneCannotConnectAppStore();
			break;
		case -2:
			errorMessage = appConstants.iphoneThankyouErrorNoItem();
			break;
		default:
			errorMessage = error.getMessage();
			break;
		}
	}

	private void draw(IPhoneCanvas canvas,boolean animate,boolean leftToRight) {
		this.canvas = canvas;
		canvas.setListener(listener);
		if (animate) {
			canvas.clearWithAnimation(leftToRight);
		} else {
			canvas.clear();
		}
//		if (mode == MODE_THANKS) {
//			//thanks
//		} else if (mode != MODE_GOT_PRICE){
//			//connection
//			FlowPanel panel = new FlowPanel();
//			panel.setWidth("100%");
//			Image image = new Image(IPhoneImages.IPHONE);
//			panel.add(image);
//			image = new Image(IPhoneImages.SERVER);
//			panel.add(image);
//			canvas.add(panel);
//		}
		Label label;
		label = new Label(model.getSettings().getBookTitle());
		label.setStyleName(css.bookName());
		canvas.add(label);
		label = new Label(model.getSettings().getBookAuthors());
		label.setStyleName(css.bookAuthors());
		canvas.add(label);
		
		switch (mode) {
		case MODE_GET_PRODUCTS:
			//getting list of products from iTunes
			label = new Label(appConstants.iphoneThankyouRetrievingPrice());
			label.setStyleName(css.thankyouTitle());
			canvas.add(label);
			break;
		case MODE_GOT_PRICE:
			if (errorMessage == null) {
				label = new Label(appConstants.iphoneThankyouGotPrice());
				label.setStyleName(css.thankyouTitle());
				label.addStyleName(css.thankyouBottom());
				canvas.add(label);
			} else {
				//we connected to App Store but could not pay (user canceled transaction?)
				label = new Label(errorMessage);
				label.setStyleName(css.thankyouTitleError());
				canvas.add(label);
				label = new Label(appConstants.iphoneThankyouDonateError());
				label.setStyleName(css.thankyouBottom());
				canvas.add(label);
			}
			addButton(canvas,appMessages.iphoneDonate(priceValue),donateHandler);
			break;
		case MODE_DONATION:
			//processing the donation
			label = new Label(appConstants.iphoneThankyouPaying());
			label.setStyleName(css.thankyouTitle());
			label.addStyleName(css.thankyouBottom());
			canvas.add(label);
			break;
		case MODE_THANKS:
			//donation was successful
			createImage(canvas);
			label = new Label(appConstants.iphoneThankyouForDonation());
			label.setStyleName(css.thankyouTitle());
			label.addStyleName(css.thankyouBottom());
			canvas.add(label);
			break;
		case MODE_PRICE_ERROR:
			//error connection to the server
			label = new Label(errorMessage);
			label.setStyleName(css.thankyouTitleError());
			canvas.add(label);
			
			label = new Label(appConstants.iphoneThankyouDonateError());
			label.setStyleName(css.thankyouBottom());
			canvas.add(label);
			
			addButton(canvas, appConstants.iphoneThankyouTryAgain(),tryAgain);
			break;
		}
		addButton(canvas, mode==MODE_THANKS ? appConstants.iphoneBack():appConstants.iphoneThankyouCancel(),back);
		if (mode==MODE_PRICE_ERROR || mode==MODE_GOT_PRICE && errorMessage != null) {
			titleImage = null;
		} else {
			createImage(canvas);
		}
		canvas.done();
	}

	private void createImage(IPhoneCanvas canvas) {
		titleImage = new Image();
		titleImage.setStyleName(css.thanksTitleImage());
		titleImage.addLoadHandler(imageLoadHandler);
		titleImage.addErrorHandler(imageErrorHandler);
		titleImage.setVisible(false);
		canvas.add(titleImage);
	}

	private void addButton(IPhoneCanvas canvas, String title, ClickHandler handler) {
		IPhoneButton button = new IPhoneButton(title,handler);
		canvas.add(button);
		canvas.addClickHandler(button, handler);
		if (canvas.isVertical()) {
			button.setWidth(IPhoneViewerOldBook.toPixels(canvas.getClientWidth()-30));
		}
	}
	public void show(IPhoneThankyouPanelListener owner, IPhoneCanvas canvas,boolean leftToRight) {
		this.owner = owner;
		performAction = true;
		mode=MODE_GET_PRODUCTS;
		draw(canvas,true,leftToRight);
	}

}
