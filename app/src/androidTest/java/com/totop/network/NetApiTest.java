package com.totop.network;

import com.totop.model.DataRes;
import com.totop.model.Goods;

import junit.framework.TestCase;

public class NetApiTest extends TestCase {

    public void testFindGoods(){
        DataRes<Goods> dataRes = NetApi.findGoods(1, 1);
        assertEquals(10,dataRes.data.size());
    }
}