package com.nisum.orderprocessingapi.controller;

import com.google.gson.Gson;
import com.nisum.orderprocessingapi.dto.IdAndOrderIdMapper;
import com.nisum.orderprocessingapi.dto.OrderCollectionDto;
import com.nisum.orderprocessingapi.dto.OrderDto;
import com.nisum.orderprocessingapi.dto.OrderResponseDto;
import com.nisum.orderprocessingapi.producer.OrderEventGenerator;
import com.nisum.orderprocessingapi.service.OrderProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverRecord;

@Slf4j
@RestController
@RequestMapping(path = "/order")
public class OrderController {

    @Autowired
    private KafkaReceiver<String,String> kafkaReceiver;

    @Autowired
    private  OrderEventGenerator orderEventGenerator;

    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    private Gson gson;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux getAllOrderEvents(){
        Flux<ReceiverRecord<String,String>> kafkaFlux = kafkaReceiver.receive();
        return kafkaFlux.checkpoint("Messages are started being consumed").log()
                .doOnNext(order->
        {
            String key = order.key();
            OrderDto orderDto = gson.fromJson(order.value(), OrderDto.class);

            orderProcessingService.save(orderDto,key).onErrorResume(o->{
                log.info("Unable to save document in mongo DB {} ",o);
                order.receiverOffset().acknowledge();
                return  Mono.empty();
            }).subscribe(orderStatus -> {
                log.info("Mongo DB : Order Key : {} saved status {}", key,orderStatus);
                order.receiverOffset().acknowledge();
            });

        }).map(ReceiverRecord::value)
                .checkpoint("Messages are done consumed");

    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<OrderResponseDto> createOrderEventInKafka(@RequestBody OrderCollectionDto orderCollectionDto) throws InterruptedException {

        log.info("orderCollection Received : {}",orderCollectionDto);
        return orderEventGenerator.publishOrderEvent("test-order",orderCollectionDto);


     }

    @PostMapping(path = "/create",produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<IdAndOrderIdMapper> createOrderEvent(@RequestBody OrderCollectionDto orderCollectionDto) throws InterruptedException {

        log.info("orderCollection Received : {}",orderCollectionDto);
        return orderProcessingService.saveAll(orderCollectionDto);


    }

    @PutMapping(path = "/update/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<OrderDto> updateOrderEvent(@PathVariable String id, @RequestBody OrderDto orderDto) throws InterruptedException {

        log.info("orderCollection Received for update : {}",orderDto);
        return orderProcessingService.update(orderDto);


    }

    @DeleteMapping("/delete/{id}")
    public Mono<Boolean> deleteOrderEvent(@PathVariable String id) throws InterruptedException {

        log.info("Order id Received for deletion : {}",id);
        return orderProcessingService.delete(id);


    }

    @GetMapping("/get/{id}")
    public Mono<OrderDto> readOrderEvent(@PathVariable String id) throws InterruptedException {

        log.info("Order id Received for retrieval : {}",id);
        return orderProcessingService.read(id);


    }



}
