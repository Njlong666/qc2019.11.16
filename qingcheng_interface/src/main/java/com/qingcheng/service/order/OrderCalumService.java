package com.qingcheng.service.order;



import com.qingcheng.pojo.order.OrderCalum;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @Author: wy
 * @Date:2019/9/24 16:13
 */
public interface OrderCalumService {
    public void saveOrderCalum();

    public List<Map> getYesterdayCalum(String date1, String date2);
}
