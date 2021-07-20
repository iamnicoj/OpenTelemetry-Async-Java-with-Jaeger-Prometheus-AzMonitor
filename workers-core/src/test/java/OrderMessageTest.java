import com.fasterxml.jackson.core.JsonProcessingException;
import entity.OrderMessage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class OrderMessageTest {
    OrderMessage message = new OrderMessage();

    @Before
    public void setUp(){
        message.setCustomerId("mkcomer");
        message.setOrderId("1234");
        message.setStockNumber("4346");
        message.setStatus("Validation Successful");
        message.setPrice(4.5);
        message.setQuantity(4);
    }

    @Test
    public void testToString(){
        String stringOutput = message.toString();
        System.out.println(stringOutput);
    }

    @Test
    public void testDeserialize() throws JsonProcessingException {
        OrderMessage newMessage = new OrderMessage();
        String stringOutput = "{\n" +
                "   \"customerId\":\"mkcomer\",\n" +
                "   \"orderId\":\"1234\",\n" +
                "   \"stockNumber\":\"4346\",\n" +
                "   \"quantity\":4,\n" +
                "   \"price\":4.5,\n" +
                "   \"status\":\"Validation Successful\"\n" +
                "}";

        OrderMessage otel = newMessage.deserialize(stringOutput);
        assertNotNull(otel);
        assertNotNull(otel.getCustomerId());
        assertNotNull(otel.getOrderId());
        assertNotNull(otel.getPrice());
        assertNotNull(otel.getQuantity());
        assertNotNull(otel.getStockNumber());
        assertNotNull(otel.getStatus());
    }

}
