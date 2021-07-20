package entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RedisHash("ORDERS")
public class OrderMessage implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OrderMessage.class);

    // @RedisHash and @Id annotations are responsible for creating the actual key used to persis the hash
    @Id
    private Long id;

    private String customerId;
    private String orderId;
    private String stockNumber;
    private int quantity;
    private double price;
    private String status;

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }

    @Override
    public String toString(){
        OrderMessage OrderMessage = new OrderMessage();
        OrderMessage.setCustomerId(customerId);
        OrderMessage.setOrderId(orderId);
        OrderMessage.setPrice(price);
        OrderMessage.setStockNumber(stockNumber);
        OrderMessage.setQuantity(quantity);
        OrderMessage.setStatus(status);
        //Creating the ObjectMapper object
        ObjectMapper mapper = new ObjectMapper();
        //Converting the Object to JSONString
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(OrderMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.info(e.getMessage());
        }
        return jsonString;
    }

    public OrderMessage deserialize(String inputString){
        OrderMessage orderMessage = null;
            ObjectMapper mapper = new ObjectMapper();
            try {
                orderMessage = mapper.readValue(inputString, OrderMessage.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
            return orderMessage;
    }
}
