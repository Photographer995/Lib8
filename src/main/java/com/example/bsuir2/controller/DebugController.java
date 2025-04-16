package com.example.bsuir2.controller;

import com.example.bsuir2.service.InvocationCounter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    private final InvocationCounter invocationCounter;

    public DebugController(InvocationCounter invocationCounter) {
        this.invocationCounter = invocationCounter;
    }

    @GetMapping("/debug/counter")
    public long getInvocationCount() {
        return invocationCounter.getCounter();
    }
}
