package otelpoc;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.AzureMonitorTraceExporter;

import io.opentelemetry.api.OpenTelemetry;
// import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
// import io.opentelemetry.sdk.OpenTelemetrySdk;
// import io.opentelemetry.sdk.trace.SdkTracerProvider;
// import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
// import io.opentelemetry.sdk.resources.Resource;
// import io.opentelemetry.api.common.Attributes;
// import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;


public final class OtelConfiguration {

    // public static OpenTelemetrySdk openTelemetrySdk;

    public static OpenTelemetry initOpenTelemetry() {

        return GlobalOpenTelemetry.get();

        // JAN 27 2022 UPDATE: THIS METHODS HAS BEEN COMMENTED AS THERE IS A BETTER WAY TO OBTAING THE 
        // OPENTELEMETRY OBJECT CREATED BY THE AGENT FROM THE GlobalOpenTelemetry USED ABOVE
        // If we weren't using the Agent, we would have needed to create and configure the SKD manually 
        // using the commented code:

        // JaegerGrpcSpanExporter exporterJg =
        // JaegerGrpcSpanExporter.builder()
        //     .setEndpoint(System.getenv("OTEL_EXPORTER_JAEGER_ENDPOINT"))
        //     .build();

        // String az_connection = System.getenv("OTEL_AZ_MONITOR_CONNECTION");
        // SdkTracerProvider tracerProvider = null;

        // // If an Application Insights connection string is different from "none" in the OTEL_AZ_MONITOR_CONNECTION
        // // when runnning in the Otel agent mode, the AzureMonitorTraceExporter will be register as an additional 
        // // span processor in the OTEL SDK Tracer provider. This will provide flexible scenarios when running under 
        // // full OTEL mode while exporting telemetry to Azure Monitor at the same time.
        // if (!az_connection.equals("none")){

        //     AzureMonitorTraceExporter exporterAz = new AzureMonitorExporterBuilder()
        //         .connectionString(az_connection)
        //         .buildTraceExporter();
            
        //     tracerProvider = SdkTracerProvider.builder()
        //     .addSpanProcessor(SimpleSpanProcessor.create(exporterJg))
        //     .addSpanProcessor(SimpleSpanProcessor.create(exporterAz))
        //     // .setResource(AutoConfiguredOpenTelemetrySdk.initialize().getResource())
        //     .build();
        // }
        // else{
        //     tracerProvider = SdkTracerProvider.builder()
        //     .addSpanProcessor(SimpleSpanProcessor.create(exporterJg))
        //     .setResource(Resource.create
        //         (Attributes.of
        //             (ResourceAttributes.SERVICE_NAME, System.getenv("service.name"))))
        //     .build();
        //     }

        // openTelemetrySdk =
        //     OpenTelemetrySdk.builder()
        //     .setTracerProvider(tracerProvider)
        //     .buildAndRegisterGlobal();
    
        // Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
    
        // return openTelemetrySdk;
      }
    
}
