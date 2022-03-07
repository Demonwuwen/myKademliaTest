package com.demon.kademlia;

import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.util.BoundedHashUtil;

/**
 * @author demon
 * @create 2022-01-09-04:48
 */
public class SampleKeyHashGenerator implements KeyHashGenerator<Integer,Integer> {
    private final int size;

    public SampleKeyHashGenerator(int size) {
        this.size = size;
    }

    @Override
    public Integer generateHash(Integer key) {
        return new BoundedHashUtil(size).hash(key, Integer.class);
    }
}
