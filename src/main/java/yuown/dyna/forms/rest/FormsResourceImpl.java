package yuown.dyna.forms.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.parsers.DOMParser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.util.Map;

@RestController
@RequestMapping(value = "/forms", produces = { MediaType.APPLICATION_JSON_VALUE })
public class FormsResourceImpl {

    @RequestMapping(method = RequestMethod.POST, value = "/html", produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public ResponseEntity<String> getHtml(@RequestBody Map<String, String> request) {
        HttpHeaders headers = new HttpHeaders();
        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new java.io.StringReader(request.get("xml"))));
            Document doc = parser.getDocument();
            Element element = doc.getDocumentElement();
            String content = extractHtmlFromXml(element);
            return new ResponseEntity<String>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @RequestMapping(method = RequestMethod.POST, value = "/nodes", produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<yuown.dyna.forms.model.Node> getHtmlNodes(@RequestBody Map<String, String> request) {
        HttpHeaders headers = new HttpHeaders();
        yuown.dyna.forms.model.Node node = null;
        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new java.io.StringReader(request.get("xml"))));
            Document doc = parser.getDocument();
            Element element = doc.getDocumentElement();
            node = extractNodesFromXml(element);
            return new ResponseEntity<yuown.dyna.forms.model.Node>(node, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<yuown.dyna.forms.model.Node>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
                    content += "</span>";
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
            attributeContent += "</span>";
        }
        return attributeContent;
    }

    private yuown.dyna.forms.model.Node extractNodesFromXml(Element element) {
        yuown.dyna.forms.model.Node n = new yuown.dyna.forms.model.Node();
        n.setContainer(true);
        n.setTitle(element.getNodeName());
        n.addNode(getAttributesNodes(element.getAttributes()));
        n.addNode(getContentNodes(element.getChildNodes()));
        return n;
    }

    private yuown.dyna.forms.model.Node getAttributesNodes(NamedNodeMap attributes) {
        yuown.dyna.forms.model.Node atts = new yuown.dyna.forms.model.Node();
        for (int i = 0; i < attributes.getLength(); i++) {
            yuown.dyna.forms.model.Node a = new yuown.dyna.forms.model.Node();
            a.setTitle(attributes.item(i).getNodeName());
            a.setContainer(false);
            a.setValue(attributes.item(i).getNodeValue());
            atts.addNode(a);
        }
        return atts;
    }

    private yuown.dyna.forms.model.Node getContentNodes(NodeList nodes) {
        yuown.dyna.forms.model.Node content = new yuown.dyna.forms.model.Node();
        int nodeCount = nodes.getLength();
        for (int i = 0; i < nodeCount; i++) {
            Node node = nodes.item(i);
            yuown.dyna.forms.model.Node n = new yuown.dyna.forms.model.Node();
            if (node.getChildNodes().getLength() > 0) {
                if (node.getChildNodes().getLength() > 1) {
                    n.setContainer(true);
                    n.setTitle(node.getNodeName());
                }
                n.addNode(getAttributesNodes(node.getAttributes()));
                n.addNode(getContentNodes(node.getChildNodes()));
                content.addNode(n);
            } else {
                if (StringUtils.isNotBlank(node.getTextContent())) {
                    n.setTitle(node.getParentNode().getNodeName());
                    n.setValue(node.getTextContent());
                    n.setContainer(false);
                    content.addNode(n);
                }
            }
        }
        return content;
    }
}
