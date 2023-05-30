package com.nisum.orderprocessingapi.repository;

import com.nisum.orderprocessingapi.dto.BuyerInfoDto;
import com.nisum.orderprocessingapi.dto.BuyerTaxInfoDto;
import com.nisum.orderprocessingapi.dto.OrderDto;
import com.nisum.orderprocessingapi.dto.ShippingAddressDto;
import com.nisum.orderprocessingapi.entity.BuyerInfo;
import com.nisum.orderprocessingapi.entity.BuyerTaxInfo;
import com.nisum.orderprocessingapi.entity.Order;
import com.nisum.orderprocessingapi.entity.ShippingAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {

   OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

   OrderDto orderEntityToDto(Order order);

   @Mapping(target = "id", source = "orderKey")
   Order orderDtoToEntity(OrderDto orderDto,String orderKey);

   ShippingAddressDto shippingAddressEntityToDTO(ShippingAddress shippingAddress);

   @Mapping(target = "id", source = "orderKey")
   ShippingAddress shippingAddressDtoToEntity(ShippingAddressDto shippingAddressDto,String orderKey);

   BuyerInfoDto buyerInfoEntityToDto(BuyerInfo buyerInfo);

   @Mapping(target = "id", source = "orderKey")
   BuyerInfo buyerInfoDtoToEntity(BuyerInfoDto buyerInfo,String orderKey);

   @Mapping(target = "id", source = "orderKey")
   BuyerTaxInfo buyerTaxInfoDtoToEntity(BuyerTaxInfoDto buyerTaxInfoDto,String orderKey);

   BuyerTaxInfoDto buyerTaxInfoEntityToDto(BuyerTaxInfo buyerTaxInfo);
}
