package cn.itcast.travel.dao;

import cn.itcast.travel.domain.Route;

import java.util.List;

public interface RouteDao {
    int findTotalCount(int cid, String rname);

    List<Route> findByPage(int cid, String rname, int start, int pageSize);

    Route findOne(int parseInt);

    void setCount(int count, int rid);
}
