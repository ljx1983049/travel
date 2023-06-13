package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {

    private RouteService routeService = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();

    /**
     * 分页查询
     * @param request
     * @param response
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //1、获取参数
        String cidstr = request.getParameter("cid");//导航条的类别id
        String currentPagestr = request.getParameter("currentPage");//当前页码
        String pageSizestr = request.getParameter("pageSize");//每页显示条数
        //获取rname 线路名称，可以进行模糊查询
        String rname = request.getParameter("rname");
        //处理参数
        int cid = 0;
        if(cidstr !=null && cidstr.length() >0 && !"null".equals(cidstr)){
            cid = Integer.parseInt(cidstr);
        }
        int currentPage = 1 ;//如果不传递则默认为第一页
        if(currentPagestr !=null && currentPagestr.length() >0) {
            currentPage = Integer.parseInt(currentPagestr);
        }
        int pageSize = 5 ;//如果不传递则默认为每页显示5条
        if(pageSizestr !=null && pageSizestr.length() >0){
            pageSize = Integer.parseInt(pageSizestr);
        }
        if(rname !=null && rname.length() >0) {
            rname = new String(rname.getBytes("iso-8859-1"), "utf-8");
        }else{
            rname = null ;
        }

        //2、调用service查询pageBean对象
        PageBean<Route> pb = routeService.pageQuery(cid,currentPage,pageSize,rname);

        //3、将pagebean 对象序列化为json，返回
        writeValue(response,pb);

    }

    /**
     * 查找一个旅游线路的详情信息
     * @param request
     * @param response
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //1、获取rid
        String rid = request.getParameter("rid");
        //2、调用service查询
        //根据rid查询route对象
        Route route = routeService.findOne(rid);

        //3、序列化为json并返回
        writeValue(response,route);
    }

    /**
     * 判断用户是否收藏
     * @param request
     * @param response
     * @throws IOException
     */
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //1、获取rid
        String rid = request.getParameter("rid");

        //2、从session获取当前登录用户对象
        User user = (User) request.getSession().getAttribute("user");
        int uid ;
        if (user == null){
            //用户没有登录
            uid = 0;//让没有登录数据库查询不到
        }else {
            //用户已经登录
            uid = user.getUid();
        }

        //3、调用FavoriteService查询，判断用户是否收藏,没有查询到，则无收藏
        boolean flag = favoriteService.isFavorite(rid,uid);

        //4、写回客户端
        writeValue(response,flag);

    }

    /**
     * 添加收藏
     * @param request
     * @param response
     * @throws IOException
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws IOException{
        String rid = request.getParameter("rid");
        //2、从session获取当前登录用户对象
        User user = (User) request.getSession().getAttribute("user");
        int uid;
        if (user == null){
            //用户尚未登录
            return;
        }else {
            //用户已登录
            uid = user.getUid();
        }
        //调用service查询
        favoriteService.addFavorite(rid,uid);
    }

}

