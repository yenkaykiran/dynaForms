package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WaitType {

    NONE, UNTIL_CLICKABLE, ELEMENT_AVAILABLE;
    
    @JsonValue
	public Integer value() {
		return ordinal();
	}

}
