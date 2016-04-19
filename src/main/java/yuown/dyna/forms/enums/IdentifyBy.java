package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum IdentifyBy {

    Id, Class, Name, TagName, CssSelector, Link, XPath, PartialText;
    
    @JsonValue
	public Integer value() {
		return ordinal();
	}

}
