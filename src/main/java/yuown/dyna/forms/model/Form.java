package yuown.dyna.forms.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "form")
public class Form extends GenericDTO {

    private static final long serialVersionUID = -553013682929268934L;

    private String name;

    private List<Field> fields;

    private List<Group> groups;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElementWrapper(name = "fields")
    @XmlElements({ @XmlElement(name = "field", type = Field.class) })
    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    @XmlElementWrapper(name = "groups")
    @XmlElements({ @XmlElement(name = "group", type = Group.class) })
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    @Override
    public int hashCode() {
        return (new HashCodeBuilder()).append(this.name).append(this.fields).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Form)) {
            return false;
        }
        Form rhs = (Form) obj;
        return (new EqualsBuilder()).append(this.name, rhs.name).append(this.fields, rhs.fields).isEquals();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
