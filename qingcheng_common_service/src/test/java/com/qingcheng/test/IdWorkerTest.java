package com.qingcheng.test;

import com.qingcheng.util.IdWorker;

/**
 * Created by Administrator on 2019/6/7.
 */
public class IdWorkerTest {

    public static void main(String[] args){
        IdWorker idWorker = new IdWorker(1,2);

        for (int i=0;i<1000;i++){
            long id = idWorker.nextId();
            System.out.println("id = " + id);
        }
    }
}
