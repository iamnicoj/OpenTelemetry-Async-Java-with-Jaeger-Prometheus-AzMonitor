package otelpoc;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import com.azure.monitor.opentelemetry.exporter.AzureMonitorTraceExporter;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.jaeger.JaegerGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.semconv.resource.attributes.ResourceAttributes;
import io.opentelemetry.api.GlobalOpenTelemetry;

public final class OtelConfiguration {

    public static OpenTelemetry openTelemetrySdk;
    
    public static OpenTelemetry initOpenTelemetry() {
        
        // JAN 27 2022 UPDATE: THIS METHODS HAS BEEN CHANGED AS THERE IS A BETTER WAY TO OBTAIN THE 
        // OPENTELEMETRY OBJECT CREATED BY THE AGENT FROM THE GlobalOpenTelemetry 
        // Only when using the AZ monitor exporter we will temporary need to create the whole stack manually

        String az_connection = System.getenv("OTEL_AZ_MONITOR_CONNECTION");

        if (!az_connection.equals("none")){
            SdkTracerProvider tracerProvider = null;

            JaegerGrpcSpanExporter exporterJg =
            JaegerGrpcSpanExporter.builder()
                .setEndpoint(System.getenv("OTEL_EXPORTER_JAEGER_ENDPOINT"))
                .build();

            AzureMonitorTraceExporter exporterAz = new AzureMonitorExporterBuilder()
                .connectionString(az_connection)
                .buildTraceExporter();
                
            tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(exporterJg))
                .addSpanProcessor(SimpleSpanProcessor.create(exporterAz))
                .setResource(Resource.create
                    (Attributes.of
                        (ResourceAttributes.SERVICE_NAME, System.getenv("service.name"))))
                .build();

            openTelemetrySdk = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .buildAndRegisterGlobal();
    
            Runtime.getRuntime().addShutdownHook(new Thread(tracerProvider::close));
        
            // return openTelemetrySdk;
        }

        // If not using AZ Monitor we can just use the OTel settings created by the agent
        else{ 
            openTelemetrySdk = GlobalOpenTelemetry.get();
        }
        
        return openTelemetrySdk;
      }   
}
