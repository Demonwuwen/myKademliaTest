package com.demon.kademlia;

import cn.cas.xjipc.kademlia.connection.EmptyConnectionInfo;
import cn.cas.xjipc.kademlia.connection.TestMessageSenderAPI;
import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.node.KademliaNode;
import io.ep2p.kademlia.node.KademliaNodeAPI;
import io.ep2p.kademlia.node.Node;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTableFactory;
import io.ep2p.kademlia.util.KadDistanceUtil;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

/**
 * @author demon
 * @create 2022-01-09-11:55
 */
public class NodeJoin {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        canPeersJoinNetwork();
    }

    public static void canPeersJoinNetwork() throws InterruptedException, ExecutionException {
        TestMessageSenderAPI<Integer, EmptyConnectionInfo> messageSenderAPI = new TestMessageSenderAPI<>();

        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;
        NodeSettings nodeSettings = NodeSettings.Default.build();

        RoutingTableFactory<Integer, EmptyConnectionInfo, Bucket<Integer, EmptyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);


        // Bootstrap Node
        System.out.println("bootstrap node ......");
        KademliaNodeAPI<Integer, EmptyConnectionInfo> bootstrapNode = new KademliaNode<>(0, new EmptyConnectionInfo(), routingTableFactory.getRoutingTable(0), messageSenderAPI, nodeSettings);
        messageSenderAPI.registerNode(bootstrapNode);
        bootstrapNode.start();

        // Other nodes
        KademliaNodeAPI<Integer, EmptyConnectionInfo> previousNode = bootstrapNode;
        for (int i = 1; i < Math.pow(2, NodeSettings.Default.IDENTIFIER_SIZE); i++) {
            KademliaNodeAPI<Integer, EmptyConnectionInfo> newNode = new KademliaNode<>(i, new EmptyConnectionInfo(), routingTableFactory.getRoutingTable(i), messageSenderAPI, nodeSettings);
            messageSenderAPI.registerNode(newNode);
            Assertions.assertTrue(newNode.start(previousNode).get(), "Failed to bootstrap the node with ID " + i);
            previousNode = newNode;
        }


        // Wait and test if all nodes join
        System.out.println("Wait and test if all nodes join");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(() -> {
            while (messageSenderAPI.map.size() < Math.pow(2, NodeSettings.Default.IDENTIFIER_SIZE)) {
                //wait
            }
            countDownLatch.countDown();
        }).start();
        boolean await = countDownLatch.await(NodeSettings.Default.PING_SCHEDULE_TIME_VALUE + 1, NodeSettings.Default.PING_SCHEDULE_TIME_UNIT);
        Assertions.assertTrue(await);

        System.out.println("All nodes tried registry in the right time");

        Thread.sleep(2000);

        // Test if nodes know about each other
        System.out.println("Test if nodes know about each other");
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(0)), 1, 2, 4, 8));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(0)));
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(1)), 0, 3, 5, 9));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(1)));
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(2)), 3, 0, 6, 10));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(2)));
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(3)), 2, 1, 7, 11));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(3)));
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(15)), 14, 13, 11, 7));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(15)));
        Assertions.assertTrue(listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(7)), 6, 5, 3, 15));
//        listContainsAll(KadDistanceUtil.getReferencedNodes(messageSenderAPI.map.get(7)));


        // stop all
        messageSenderAPI.stopAll();
    }
    private static boolean listContainsAll(List<Node<Integer, EmptyConnectionInfo>> referencedNodes, Integer... nodeIds){
        List<Integer> nodeIdsToContain = new ArrayList<>(Arrays.asList(nodeIds));
        for (Node<Integer, EmptyConnectionInfo> referencedNode : referencedNodes) {
            System.out.print(" "+referencedNode.getId());
            nodeIdsToContain.remove(referencedNode.getId());
        }
        System.out.println();
        return nodeIdsToContain.size() == 0;
    }
}
