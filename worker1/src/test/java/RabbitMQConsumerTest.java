import entity.OrderMessage;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import worker1.RabbitMqReceiver;

public class RabbitMQConsumerTest {
    RabbitMqReceiver consumer = new RabbitMqReceiver();
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
    public void testValidateOtelMessageVALID(){
        OrderMessage returnedMessage = consumer.updateStatus(message);
        assertEquals(returnedMessage.getStatus(), "VALID");
    }

    @Test
    public void testValidateOtelMessageINVALID(){
        message.setQuantity(0);
        OrderMessage returnedMessage = consumer.updateStatus(message);
        assertEquals(returnedMessage.getStatus(), "INVALID");
    }


}
