package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionType {

	SendKeys, Click, MOVE_TO_ELEMENT;

	@JsonValue
	public Integer value() {
		return ordinal();
	}
}
