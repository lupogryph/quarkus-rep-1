package lupogryph.rep.quarkus;

import io.smallrye.common.vertx.ContextLocals;
import jakarta.ws.rs.container.ContainerRequestContext;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

public class IncomingContextMapper {

    @ServerRequestFilter(preMatching = true)
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        var cid = ContextLocals.get("CORRELATION_ID");
        if (cid.isEmpty()) {
            ContextLocals.put("CORRELATION_ID", requestContext.getHeaderString("Correlation-Id"));
        }
    }

}
