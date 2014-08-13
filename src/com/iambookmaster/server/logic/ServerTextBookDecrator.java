package com.iambookmaster.server.logic;

import java.io.UnsupportedEncodingException;

import com.iambookmaster.client.locale.AppConstants;
import com.iambookmaster.client.locale.AppMessages;
import com.iambookmaster.client.model.Model;
import com.iambookmaster.client.paragraph.TextBookDecrator;

public class ServerTextBookDecrator extends TextBookDecrator {

	public static final String ENCODING = "UTF-8";

	public ServerTextBookDecrator(Model mod, AppConstants appConstants, AppMessages appMessages) {
		super(mod, appConstants, appMessages);
	}

	public byte[] toBytes() {
		try {
			return buffer.toString().getBytes(ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
}
