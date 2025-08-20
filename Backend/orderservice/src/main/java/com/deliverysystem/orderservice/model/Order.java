package io.example.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private final OrderIdentifier id;

    private Long orderId;
    private Long customerId;
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Float price;
    public Long paymentDue;
    @ManyToMany
    @JoinTable(
            name = "order_inventory",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "inventory_id")
    )
    private Set<Food> foods;
    public String location;

    @Builder
    public Order(Long orderId,
                       Long customerId,
                       LocalDate orderDate,
                       Status status,
                       Float price,
                       Long paymentDue,
                       Set<Food> foods,
                        String location) {
        this.orderId = requireNonNull(orderId, "orderId cannot be null");
        this.customerId = requireNonNull(customerId, "userId cannot be null");
        this.orderDate = requireNonNull(orderDate, "orderDate cannot be null");
        this.status = requireNonNull(status, "status cannot be null");
        this.price = requireNonNull(price, "price cannot be null");
        this.paymentDue = requireNonNull(paymentDue, "paymentDue cannot be null");
        this.foods = requireNonNull(foods, "foods cannot be null");
    }

    public OrderIdentifier id() {
        return id;
    }

    public Status status() {
        return status;
    }

    public Long hotelId() {
        return hotelId;
    }

    public Long roomId() {
        return roomId;
    }

    public Long guestId() {
        return guestId;
    }

    public void markSucceed() {
        this.status = Status.SUCCEED;
    }

    public void markFailed() {
        this.status = Status.FAILED;
    }

    @Embeddable
    public record ReservationIdentifier(UUID id) implements Serializable {

        public String toString() {
            return id.toString();
        }
    }

    public enum Status {
        PENDING, SUCCEED, FAILED, CANCELED, REFUND
    }

    public ObjectNode toSagaPayload() {
        return new ObjectMapper().createObjectNode()
                .put("reservationId", id.toString())
                .put("hotelId", hotelId)
                .put("roomId", roomId)
                .put("startDate", startDate.format(DateTimeFormatter.ISO_DATE))
                .put("endDate", endDate.format(DateTimeFormatter.ISO_DATE))
                .put("guestId", guestId)
                .put("paymentDue", paymentDue)
                .put("creditCardNo", creditCardNo);
    }
}
