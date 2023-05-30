package com.nisum.orderprocessingapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("shipping_address")
public class ShippingAddress {


    @Id
    private String id;
    private String name;
    private String addressLine1;
    private String city;
    private String stateOrRegion;
    private String postalCode;
    private String countryCode;

}
