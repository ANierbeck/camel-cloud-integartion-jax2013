/**
 * 
 */
package de.inovex.jax2013.showcase.wsendpoint;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author anierbeck
 *
 */
@WebService
public interface MessageEndpoint {
	public Boolean storeMessage(@WebParam(name="message") Message message );
}
