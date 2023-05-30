package com.nisum.orderprocessingapi.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdAndOrderIdMapper {
    
    private String id;
    private String orderId;
}
