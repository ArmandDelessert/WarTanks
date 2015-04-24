package navalbattle.datamodel;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DataModelEntity {
    public abstract Node getXMLRepresentation(Document doc);
    public abstract DataModelEntity parse(Node message) throws InvalidModelException;
}