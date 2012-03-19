package org.openstack.api.imagestore;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.Target;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.FilterContext;
import javax.ws.rs.ext.RequestFilter;

import org.openstack.api.OpenStackSession2;
import org.openstack.api.common.Resource;
import org.openstack.model.exceptions.OpenstackException;
import org.openstack.model.exceptions.OpenstackNotFoundException;
import org.openstack.model.image.GlanceImage;

public class ImageResource extends Resource {
	
	public ImageResource(Target target) {
		super(target);
	}
	
    public void put(Map<String, Object> properties, Map<String, Object> metadata) {
    	Builder b = target.request(MediaType.APPLICATION_JSON);
        b = GlanceHeaderUtils.setHeadersForProperties(b, metadata);
        b = b.header("X-Auth-Token", properties.get("X-Auth-Token"));
        b.method("PUT");
    }

    public GlanceImage head(Map<String, Object> p) throws OpenstackException {
        Response response = target.request().header("X-Auth-Token", p.get("X-Auth-Token")).head();
        int httpStatus = response.getStatus();
        if (httpStatus == 200) {
            //GlanceImage image = GlanceHeaderUtils.unmarshalHeaders(response);
            //return image;
        	return null;
        }

        if (httpStatus == 404) {
            throw new OpenstackNotFoundException("Image not found");
        }

        throw new OpenstackException("Unexpected HTTP status code: " + httpStatus);
    }

	public InputStream openStream() {
        return target.request().get(InputStream.class);
    }

    public void delete(Map<String, Object> properties) {
        target.request().header("X-Auth-Token", properties.get("X-Auth-Token"));
    }

	public static ImageResource endpoint(Client client, String endpoint) {
		return new ImageResource(client. target(endpoint));
	}
	
	public void setSession(final OpenStackSession2 session) {
		target.configuration().register(new RequestFilter() {
			
			@Override
			public void preFilter(FilterContext context) throws IOException {
				context.getRequestBuilder().header("X-Auth-Token", session.getAccess().getToken().getId());
				
			}
		});
	}
}
