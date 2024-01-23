package lupogryph.rep.quarkus;

import io.smallrye.common.vertx.ContextLocals;
import io.smallrye.reactive.messaging.OutgoingInterceptor;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadata;
import io.smallrye.reactive.messaging.ce.OutgoingCloudEventMetadataBuilder;
import io.smallrye.reactive.messaging.ce.impl.DefaultOutgoingCloudEventMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@ApplicationScoped
public class MyOutgoingInterceptor implements OutgoingInterceptor {
    private static final Logger log = LoggerFactory.getLogger(MyOutgoingInterceptor.class);

    @Override
    public Message<?> onMessage(Message<?> message) {
        Optional<String> ctxCid = ContextLocals.get("CORRELATION_ID");
        log.info("ctx cid : {}", ctxCid.orElse("null"));

        Optional<DefaultOutgoingCloudEventMetadata> optionalMetadata = message.getMetadata()
                .get(DefaultOutgoingCloudEventMetadata.class);

        if(ctxCid.isPresent()) {
            OutgoingCloudEventMetadataBuilder metadataBuilder = optionalMetadata
                    .<OutgoingCloudEventMetadataBuilder>map(OutgoingCloudEventMetadata::from)
                    .orElseGet(OutgoingCloudEventMetadata::builder);

            metadataBuilder
                    .withExtension("correlationid", ctxCid.get())
                    .withExtension("name", "test");

            return message.addMetadata(metadataBuilder.build());
        }

        return message;
    }

    @Override
    public void onMessageAck(Message<?> message) {

    }

    @Override
    public void onMessageNack(Message<?> message, Throwable throwable) {

    }
}
