package ch.puzzle.quarkustechlab.reactivetransformer.boundary;

import ch.puzzle.quarkustechlab.reactivetransformer.control.HeadersMapExtractAdapter;
import ch.puzzle.quarkustechlab.reactivetransformer.entity.SensorMeasurement;
import io.opentracing.Scope;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.smallrye.reactive.messaging.kafka.IncomingKafkaRecordMetadata;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

@ApplicationScoped
public class ReactiveDataTransformer {

    private final Logger logger = Logger.getLogger(ReactiveDataTransformer.class.getName());

    static double sum;
    static int count;

    @Inject
    Tracer tracer;


    @Counted(name = "messagesTransformed", description = "How many messages were transformed.")
    @Incoming("data")
    @Timed(name = "transformationTimer", description = "A measure of how long it takes to transform the data.", unit = MetricUnits.MILLISECONDS)
    public CompletionStage<Void> consumeStream(Message<SensorMeasurement> message) {
        logger.info("Message received");
        Optional<IncomingKafkaRecordMetadata> metadata = message.getMetadata(IncomingKafkaRecordMetadata.class);
        if (metadata.isPresent()) {
            SpanContext extract = tracer.extract(Format.Builtin.TEXT_MAP, new HeadersMapExtractAdapter(metadata.get().getHeaders()));
            try (Scope scope = tracer.buildSpan("transform-data").asChildOf(extract).startActive(true)) {
                sum += message.getPayload().data;
                count++;
                logger.info("Current average: " + sum / count);
                tracer.scopeManager().active().close();
                return message.ack();
            }
        }
        return message.nack(new RuntimeException());
    }
}
