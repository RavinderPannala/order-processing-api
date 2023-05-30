package com.nisum.orderprocessingapi;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class OrderProcessingApplication {

	private static final String TOPIC = "test-order";
	private static final String BOOTSTRAP_SERVERS = "HYD-LAP-00484.corp.nisum.com:9092";
	private static final String CLIENT_ID_CONFIG = "trans-string-consumer-test-order";

	public static void main(String[] args) {
		SpringApplication.run(OrderProcessingApplication.class, args);
	}


	@Bean
	public KafkaSender<String, String> kafkaSender()
	{
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
		props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID_CONFIG);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

		SenderOptions<String, String> senderOptions = SenderOptions.create(props);
		return KafkaSender.create(senderOptions);
	}


	@Bean
	public Gson gson ()
	{
		return new Gson();
	}

}
