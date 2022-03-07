package com.demon.helpers;

import io.ep2p.kademlia.repository.KademliaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SampleRepository implements KademliaRepository<Integer, String> {
    protected final Map<Integer, String> data = new HashMap<>();
    protected final Map<Integer, Set> data1 = new HashMap<>();

    @Override
    public void store(Integer key, String value) {
        data.putIfAbsent(key, value);
    }

    @Override
    public String get(Integer key) {
        return data.get(key);
    }

    @Override
    public void remove(Integer key) {
        data.remove(key);
    }

    @Override
    public boolean contains(Integer key) {
        return data.containsKey(key);
    }
}
