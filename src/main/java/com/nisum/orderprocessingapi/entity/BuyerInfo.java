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
@Document("buyer_info")
public class BuyerInfo {

    @Id
    private String id;
    private String buyerEmail;
    private String buyerName;
    private String purchaseOrderNumber;

}
