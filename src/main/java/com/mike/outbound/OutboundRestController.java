package com.mike.outbound;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
@Slf4j
public class OutboundRestController {
    private OutboundConfiguration.PubsubOutboundGateway messagingGateway;

    @PostMapping("/")
    public void sendMessage(@RequestBody String message){
        System.out.println("Send this message to outbound channel" + message);
        messagingGateway.sendToPubsub(message);
    }

}
