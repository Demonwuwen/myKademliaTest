package com.demon.kademlia;

import cn.cas.xjipc.kademlia.connection.EmptyConnectionInfo;
import cn.cas.xjipc.kademlia.connection.SampleRepository;
import cn.cas.xjipc.kademlia.connection.TestMessageSenderAPI;
import io.ep2p.kademlia.NodeSettings;
import io.ep2p.kademlia.exception.DuplicateStoreRequest;
import io.ep2p.kademlia.model.LookupAnswer;
import io.ep2p.kademlia.model.StoreAnswer;
import io.ep2p.kademlia.node.DHTKademliaNode;
import io.ep2p.kademlia.node.DHTKademliaNodeAPI;
import io.ep2p.kademlia.node.KeyHashGenerator;
import io.ep2p.kademlia.table.Bucket;
import io.ep2p.kademlia.table.DefaultRoutingTableFactory;
import io.ep2p.kademlia.table.RoutingTableFactory;
import org.junit.jupiter.api.Assertions;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author demon
 * @create 2022-01-09-04:43
 */
public class DHTTest {
    public static void main(String[] args) throws DuplicateStoreRequest, ExecutionException, InterruptedException {
        testStore();
    }
    public static void testStore() throws ExecutionException, InterruptedException, DuplicateStoreRequest {
        TestMessageSenderAPI<Integer, EmptyConnectionInfo> messageSenderAPI = new TestMessageSenderAPI<>();

        NodeSettings.Default.IDENTIFIER_SIZE = 4;
        NodeSettings.Default.BUCKET_SIZE = 100;
        NodeSettings.Default.PING_SCHEDULE_TIME_VALUE = 5;
        NodeSettings nodeSettings = NodeSettings.Default.build();

        RoutingTableFactory<Integer, EmptyConnectionInfo, Bucket<Integer,EmptyConnectionInfo>> routingTableFactory = new DefaultRoutingTableFactory<>(nodeSettings);

        KeyHashGenerator<Integer,Integer> keyHashGenerator = new SampleKeyHashGenerator(NodeSettings.Default.IDENTIFIER_SIZE);

        //bootStrap node
        DHTKademliaNodeAPI<Integer,EmptyConnectionInfo,Integer,String> bootstrapNode = new DHTKademliaNode<>(0,new EmptyConnectionInfo(),routingTableFactory.getRoutingTable(0),messageSenderAPI,nodeSettings,new SampleRepository(),keyHashGenerator);
        messageSenderAPI.registerNode(bootstrapNode);
        bootstrapNode.start();

        //Other nodes
        for (int i=1;i<Math.pow(2,NodeSettings.Default.IDENTIFIER_SIZE);i++){
            DHTKademliaNodeAPI<Integer,EmptyConnectionInfo,Integer,String> nextNode = new DHTKademliaNode<>(i,new EmptyConnectionInfo(),routingTableFactory.getRoutingTable(i),messageSenderAPI,nodeSettings,new SampleRepository(),keyHashGenerator);
            messageSenderAPI.registerNode(nextNode);
            Assertions.assertTrue(nextNode.start(bootstrapNode).get(),"Failed to bootstrap the node with ID "+ i);
        }

        //Wait and test if all nodes join
        CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(()->{
            while (messageSenderAPI.map.size() <Math.pow(2,NodeSettings.Default.IDENTIFIER_SIZE)){
                //wait
            }
            countDownLatch.countDown();
        }).start();
        boolean await = countDownLatch.await(NodeSettings.Default.PING_SCHEDULE_TIME_VALUE +1, NodeSettings.Default.PING_SCHEDULE_TIME_UNIT);
        Assertions.assertTrue(await);

        System.out.println("All nodes tried registry in the right time");

        Thread.sleep(2000);

        Storedata(bootstrapNode, "Eleuth");
        for (int i = 0;i<10;i++){
            Storedata(bootstrapNode, UUID.randomUUID().toString());
        }

        String data2 = UUID.randomUUID().toString();
        Assertions.assertThrows(TimeoutException.class,()->bootstrapNode.store(data2.hashCode(),data2).get(1, TimeUnit.NANOSECONDS));
        System.out.println("Stopping all node!");
        messageSenderAPI.stopAll();
        System.out.println("Stopped all node!");
        System.exit(0);
    }

    private static void Storedata(DHTKademliaNodeAPI<Integer, EmptyConnectionInfo, Integer, String> node, String data) throws DuplicateStoreRequest, ExecutionException, InterruptedException {
        Future<StoreAnswer<Integer, Integer>> storeFuture = node.store(data.hashCode(), data);
        StoreAnswer<Integer, Integer> storeAnswer = storeFuture.get();
        Assertions.assertEquals(storeAnswer.getResult(), StoreAnswer.Result.STORED, "StoreAnswer Result was " + storeAnswer.getResult() + ", stored in node" + storeAnswer.getNodeId());
        System.out.println(storeAnswer.getNodeId() + " stored " + storeAnswer.getKey());
        System.out.println("data :"+data+"/data hash:"+data.hashCode()+",get data from node:");

        if (!storeAnswer.getNodeId().equals(node.getId()))
            Assertions.assertFalse(node.getKademliaRepository().contains(data.hashCode()));

        Future<LookupAnswer<Integer, Integer, String>> lookupFuture = node.lookup(data.hashCode());
        LookupAnswer<Integer, Integer, String> lookupAnswer = lookupFuture.get();

        Assertions.assertEquals(lookupAnswer.getResult(), LookupAnswer.Result.FOUND);
        Assertions.assertEquals(lookupAnswer.getValue(), data);
        Assertions.assertEquals(lookupAnswer.getNodeId(), storeAnswer.getNodeId());
        System.out.println(lookupAnswer.getNodeId() + " returned the data");
    }
}
