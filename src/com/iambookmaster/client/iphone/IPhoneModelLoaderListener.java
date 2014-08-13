package com.iambookmaster.client.iphone;

import com.iambookmaster.client.model.Model;

public interface IPhoneModelLoaderListener {
	
	void success(Model model);

	void error(Throwable throwable);
}
