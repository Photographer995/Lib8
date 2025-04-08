package com.example.bsuir2.service;

import org.springframework.stereotype.Service;

@Service
public class InvocationCounter {

    private long counter = 0;

    public synchronized void increment() {
        counter++;
    }

    public synchronized long getCounter() {
        return counter;
    }
}
