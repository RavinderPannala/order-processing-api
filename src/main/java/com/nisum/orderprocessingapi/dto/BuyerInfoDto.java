package com.nisum.orderprocessingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyerInfoDto {


    private String id;
    private String buyerEmail;
    private String buyerName;
    private BuyerTaxInfoDto buyerTaxInfo;
    private String purchaseOrderNumber;

}
