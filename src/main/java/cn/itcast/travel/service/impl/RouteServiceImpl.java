package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.FavoriteDao;
import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.dao.RouteImgDao;
import cn.itcast.travel.dao.SellerDao;
import cn.itcast.travel.dao.impl.FavoriteDaoImpl;
import cn.itcast.travel.dao.impl.RouteDaoImpl;
import cn.itcast.travel.dao.impl.RouteImgDaoImpl;
import cn.itcast.travel.dao.impl.SellerDaoImpl;
import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.RouteImg;
import cn.itcast.travel.domain.Seller;
import cn.itcast.travel.service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {

    private RouteDao routeDao = new RouteDaoImpl();
    private RouteImgDao routeImgDao = new RouteImgDaoImpl();
    private SellerDao sellerDao = new SellerDaoImpl();
    private FavoriteDao favoriteDao = new FavoriteDaoImpl();

    /**
     * 调用dao查询页面信息，封装PageBean
     * @param cid
     * @param currentPage
     * @param pageSize
     * @param rname
     * @return
     */
    @Override
    public PageBean<Route> pageQuery(int cid, int currentPage, int pageSize, String rname) {
        //1、封装PageBean
        PageBean<Route> pb = new PageBean<>();
        //设置总记录数
        int totalCount = routeDao.findTotalCount(cid,rname);//根据cid和rname查询总记录数
        pb.setTotalCount(totalCount);
        //设置总页数
        int totalPage = totalCount % pageSize == 0 ? totalCount/pageSize : (totalCount/pageSize)+1 ;
        pb.setTotalPage(totalPage);
        //设置当前页码
        pb.setCurrentPage(currentPage);
        //设置每页显示条数
        pb.setPageSize(pageSize);
        //设置每页显示的数据集合
        int start = (currentPage-1)*pageSize;//开始的记录数
        List<Route> list = routeDao.findByPage(cid,rname,start,pageSize);//根据所需页面信息查找页面显示的数据集合
        pb.setList(list);

        return pb;
    }

    /**
     * 调用dao封装route对象
     * @param rid
     * @return
     */
    @Override
    public Route findOne(String rid) {
        //1、调用routeDao 根据rid查询route对象
        Route route = routeDao.findOne(Integer.parseInt(rid));

        //2、调用RouteImgDao 根据rid查询 商品详情图片列表
        List<RouteImg> routeImgList = routeImgDao.findByRid(route.getRid());
        //将List设置到route对象的routeImgList
        route.setRouteImgList(routeImgList);

        //3、调用SellerDao 根据sid查询 所属商家
        Seller seller = sellerDao.findBySid(route.getSid());
        //将seller设置到route对象的seller
        route.setSeller(seller);

        //4、设置收藏次数 count
        //根据rid统计count(*) 获取tab_favorite表的收藏次数
        int count = favoriteDao.findCountByRid(route.getRid());
        //设置route对象的count
        route.setCount(count);
        //更新设置数据库表tab_route 的 count
        routeDao.setCount(count,route.getRid());

        return route;
    }
}
