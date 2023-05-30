package com.nisum.orderprocessingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {


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
    private ShippingAddressDto shippingAddress;
    private BuyerInfoDto buyerInfo;


}