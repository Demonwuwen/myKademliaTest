package com.demon.kademlia.table;

import io.ep2p.kademlia.exception.FullBucketException;
import io.ep2p.kademlia.model.FindNodeAnswer;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.RoutingTable;

import java.util.Vector;

/**
 * @author demon
 * @create 2022-01-07-10:54
 */
public class MyRoutingTable implements RoutingTable {
    @Override
    public Number getIdInPrefix(Number number, int i) {
        return null;
    }

    @Override
    public int getNodePrefix(Number number) {
        return 0;
    }

    @Override
    public Bucket findBucket(Number number) {
        return null;
    }

    @Override
    public boolean update(Node node) throws FullBucketException {
        return false;
    }

    @Override
    public void forceUpdate(Node node) {

    }

    @Override
    public void delete(Node node) {

    }

    @Override
    public FindNodeAnswer findClosest(Number number) {
        return null;
    }

    @Override
    public boolean contains(Number number) {
        return false;
    }

    @Override
    public Vector getBuckets() {
        return null;
    }
}
