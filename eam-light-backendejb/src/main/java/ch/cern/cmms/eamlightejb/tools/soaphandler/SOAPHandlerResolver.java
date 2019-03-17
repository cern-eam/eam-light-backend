package ch.cern.cmms.eamlightejb.tools.soaphandler;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.ArrayList;
import java.util.List;

public class SOAPHandlerResolver implements HandlerResolver {
	@SuppressWarnings("rawtypes")
	@Override
	public List<Handler> getHandlerChain(PortInfo portInfo) {
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new NSEraserHandler());
		handlerChain.add(new WSLoggingHandler());
		return handlerChain;
	}
}