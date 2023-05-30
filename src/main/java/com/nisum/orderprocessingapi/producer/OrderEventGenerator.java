package com.nisum.orderprocessingapi.producer;

import com.google.gson.Gson;
import com.nisum.orderprocessingapi.dto.IdAndOrderIdMapper;
import com.nisum.orderprocessingapi.dto.OrderCollectionDto;
import com.nisum.orderprocessingapi.dto.OrderResponseDto;
import com.nisum.orderprocessingapi.service.OrderProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
@Slf4j
public class OrderEventGenerator {

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    @Autowired
    private OrderProcessingService orderProcessingService;

    @Autowired
    private Gson gson;


    public Flux<OrderResponseDto> publishOrderEvent(String topic, OrderCollectionDto orderCollectionDto) throws InterruptedException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmssSSSzddMMyyyy");


        OrderResponseDto response= new OrderResponseDto();
        response.setTopicName(topic);
        List<IdAndOrderIdMapper> idAndOrderIdMappers=orderCollectionDto.getOrdersDto().stream().map(orderDto -> {
            String uuid= UUID.randomUUID().toString();
            String orderId= orderDto.getOrderId();
            String orderString=gson.toJson(orderDto);
            log.info("Order Information: {} ",orderString);
            kafkaSender.send(Flux.just(
                    SenderRecord.create(new ProducerRecord<>(topic, uuid, orderString),uuid)))
                    .doOnError(e -> log.error("Send failed", e))
                    .subscribe(r -> {
                        RecordMetadata metadata = r.recordMetadata();
                        System.out.printf("Message %s sent successfully, topic-partition=%s-%d offset=%d timestamp=%s%n",
                                r.correlationMetadata(),
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset(),
                                dateFormat.format(new Date(metadata.timestamp())));

                    });

            return IdAndOrderIdMapper.builder().orderId(orderId).id(uuid).build();
        }).collect(Collectors.toList());
        response.setOrderKey(idAndOrderIdMappers);
        return Flux.just(response);

    }


}
