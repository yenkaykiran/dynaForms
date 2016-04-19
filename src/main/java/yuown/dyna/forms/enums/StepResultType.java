package yuown.dyna.forms.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StepResultType {

    FINISHED, EXECUTION_FAILED, IN_PROGRESS, SKIPPED, VERIFY_PASSED, VERIFY_FAILED, WAIT_FAILED;

    @JsonValue
    public Integer value() {
        return ordinal();
    }

}
