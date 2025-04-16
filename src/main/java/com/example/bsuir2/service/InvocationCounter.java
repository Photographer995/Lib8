package com.example.bsuir2.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InvocationCounter {
    private final AtomicLong counter = new AtomicLong(0);

    // Метод вызывается только аспектом, синхронизация не нужна (AtomicLong потокобезопасен)
    public void increment() {
        counter.incrementAndGet();
    }

    public long getCounter() {
        return counter.get();
    }
}