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
@Document("buyer_tax_info")
public class BuyerTaxInfo {
    @Id
    private String id;
    private String companyLegalName;
}
