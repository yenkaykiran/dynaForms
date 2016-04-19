package yuown.dyna.forms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import yuown.dyna.forms.model.entity.Forms;
import yuown.dyna.forms.repository.FormsRepository;

@Service
public class FormsService extends AbstractServiceImpl<Integer, Forms, FormsRepository> {

    @Autowired
    private FormsRepository formsRepository;

    @Override
    public FormsRepository repository() {
        return formsRepository;
    }
}
