package ch.cern.cmms.eamlightejb.tools.soaphandler;

import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

import javax.enterprise.context.Dependent;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

@Dependent
public class WSLoggingHandler implements SOAPHandler<SOAPMessageContext> {

	Pattern regex = null;

	public WSLoggingHandler() {
		regex = Pattern.compile("(<([a-zA-Z0-9]+:)?password>)([\\s\\S]*?)(</([a-zA-Z0-9]+:)?password>)", Pattern.CASE_INSENSITIVE);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		String soapMessage  = soapMessageToString(context.getMessage());
		Logger.getLogger("wshublogger").log(Level.DEBUG, regex.matcher(soapMessage).replaceAll("$1*********$4"));
		try {
			Iterator<SOAPElement> it = context.getMessage().getSOAPBody().getChildElements();
			
			while (it.hasNext()) {
				SOAPElement se = it.next();
				if (se.getElementName().getLocalName().equalsIgnoreCase("InformationAlert")) {
					se.detachNode();
				}
			}
		} catch (Exception e) {

		}
		return true;
	}

	private String soapMessageToString(SOAPMessage message) 
	{
		String result = null;

		if (message != null) 
		{
			ByteArrayOutputStream baos = null;
			try 
			{
				baos = new ByteArrayOutputStream();
				message.writeTo(baos); 
				result = baos.toString();
			} 
			catch (Exception e) 
			{
			} 
			finally 
			{
				if (baos != null) 
				{
					try 
					{
						baos.close();
					} 
					catch (IOException ioe) 
					{
					}
				}
			}
		}
		return result;
	}   

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		String soapMessage  = soapMessageToString(context.getMessage());
		Logger.getLogger("wshublogger").log(Level.DEBUG, regex.matcher(soapMessage).replaceAll("$1*********$4"));
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

