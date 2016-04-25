package yuown.dyna.forms.model.entity;

import java.io.Serializable;

public abstract class BaseEntity<ID extends Serializable> {

    protected ID id;
    private boolean enabled;

    public BaseEntity() {
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract ID getId();

    public abstract void setId(ID id);

}
