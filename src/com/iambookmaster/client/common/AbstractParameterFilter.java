package com.iambookmaster.client.common;

public interface AbstractParameterFilter<T> {

	boolean match(T abstractParameter);

}
