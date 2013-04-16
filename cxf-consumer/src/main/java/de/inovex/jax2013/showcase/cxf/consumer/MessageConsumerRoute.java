package de.inovex.jax2013.showcase.cxf.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import de.inovex.jax2013.showcase.defaults.ShowcaseDefaults;
import de.inovex.jax2013.showcase.wsendpoint.Message;

public class MessageConsumerRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("cxf:bean:messageService")
				.routeId(ShowcaseDefaults.CXF_CONSUMER_ROUTE_ID)
				.log(LoggingLevel.WARN, ShowcaseDefaults.MESSAGE_LOGGER,
						"Received Message!")
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						Message message = exchange.getIn().getBody(Message.class);
						exchange.getIn().setBody(message.getMessage(), String.class);
					}
				})
				.log(LoggingLevel.WARN, ShowcaseDefaults.MESSAGE_LOGGER,
						"Message: ${body}")
				.to(ShowcaseDefaults.HAZELCAST_QUEUE)
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						
						Exception exception = exchange.getException();
						Boolean rc = true;
						if (exception != null)
							rc = false;
						
						exchange.setPattern(ExchangePattern.InOut);
						exchange.getOut().setBody(rc, Boolean.class);
					}
				});
		
	}

}
