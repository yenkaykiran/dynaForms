package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NavigationType {

    Back, Next, Refresh;
    
    @JsonValue
	public Integer value() {
		return ordinal();
	}

}
