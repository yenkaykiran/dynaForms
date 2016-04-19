package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BrowserType {

	Chrome("webdriver.chrome.driver"), Firefox("f"), InternetExplorer("ie"), Safari("s");

	private String driverProperty;

	private BrowserType(String p) {
		this.setDriverProperty(p);
	}

	public String getDriverProperty() {
		return driverProperty;
	}

	public void setDriverProperty(String driverProperty) {
		this.driverProperty = driverProperty;
	}

	@JsonValue
	public Integer value() {
		return ordinal();
	}
}
