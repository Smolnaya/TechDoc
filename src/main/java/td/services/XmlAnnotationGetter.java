package td.services;

import org.apache.xmlbeans.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlAnnotationGetter {
    public static String getAnnotationDocumentation(SchemaAnnotation an) {
        if (null != an) {
            StringBuilder sb = new StringBuilder();
            XmlObject[] userInformation = an.getUserInformation();
            if (userInformation != null && userInformation.length > 0) {
                for (XmlObject obj : userInformation) {
                    Node docInfo = obj.getDomNode();
                    NodeList list = docInfo.getChildNodes();
                    for (int i = 0; i < list.getLength(); i++) {
                        Node c = list.item(i);
                        if (c.getNodeType() == Node.TEXT_NODE) {
                            String str = c.getNodeValue();
                            sb.append(str.trim());
                            break;
                        }
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }
}
