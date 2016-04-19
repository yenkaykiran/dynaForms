package yuown.dyna.forms.model;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@XmlRootElement(name = "field")
public class Field implements Serializable {

    private static final long serialVersionUID = -7045528090822840108L;

    private String name;

    private List<Attribute> attributes;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "attributes")
    @XmlElements({ @XmlElement(name = "attribute", type = Attribute.class) })
    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public int hashCode() {
        return (new HashCodeBuilder()).append(this.name).append(this.attributes).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Field)) {
            return false;
        }
        Field rhs = (Field) obj;
        return (new EqualsBuilder()).append(this.name, rhs.name).append(this.attributes, rhs.attributes).isEquals();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
