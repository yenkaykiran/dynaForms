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

import java.util.ArrayList;
import java.util.List;
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

    @RequestMapping(method = RequestMethod.POST, value = "/xml", produces = { MediaType.TEXT_HTML_VALUE })
    @ResponseBody
    public ResponseEntity<String> getXmlFromNodes(@RequestBody yuown.dyna.forms.model.Node node) {
        HttpHeaders headers = new HttpHeaders();
        try {
            String xml = extractXmlFromNode(node);
            return new ResponseEntity<String>(xml, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
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
        n.addNodes(getAttributesNodes(element.getAttributes()));
        n.addNodes(getContentNodes(element));
        return n;
    }

    private List<yuown.dyna.forms.model.Node> getAttributesNodes(NamedNodeMap attributes) {
        List<yuown.dyna.forms.model.Node> atts = new ArrayList<yuown.dyna.forms.model.Node>();
        if (null != attributes) {
            for (int i = 0; i < attributes.getLength(); i++) {
                yuown.dyna.forms.model.Node a = new yuown.dyna.forms.model.Node();
                a.setTitle(attributes.item(i).getNodeName());
                a.setContainer(false);
                a.setAttribute(true);
                a.setValue(attributes.item(i).getNodeValue());
                atts.add(a);
            }
        }
        return atts;
    }

    private List<yuown.dyna.forms.model.Node> getContentNodes(Node element) {
        List<yuown.dyna.forms.model.Node> content = new ArrayList<yuown.dyna.forms.model.Node>();
        NodeList list = element.getChildNodes();
        if (list != null && list.getLength() > 0) {
            int nodeCount = list.getLength();
            for (int i = 0; i < nodeCount; i++) {
                Node node = list.item(i);
                if (!StringUtils.equals(node.getNodeName(), "#text")) {
                    yuown.dyna.forms.model.Node n = new yuown.dyna.forms.model.Node();
                    n.setTitle(node.getNodeName());
                    n.setValue(node.getTextContent());
                    n.addNodes(getAttributesNodes(node.getAttributes()));
                    if (node.getChildNodes().getLength() > 0) {
                    	if (node.getChildNodes().getLength() > 1) {
	                        n.setContainer(true);
	                        n.setValue(null);
	                        n.addNodes(getContentNodes(node));
                    	}
                    }
                    content.add(n);
                }
            }
        }
        return content;
    }

    private String extractXmlFromNode(yuown.dyna.forms.model.Node node) {
        String main = "<" + node.getTitle() + " " + getNodeAttributes(node) + ">" + "\n";
        main += getNodeContents(node) + "\n";
        main += "</" + node.getTitle() + ">";
        return main;
    }

    private String getNodeContents(yuown.dyna.forms.model.Node node) {
        String content = "";
        List<yuown.dyna.forms.model.Node> subs = node.getNodes();
        if (null != subs && subs.size() > 0) {
            for (int i = 0; i < subs.size(); i++) {
                yuown.dyna.forms.model.Node sub = subs.get(i);
                if (sub.getContainer() == true) {
                	content += "<" + sub.getTitle() + " " + getNodeAttributes(sub) + ">" + "\n";
                    content += getNodeContents(sub) + "\n";
                    content += "</" + sub.getTitle() + ">";
                } else {
                	if(sub.getAttribute() == false) {
	                	content += "<" + sub.getTitle() + " " + getNodeAttributes(sub) + ">" + "\n";
	                    content += sub.getValue();
	                    content += "</" + sub.getTitle() + ">";
                	}
                }
            }
        }
        return content;
    }

    private String getNodeAttributes(yuown.dyna.forms.model.Node node) {
        String attributeContent = "";
        List<yuown.dyna.forms.model.Node> atts = node.getNodes();
        if (null != atts && atts.size() > 0) {
            for (int i = 0; i < atts.size(); i++) {
                if (atts.get(i).getAttribute() == true) {
                    attributeContent += atts.get(i).getTitle() + "='" + atts.get(i).getValue() + "' ";
                }
            }
        }
        return attributeContent.trim();
    }
}
