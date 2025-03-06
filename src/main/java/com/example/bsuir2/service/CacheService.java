package com.example.bsuir2.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    private final Map<Long, Object> cache = new ConcurrentHashMap<>();

    // Получить объект из кэша
    public Object getFromCache(Long id) {
        return cache.get(id);
    }

    // Добавить объект в кэш
    public void putInCache(Long id, Object object) {
        cache.put(id, object);
    }

    // Удалить объект из кэша
    public void removeFromCache(Long id) {
        cache.remove(id);
    }

    // Очистить кэш
    public void clearCache() {
        cache.clear();
    }
}
