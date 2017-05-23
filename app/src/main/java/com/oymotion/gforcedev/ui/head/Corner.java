package com.oymotion.gforcedev.ui.head;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Corner {
	int TOP_LEFT = 0;
	int TOP_RIGHT = 1;
	int BOTTOM_RIGHT = 2;
	int BOTTOM_LEFT = 3;
}
