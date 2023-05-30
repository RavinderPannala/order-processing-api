package com.nisum.orderprocessingapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyerTaxInfoDto {


    private String id;
    private String companyLegalName;
}
