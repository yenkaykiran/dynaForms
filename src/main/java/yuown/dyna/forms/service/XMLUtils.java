package yuown.dyna.forms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

@Component
public class XMLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(XMLUtils.class);
    private static final String JAXB_NO_HEADER_PROPERTY = "jaxb.fragment";

    public <T> T getJAXBObjectFromXMLResource(Class<T> clazz, String xmlResource) {
        T retValue = null;
        try {
            retValue = getJAXBObjectFromXMLStream(clazz, XMLUtils.class.getResourceAsStream(xmlResource));
        } catch (Exception e) {
            LOG.error("Error Unmarshalling XML - getJAXBObjectFromXMLResource(). XML Data:{}", xmlResource, e);
        }
        return retValue;
    }

    public <T> T getJAXBObjectFromXMLString(Class<T> clazz, String xmlString) {
        T retValue = null;
        try {
            retValue = getJAXBObjectFromXMLStream(clazz, new ByteArrayInputStream(xmlString.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            LOG.error("Error Unmarshalling XML - getJAXBObjectFromXMLString(). XML Data:{}", xmlString, e);
        } catch (Exception e) {
            LOG.error("Error Unmarshalling XML - getJAXBObjectFromXMLString(). XML Data:{}", xmlString, e);
        }
        return retValue;
    }

    public <T> String generateXMLStringWithHeaderFromJAXBObject(T object) {
        return generateXMLStringFromJAXBObject(object, false);
    }

    public <T> String generateXMLStringWithoutHeaderFromJAXBOjbect(T object) {
        return generateXMLStringFromJAXBObject(object, true);
    }

    @SuppressWarnings({ "unchecked", "restriction" })
    private <T> T getJAXBObjectFromXMLStream(Class<T> clazz, InputStream configStream) throws Exception {
        T returnObject = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unMarshaller = jaxbContext.createUnmarshaller();
            returnObject = (T) unMarshaller.unmarshal(configStream);
        } catch (UnmarshalException ue) {
            if (com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException.class.equals((ue.getLinkedException()).getClass())) {
                throw new Exception("Error while parsing invalid XML data stream. Reason", ue);
            } else {
                LOG.error("Error while Unmarshalling XML data stream. Reason: {}", ue.toString(), ue);
                throw new Exception(ue);
            }
        } catch (JAXBException e) {
            LOG.error("Error while Unmarshalling XML data stream. Reason: {}", e.toString(), e);
            throw new Exception(e);
        }
        return returnObject;
    }

    private static <T> String generateXMLStringFromJAXBObject(T object, boolean disableXMLHeader) {
        String xml = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            if (disableXMLHeader) {
                marshaller.setProperty(JAXB_NO_HEADER_PROPERTY, Boolean.TRUE);
            }
            StringWriter sw = new StringWriter();
            marshaller.marshal(object, sw);
            xml = sw.toString();
        } catch (Exception e) {
            LOG.error("Exception while generating XML from JAXB object: {}. Reason: {}", object, e.toString(), e);
        }
        return xml;
    }
}
