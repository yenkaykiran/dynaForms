package yuown.dyna.forms.rest;

import org.apache.commons.lang3.StringUtils;
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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import yuown.dyna.forms.model.entity.Forms;
import yuown.dyna.forms.service.FormsService;
import yuown.dyna.forms.service.XMLUtils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

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
    public ResponseEntity<Forms> save(@RequestBody Forms entity, @Context HttpServletRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus responseStatus = null;
        try {
            entity = getService().save(entity);
            responseStatus = HttpStatus.OK;
        } catch (Exception e) {
            headers.add("errorMessage", e.getMessage());
            responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<Forms>(entity, headers, responseStatus);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Forms getById(@PathVariable("id") Integer id) {
        return getService().findById(id);
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
    public List<Forms> getAll() {
        return getService().findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/html/{id}", produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public ResponseEntity<String> getHtml(@PathVariable("id") Integer id) {
        Forms form = getService().findById(id);
        HttpHeaders headers = new HttpHeaders();
        if (null == form) {
            headers.add("errorMessage", "Entity with ID " + id + " Not Found");
            return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
        } else {
            try {
                DOMParser parser = new DOMParser();
                parser.parse(new InputSource(new java.io.StringReader(form.getTemplate())));
                Document doc = parser.getDocument();
                Element element = doc.getDocumentElement();
                String content = extractHtmlFromXml(element);
                return new ResponseEntity<String>(content, headers, HttpStatus.OK);
            } catch (Exception e) {
                headers.add("errorMessage", "Entity with ID " + id + " cannot be Deleted");
                return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private String extractHtmlFromXml(Element element) {
        String main = "";
        main += "<div>";
        main += "<fieldset>";
        main += "<legend>" + element.getNodeName() + "</legend>";
        main += getAttributesContent(element.getAttributes());
        main += getContent(element.getChildNodes());
        main += "</fieldset>";
        main += "</div>";
        return main;
    }

    private String getContent(NodeList nodes) {
        String content = "";
        int nodeCount = nodes.getLength();
        for (int i = 0; i < nodeCount; i++) {
            Node node = nodes.item(i);
            if (node.getChildNodes().getLength() > 0) {
                if (node.getChildNodes().getLength() > 1) {
                    content += "<div>";
                    content += "<fieldset>";
                    content += "<legend>" + node.getNodeName() + "</legend>";
                }
                content += getAttributesContent(node.getAttributes());
                content += getContent(node.getChildNodes());
                if (node.getChildNodes().getLength() > 1) {
                    content += "</fieldset>";
                    content += "</div>";
                }
            } else {
                if (StringUtils.isNotBlank(node.getTextContent())) {
                    content += "<span>";
                    content += "<label>" + node.getParentNode().getNodeName() + ": </label>";
                    content += "<input type='text' value='" + node.getTextContent() + "' name='" + node.getParentNode().getNodeName() + "' />";
                    content += "</span><br />";
                }
            }
        }
        return content;
    }

    private String getAttributesContent(NamedNodeMap attributes) {
        String attributeContent = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            attributeContent += "<span>";
            attributeContent += "<label>" + attributes.item(i).getNodeName() + ": </label>";
            attributeContent += "<input type='text' value='" + attributes.item(i).getNodeValue() + "' name='" + attributes.item(i).getNodeName() + "' />";
            attributeContent += "</span><br />";
        }
        return attributeContent;
    }
}
