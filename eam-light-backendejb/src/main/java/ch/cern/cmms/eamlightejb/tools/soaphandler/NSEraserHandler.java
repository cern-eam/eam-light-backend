package ch.cern.cmms.eamlightejb.tools.soaphandler;

import javax.enterprise.context.Dependent;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Remove redundant namespaces in the Infor WS requests
 */
@Dependent
public class NSEraserHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		if (outbound) {
			try {
				// Clean SOAP Header
				@SuppressWarnings("unchecked")
				Iterator<SOAPElement> headerIter = context.getMessage().getSOAPPart().getEnvelope().getHeader().getChildElements();
				while (headerIter.hasNext()) {
					cleanSOAPElement(headerIter.next());	
				}
				// Clean SOAP Body (only the first element)
				cleanSOAPElement((SOAPElement) context.getMessage().getSOAPPart().getEnvelope().getBody().getChildElements().next());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Problem: " + e.getMessage() + e.getClass().getName());
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private void cleanSOAPElement(SOAPElement soapElement) {
		String headerNSPrefix = soapElement.getElementName().getPrefix();
		Iterator<String> namespaceIT = soapElement.getNamespacePrefixes();
		List<String> result = new ArrayList<String>();
		while (namespaceIT.hasNext()){
		    result.add(namespaceIT.next());
		}
		// Remove 
		result.stream().filter(ns -> !ns.equals(headerNSPrefix)).forEach(ns -> soapElement.removeNamespaceDeclaration(ns));
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
}

