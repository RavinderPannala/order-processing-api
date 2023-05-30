package com.nisum.orderprocessingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressDto {


    private String id;
    private String name;
    private String addressLine1;
    private String city;
    private String stateOrRegion;
    private String postalCode;
    private String countryCode;

}
