package com.mike.inbound;

import com.mike.config.PubSubConfiguration;
import com.mike.outbound.OutboundConfiguration;
import com.mike.outbound.OutboundRestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import com.mike.models.Transaction;
import com.mike.models.UserPreference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class InboundConfiguration {
    @Autowired
    private PubSubConfiguration pubSubConfiguration;
    @Autowired
    private OutboundRestController outboundRestController;
    @Autowired
    private OutboundConfiguration.PubsubOutboundGateway messagingGateway;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, pubSubConfiguration.getSubscription());
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }
    @Bean
    public MessageChannel pubsubInputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubsubInputChannel")
    public MessageHandler messageReceiver() {
        return message -> {
            BasicAcknowledgeablePubsubMessage originalMessage =
                    message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE,
                            BasicAcknowledgeablePubsubMessage.class);

            try{
            System.out.println("¤¤¤ Message arrived! Payload: " + new String((byte[]) message.getPayload()));
//            log.info("headers {}", message.getHeaders());
//            Transaction transaction = (Transaction) message.getPayload();
                byte[] payloadBytes = (byte[]) message.getPayload();
                Transaction transaction = objectMapper.readValue(payloadBytes, Transaction.class);

                System.out.println("Trasaction is: "+ transaction);
            System.out.println("Trasaction Amount is: "+ transaction.getAmount());
            System.out.println("Trasaction UserId is: "+ transaction.getUserId());
            if (transaction.getAmount() > 200) {

                System.out.println("Before Publishing Message");
  //              UserPreference userPreference = new UserPreference();
  //              userPreference.setUserId(transaction.getUserId());
 //               pubSubTemplate.publish(userPreferenceTopic, userPreference);
                messagingGateway.sendToPubsub(transaction.getUserId());
                System.out.println("After Publishing Message");
 //               outboundRestController.sendMessage(transaction.getUserId());
            }

            System.out.println("Before OriginalMessage");

            System.out.println("After OriginalMessage");
                if (originalMessage != null) {
                    originalMessage.ack();
                } else {
                    log.warn("Original message is null, unable to acknowledge.");
                }
            System.out.println("After Ack()");
            } catch (Exception e) {
                log.error("Error processing message", e);
                if (originalMessage != null) {
                    originalMessage.nack(); // Optionally negative acknowledge the message if processing fails
                }
            }
        };
    }

}

//    @Bean
//    @ServiceActivator(inputChannel = "pubsubInputChannel")
//    public MessageHandler messageReceiver() {
//        return message -> {
//            byte[] payloadBytes = (byte[]) message.getPayload();
//            try {
//                // Deserialize the payload
//                Transaction transaction = objectMapper.readValue(payloadBytes, Transaction.class);
//                System.out.println("¤¤¤ Message arrived! Payload: " + transaction);
//
//                if (transaction.getAmount() > 250) {
//                    outboundRestController.sendMessage(transaction.getUserId());
//                }
//
//                BasicAcknowledgeablePubsubMessage originalMessage =
//                        message.getHeaders().get(GcpPubSubHeaders.ORIGINAL_MESSAGE,
//                                BasicAcknowledgeablePubsubMessage.class);
//                originalMessage.ack();
//
//            } catch (Exception e) {
//                log.error("Failed to deserialize message payload", e);
//            }
//        };
//    }
//}
