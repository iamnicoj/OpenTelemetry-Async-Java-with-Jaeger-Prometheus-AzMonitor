package otelpoc;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import entity.OrderMessage;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;

public class OtelOrderSpan {


    private static final Logger logger = LoggerFactory.getLogger(OtelOrderSpan.class);

    private Tracer tracer;

    public Span GetOtelOrderSpan(OrderMessage om, String spanName, String tracerName) throws InterruptedException {
		if (System.getenv("AGENTTYPE").toLowerCase().equals("otel")){
            tracer = OtelConfiguration.openTelemetrySdk.getTracer(tracerName);
		}
        
        Span childSpan = null;
        if (tracer != null){
            logger.info("Creating Otel Span from Tracer");
            childSpan = tracer.spanBuilder(spanName).startSpan();
        }
        else{
            logger.info("Creating Otel Span from Current Span");
            childSpan = Span.current();
        }
        childSpan.updateName(spanName);
        childSpan.setStatus(StatusCode.OK);

        childSpan.setAttribute("orderId", om.getOrderId())
                .setAttribute("customerId", om.getCustomerId())
                .setAttribute("price", om.getPrice())
                .setAttribute("quantity", om.getQuantity())
                .setAttribute("stockNumber", om.getStockNumber())
                .setAttribute("status", om.getStatus());

        return childSpan;
    }
}
