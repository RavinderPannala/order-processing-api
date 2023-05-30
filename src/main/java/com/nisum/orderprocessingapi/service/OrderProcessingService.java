package com.nisum.orderprocessingapi.service;

import com.nisum.orderprocessingapi.dto.IdAndOrderIdMapper;
import com.nisum.orderprocessingapi.dto.OrderCollectionDto;
import com.nisum.orderprocessingapi.dto.OrderDto;
import com.nisum.orderprocessingapi.entity.BuyerInfo;
import com.nisum.orderprocessingapi.entity.BuyerTaxInfo;
import com.nisum.orderprocessingapi.entity.Order;
import com.nisum.orderprocessingapi.entity.ShippingAddress;
import com.nisum.orderprocessingapi.repository.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderProcessingService {

    @Autowired
    private ReactiveMongoOperations reactiveOps;



    public Mono<Boolean> save(OrderDto orderDto,String orderKey)
    {

       Order order=OrderMapper.INSTANCE.orderDtoToEntity(orderDto,orderKey);
       BuyerInfo buyerInfo=OrderMapper.INSTANCE.buyerInfoDtoToEntity(orderDto.getBuyerInfo(),orderKey);
       BuyerTaxInfo buyerTaxInfo= OrderMapper.INSTANCE.buyerTaxInfoDtoToEntity(orderDto.getBuyerInfo().getBuyerTaxInfo(), orderKey);
       ShippingAddress shippingAddress= OrderMapper.INSTANCE.shippingAddressDtoToEntity(orderDto.getShippingAddress(),orderKey);
        reactiveOps.<Order>save(order)
                .<BuyerInfo>and(reactiveOps.save(buyerInfo))
                .<BuyerTaxInfo>and(reactiveOps.save(buyerTaxInfo))
                .<ShippingAddress>and(reactiveOps.save(shippingAddress))
                .subscribe(status->log.info("Order|buyerInfo|buyerTaxInfo|shippingAddress is created : {}",status));

        return Mono.just(Boolean.TRUE);

    }

    public Flux<IdAndOrderIdMapper> saveAll(OrderCollectionDto orderCollectionDto)
    {

        List<IdAndOrderIdMapper> idAndOrderIdMappers= orderCollectionDto.getOrdersDto().stream().map(orderDto -> {
            try {

                String uuid= UUID.randomUUID().toString();
                Order order = OrderMapper.INSTANCE.orderDtoToEntity(orderDto, uuid);
                BuyerInfo buyerInfo = OrderMapper.INSTANCE.buyerInfoDtoToEntity(orderDto.getBuyerInfo(), uuid);
                BuyerTaxInfo buyerTaxInfo = OrderMapper.INSTANCE.buyerTaxInfoDtoToEntity(orderDto.getBuyerInfo().getBuyerTaxInfo(), uuid);
                ShippingAddress shippingAddress = OrderMapper.INSTANCE.shippingAddressDtoToEntity(orderDto.getShippingAddress(), uuid);
                reactiveOps.<Order>save(order)
                        .<BuyerInfo>and(reactiveOps.save(buyerInfo))
                        .<BuyerTaxInfo>and(reactiveOps.save(buyerTaxInfo))
                        .<ShippingAddress>and(reactiveOps.save(shippingAddress))
                        .subscribe(status -> log.info("Order|buyerInfo|buyerTaxInfo|shippingAddress is created : {}", status));


                return IdAndOrderIdMapper.builder().orderId(orderDto.getOrderId()).id(uuid).build();
            } catch (Exception e) {

                e.printStackTrace();
                return IdAndOrderIdMapper.builder().build();
            }
        }).collect(Collectors.toList());

        return Flux.fromIterable(idAndOrderIdMappers);

    }



    public Mono<OrderDto> update(OrderDto orderDto)
    {


                    return reactiveOps.findById(orderDto.getId(),Order.class)
                            .map(order -> OrderMapper.INSTANCE.orderDtoToEntity(orderDto,orderDto.getId()))
                            .map(reactiveOps::save)
                            .map(Mono::block)
                            .map(order -> OrderMapper.INSTANCE.orderEntityToDto(order))
                            .map(updatedOrderDto->{
                                log.info("Order information is updated {} ",updatedOrderDto);
                                return reactiveOps.findById(orderDto.getId(),ShippingAddress.class)
                                        .map(buyerInfo -> OrderMapper.INSTANCE.shippingAddressDtoToEntity(orderDto.getShippingAddress(),orderDto.getId()))
                                        .map(reactiveOps::save)
                                        .map(Mono::block)
                                        .map(shippingAddress -> OrderMapper.INSTANCE.shippingAddressEntityToDTO(shippingAddress))
                                        .map(shippingAddressDto -> {
                                            log.info("Shipping information is updated {} ",shippingAddressDto);
                                            updatedOrderDto.setShippingAddress(shippingAddressDto);
                                            return updatedOrderDto;
                                        });

                            })
                            .map(Mono::block)
                            .map(updatedOrderDto -> {
                                  Mono<OrderDto>  orderDtoMono=reactiveOps.findById(orderDto.getId(),BuyerInfo.class)
                                        .map(buyerInfo -> OrderMapper.INSTANCE.buyerInfoDtoToEntity(orderDto.getBuyerInfo(),orderDto.getId()))
                                        .map(reactiveOps::save)
                                        .map(Mono::block)
                                        .map(buyerInfo -> OrderMapper.INSTANCE.buyerInfoEntityToDto(buyerInfo))
                                        .map(buyerInfoDto -> {
                                            log.info("BuyerInfo information is updated {} ",buyerInfoDto);
                                            return reactiveOps.findById(orderDto.getId(),BuyerTaxInfo.class)
                                                    .map(buyerTaxInfo -> OrderMapper.INSTANCE.buyerTaxInfoDtoToEntity(orderDto.getBuyerInfo().getBuyerTaxInfo(),orderDto.getId()))
                                                    .map(reactiveOps::save)
                                                    .map(Mono::block)
                                                    .map(buyerTaxInfo->OrderMapper.INSTANCE.buyerTaxInfoEntityToDto(buyerTaxInfo))
                                                    .map(buyerTaxInfoDto->{
                                                        log.info("Buyer Tax Info is updated  {} ",buyerTaxInfoDto);
                                                        buyerInfoDto.setBuyerTaxInfo(buyerTaxInfoDto);
                                                        return buyerInfoDto;
                                                    });
                                        })
                                         .map(Mono::block)
                                         .map(buyerInfoDto -> {
                                            updatedOrderDto.setBuyerInfo(buyerInfoDto);
                                            return updatedOrderDto;
                                         });
                                 return orderDtoMono;
                            }).map(Mono::block);


    }

    public Mono<Boolean> delete(String id )
    {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("id").is(id));

            reactiveOps.findAndRemove(query,Order.class).subscribe(order -> {
                        log.info("Order information deleted !!! for id: {}", order.getId());

            });
            reactiveOps.findAndRemove(query,BuyerInfo.class).subscribe(buyerInfo -> {
                log.info("Buyer information deleted !!! for id: {}", buyerInfo.getId());

            });
            reactiveOps.findAndRemove(query,BuyerTaxInfo.class).subscribe(taxInfo -> {
                log.info("BuyerTax information deleted !!! for id: {}", taxInfo.getId());

            });
            reactiveOps.findAndRemove(query,ShippingAddress.class).subscribe(address -> {
                log.info("Shipping information deleted !!! for id: {}", address.getId());

            });

            return Mono.just(Boolean.TRUE);
        }catch (Exception e)
        {

            e.printStackTrace();
            return Mono.just(Boolean.FALSE);
        }
    }

    public Mono<OrderDto> read(String id ) {


        return reactiveOps.findById(id,Order.class)
                .map(orderEntity -> OrderMapper.INSTANCE.orderEntityToDto(orderEntity))
                .map(orderDto -> {
                    log.info("Order information found !!! for id: {}",orderDto );
                    return reactiveOps.findById(id,ShippingAddress.class).map(shippingAddress -> OrderMapper.INSTANCE.shippingAddressEntityToDTO(shippingAddress)
                    ).map(shippingAddressDto  -> {

                        log.info("shippingAddress information found !!! for id: {}",shippingAddressDto );
                        orderDto.setShippingAddress(shippingAddressDto);
                        return orderDto;
                    }).block();
                })
                .map(orderDto -> {

                    return  reactiveOps.findById(id,BuyerInfo.class)
                            .map(buyerInfo -> OrderMapper.INSTANCE.buyerInfoEntityToDto(buyerInfo)
                    ).map(buyerInfoDto -> {
                        log.info("BuyerInfo information found !!! for id: {}",buyerInfoDto );
                        //Retrieving Buyer tax information
                        return reactiveOps.findById(id,BuyerTaxInfo.class).map(buyerTaxInfo -> OrderMapper.INSTANCE.buyerTaxInfoEntityToDto(buyerTaxInfo)
                        ).map(buyerTaxInfoDto -> {
                            log.info("buyerTaxInfo information found !!! for id: {}",buyerTaxInfoDto );
                            buyerInfoDto.setBuyerTaxInfo(buyerTaxInfoDto);
                            return buyerInfoDto;
                        }).map(taxInfo-> {
                            orderDto.setBuyerInfo(taxInfo);
                            return orderDto;
                        }).block();

                    }).block();
                });

    }

}
