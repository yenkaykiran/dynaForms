package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProxyType {

	DIRECT, MANUAL, PAC, RESERVED_1, AUTODETECT, SYSTEM, UNSPECIFIED;

	@JsonValue
	public Integer value() {
		return ordinal();
	}
}
