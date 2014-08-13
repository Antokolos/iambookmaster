package com.iambookmaster.client.iphone;

public interface IPhoneViewListener {

	void redraw(IPhoneCanvas viewer);

//	void horizontalTouch(int delta);

	void back();

	void forward();

	void drawn();

	void click(int x, int y);
}
