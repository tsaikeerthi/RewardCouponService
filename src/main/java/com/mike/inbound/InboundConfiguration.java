package com.mike.inbound;

import com.mike.config.PubSubConfiguration;
import com.mike.models.Coupon;
import com.mike.outbound.OutboundConfiguration;
import com.mike.outbound.OutboundRestController;
import com.mike.service.EmailService;
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

import java.nio.charset.StandardCharsets;


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
    @Autowired
    private EmailService emailService;

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
                String payloadString = new String(payloadBytes, StandardCharsets.UTF_8);

                if (!payloadString.contains("Coupon")) {
                    Transaction transaction = objectMapper.readValue(payloadBytes, Transaction.class);

                    System.out.println("Trasaction is: " + transaction);
                    System.out.println("Trasaction Amount is: " + transaction.getAmount());
                    System.out.println("Trasaction UserId is: " + transaction.getUserId());
                    if (transaction.getAmount() > 200) {

                        System.out.println("Before Publishing Message");
                        //              UserPreference userPreference = new UserPreference();
                        //              userPreference.setUserId(transaction.getUserId());
                        //               pubSubTemplate.publish(userPreferenceTopic, userPreference);
                        messagingGateway.sendToPubsub(transaction.getUserId());
                        System.out.println("After Publishing Message");
                        //               outboundRestController.sendMessage(transaction.getUserId());
                    }
                } else {
                    Coupon couponDetails = objectMapper.readValue(payloadBytes,Coupon.class);
                    String messageCoupon;
                    switch (couponDetails.getCouponCode()){
                        case "1":
                            messageCoupon = "Brand: Tesco\nCoupon Code: TES12345\nDetails: 10% off on groceries. Valid until 31st December 2024.";
                            break;
                        case "2":
                            messageCoupon = "Brand: Sainsbury's\nCoupon Code: SAI67890\nDetails: £5 off on orders over £50. Valid until 30th November 2024.";
                            break;
                        case "3":
                            messageCoupon = "Brand: Marks & Spencer\nCoupon Code: MKS13579\nDetails: 15% off on clothing. Valid until 31st October 2024.";
                            break;
                        case "4":
                            messageCoupon = "Brand: ASDA\nCoupon Code: ASD24680\nDetails: 20% off on electronics. Valid until 31st December 2024.";
                            break;
                        case "5":
                            messageCoupon = "Brand: Boots\nCoupon Code: BOO11223\nDetails: £10 off on beauty products. Valid until 31st August 2024.";
                            break;
                        case "6":
                            messageCoupon = "Brand: John Lewis\nCoupon Code: JNL44556\nDetails: 25% off on home decor. Valid until 31st December 2024.";
                            break;
                        case "7":
                            messageCoupon = "Brand: Argos\nCoupon Code: ARG77889\nDetails: 5% off on all items. Valid until 31st July 2024.";
                            break;
                        case "8":
                            messageCoupon = "Brand: Amazon UK\nCoupon Code: AMZ99000\nDetails: £15 off on orders over £100. Valid until 31st December 2024.";
                            break;
                        case "9":
                            messageCoupon = "Brand: Next\nCoupon Code: NXT11122\nDetails: 30% off on footwear. Valid until 31st December 2024.";
                            break;
                        case "10":
                            messageCoupon = "Brand: Debenhams\nCoupon Code: DEB33344\nDetails: 10% off on home and furniture. Valid until 31st December 2024.";
                            break;
                        default:
                            messageCoupon = "Invalid coupon code.";
                            break;
                    }
                    System.out.println("Message is: "+ messageCoupon);
                    messagingGateway.sendToPubsub(messageCoupon);

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