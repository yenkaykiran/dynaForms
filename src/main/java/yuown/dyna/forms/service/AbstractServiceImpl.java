package yuown.dyna.forms.service;

import yuown.dyna.forms.model.entity.BaseEntity;
import yuown.dyna.forms.repository.BaseRepository;

import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Transactional
public abstract class AbstractServiceImpl<ID extends Serializable, E extends BaseEntity<ID>, R extends BaseRepository<E, ID>> {

    public abstract R repository();

    public E save(E entity) {
        return repository().save(entity);
    }

    public List<E> findAll() {
        return repository().findAll();
    }

    public void delete(E entity) {
        repository().delete(entity);
    }

    public List<E> saveAll(List<E> entities) {
        return repository().save(entities);
    }

    public E findById(ID id) {
        return repository().findOne(id);
    }
}