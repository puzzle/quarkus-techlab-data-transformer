package ch.puzzle.quarkustechlab.reactivetransformer.control;

import ch.puzzle.quarkustechlab.reactivetransformer.entity.SensorMeasurement;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class SensorMeasurementDeserializer extends JsonbDeserializer<SensorMeasurement> {

    public SensorMeasurementDeserializer() {
        super(SensorMeasurement.class);
    }
}
