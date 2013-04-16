package de.inovex.jax2013.showcase.cxf.consumer;

import org.apache.camel.Exchange;
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
						"Received Message ${body}")
				.process(new Processor() {
					
					@Override
					public void process(Exchange exchange) throws Exception {
						Message message = exchange.getIn().getBody(Message.class);
						exchange.getIn().setBody(message.getMessage(), String.class);
					}
				})
				.to(ShowcaseDefaults.HAZELCAST_QUEUE);
	}

}
