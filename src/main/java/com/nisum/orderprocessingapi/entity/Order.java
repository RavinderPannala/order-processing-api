package com.nisum.orderprocessingapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("order")
public class Order {

    @Id
    private String id;
    private String orderId;
    private String purchaseDate;
    private String lastUpdateDate;
    private String orderStatus;
    private String fulfillmentChannel;
    private Integer numberOfItemsShipped;
    private Integer numberOfItemsUnshipped;
    private String paymentMethod;
    private List<String> paymentMethodDetails;
    private String marketplaceId;
    private String shipmentServiceLevelCategory;
    private String orderType;
    private String earliestShipDate;
    private String latestShipDate;


}