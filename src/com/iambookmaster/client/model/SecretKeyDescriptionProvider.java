package com.iambookmaster.client.model;

import com.iambookmaster.client.beans.ObjectBean;

public interface SecretKeyDescriptionProvider {

	String getObjectSecretKey(ObjectBean object);

}
