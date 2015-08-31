package com.myclasses.xmlClasses;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;

/**
 * Created by amoiseev on 31.08.2015.
 */
public class XpathReader {

    private static final String YES="yes";
    private static final String NO="no";

    public XpathReader(){

    }

    public NodeList parse(Document doc, String expression) throws XPathExpressionException {
        XPath xPath =  XPathFactory.newInstance().newXPath();
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        return nodeList;
    }

    public  String getNodeAsString(Node node, String encoding, boolean setHeaderAndFormat)
    {
        if (node == null)
        {
            return null;
        }
        long duration=System.currentTimeMillis();
        removeTextNodes(node);
        StringWriter writer=new StringWriter(64000);
        try
        {
            Transformer transformer= TransformerFactory.newInstance().newTransformer();
            if (encoding != null)
            {
                transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
            }

            if (setHeaderAndFormat)
            {
                transformer.setOutputProperty(OutputKeys.INDENT, YES);
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, NO);
            }
            else
            {
                transformer.setOutputProperty(OutputKeys.INDENT, NO);
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, YES);
            }
            transformer.transform(new DOMSource(node), new StreamResult(writer));
            return writer.toString();
        }
        catch (TransformerException ex)
        {

            throw new IllegalArgumentException("Parser error!", ex);
        }
    }

    public  void removeTextNodes(Node nodeParent)
    {
        if (nodeParent == null)
        {
            return;
        }
        Node child, child2;
        child=nodeParent.getFirstChild();
        while (child != null)
        {
            if (child.getNodeType() == Node.ELEMENT_NODE)
            {
                removeTextNodes(child);
            }
            else
            if (child.getNodeType() == Node.TEXT_NODE)
            {
                // Remove only if it contains only spaces, tabs or line breaks
                String text=child.getTextContent().replace("\n", "").replace("\r", "").replace("\t", "").replace(" ", "");
                if (text.length() == 0)
                {
                    child2=child;
                    child=child.getNextSibling();
                    nodeParent.removeChild(child2);
                    continue;
                }
            }
            child=child.getNextSibling();
        }
    }

}
