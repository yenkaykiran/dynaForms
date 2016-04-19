package yuown.dyna.forms.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMetaResourceImpl<E extends Enum<E>> {

    private E type;
    
    public AbstractMetaResourceImpl() { }
    
    public AbstractMetaResourceImpl(E type) {
        this.type = type;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<String> listTypes() {
        Class<E> clazz = (Class<E>) getType().getClass();
        List<String> types = new ArrayList<String>();
        for (E option : clazz.getEnumConstants()) {
            types.add(option.toString());
        }
        return types;
    }

    public E getType() {
        return type;
    }
}