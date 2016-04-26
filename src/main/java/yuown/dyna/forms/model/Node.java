package yuown.dyna.forms.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable, Cloneable {

    private List<Node> nodes = new ArrayList<Node>();

    private boolean container = false;
    
    private boolean attribute = false;

    private String title;

    private String value;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public void addNodes(List<Node> nodes) {
        for (Node node : nodes) {
            try {
                this.nodes.add((Node)node.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isContainer() {
        return container;
    }

    public boolean getContainer() {
        return container;
    }

    public void setContainer(boolean container) {
        this.container = container;
    }

    public boolean isAttribute() {
        return attribute;
    }
    
    public boolean getAttribute() {
        return attribute;
    }

    public void setAttribute(boolean attribute) {
        this.attribute = attribute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
