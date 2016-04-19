package yuown.dyna.forms.rest;

import yuown.dyna.forms.model.entity.BaseEntity;
import yuown.dyna.forms.repository.BaseRepository;
import yuown.dyna.forms.service.AbstractServiceImpl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

public abstract class AbstractResourceImpl<ID extends Serializable, E extends BaseEntity<ID>, R extends BaseRepository<E, ID>, S extends AbstractServiceImpl<ID, E, R>> {

    public abstract S getService();

    @RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<E> save(@RequestBody E entity, @Context HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus = null;
        try {
            entity = getService().save(entity);
            responseStatus = HttpStatus.OK;
        } catch (Exception e) {
            responseStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<E>(entity, headers, responseStatus);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public E getById(@PathVariable("id") ID id) {
        return getService().findById(id);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<String> removeById(@PathVariable("id") ID id) {
        E item = getService().findById(id);
        HttpHeaders headers = new HttpHeaders();
        if (null == item) {
            headers.add("errorMessage", "Entity with ID " + id + " Not Found");
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } else {
            try {
                getService().delete(item);
                return new ResponseEntity<String>(headers, HttpStatus.OK);
            } catch (Exception e) {
                headers.add("errorMessage", "Entity with ID " + id + " cannot be Deleted");
                return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<E> getAll() {
        return getService().findAll();
    }
}
