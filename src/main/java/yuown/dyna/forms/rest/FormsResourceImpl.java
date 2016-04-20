package yuown.dyna.forms.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.xerces.parsers.DOMParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import yuown.dyna.forms.model.Form;
import yuown.dyna.forms.model.entity.Forms;
import yuown.dyna.forms.service.FormsService;
import yuown.dyna.forms.service.XMLUtils;

@RestController
@RequestMapping(value = "/forms", produces = { MediaType.APPLICATION_JSON_VALUE })
public class FormsResourceImpl {

	@Autowired
	private FormsService formsService;

	@Autowired
	private XMLUtils xmlUtils;

	public FormsService getService() {
		return formsService;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<Form> save(@RequestBody Form entity, @Context HttpServletRequest httpRequest) {
		HttpHeaders headers = new HttpHeaders();
		HttpStatus responseStatus = null;
		try {
			Forms f = new Forms();
			if (null != entity.getId()) {
				f.setId(entity.getId());
			}
			f.setName(entity.getName());
			f.setTemplate(xmlUtils.generateXMLStringWithHeaderFromJAXBObject(entity));
			f = getService().save(f);
			entity = xmlUtils.getJAXBObjectFromXMLString(Form.class, f.getTemplate());
			responseStatus = HttpStatus.OK;
		} catch (Exception e) {
			responseStatus = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Form>(entity, headers, responseStatus);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	@ResponseBody
	public Form getById(@PathVariable("id") Integer id) {
		Forms form = getService().findById(id);
		Form f = xmlUtils.getJAXBObjectFromXMLString(Form.class, form.getTemplate());
		return f;
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<String> removeById(@PathVariable("id") Integer id) {
		Forms form = getService().findById(id);
		HttpHeaders headers = new HttpHeaders();
		if (null == form) {
			headers.add("errorMessage", "Entity with ID " + id + " Not Found");
			return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
		} else {
			try {
				getService().delete(form);
				return new ResponseEntity<String>(headers, HttpStatus.OK);
			} catch (Exception e) {
				headers.add("errorMessage", "Entity with ID " + id + " cannot be Deleted");
				return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Form> getAll() {
		List<Forms> db = getService().findAll();
		List<Form> all = new ArrayList<Form>();
		for (Forms form : db) {
			Form f = xmlUtils.getJAXBObjectFromXMLString(Form.class, form.getTemplate());
			f.setId(form.getId());
			all.add(f);
		}
		return all;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE }, value = "/xml")
	@ResponseBody
	public void saveXml(@RequestBody Forms entity, @Context HttpServletRequest httpRequest) {
		HttpHeaders headers = new HttpHeaders();
		HttpStatus responseStatus = null;
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(new java.io.StringReader(entity.getTemplate())));
			Document doc = parser.getDocument();
			String message = doc.getDocumentElement().getTextContent();
			System.out.println(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
