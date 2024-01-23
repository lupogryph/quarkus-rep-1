package lupogryph.rep.quarkus;

import io.smallrye.common.vertx.ContextLocals;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.ce.IncomingCloudEventMetadata;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Path("/hello")
public class GreetingResource {
    private static final Logger log = LoggerFactory.getLogger(GreetingResource.class);

    @Inject
    @Channel("words-out")
    Emitter<String> emitter;

    String cid;

    @Incoming("words-in")
    Uni<Void> consume(Message<String> message) {
        log.info("words-in : {}", message.getPayload());

        Optional<IncomingCloudEventMetadata> metadata = message.getMetadata(IncomingCloudEventMetadata.class);
        if (metadata.isPresent()) {
            Optional<String> optionalCid = metadata.get().getExtension("correlationid");
            optionalCid.ifPresent(inCid -> {
                cid = inCid;
            });
        }
        return Uni.createFrom().voidItem();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() throws InterruptedException {
        var message = "hello_" + System.currentTimeMillis();
        log.info("hello ctx cid : {}", ContextLocals.get("CORRELATION_ID").orElse("null"));
        emitter.send("hello");
        Thread.sleep(5000);
        return cid != null ? cid : "null";
    }

    public String getCid() {
        return cid;
    }
}
