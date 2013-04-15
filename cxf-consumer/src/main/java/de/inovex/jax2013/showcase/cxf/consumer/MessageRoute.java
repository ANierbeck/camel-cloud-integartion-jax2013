package de.inovex.jax2013.showcase.cxf.consumer;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import de.inovex.jax2013.showcase.defaults.ShowcaseDefaults;

public class MessageRoute extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		from("cxf:bean:messageService")
				.routeId(ShowcaseDefaults.MESSAGE_ROUTE_ID)
				.log(LoggingLevel.WARN, ShowcaseDefaults.MESSAGE_LOGGER,
						"Received Message ${body}")
				.to(ShowcaseDefaults.HAZELCAST_QUEUE);
	}

}
