package cn.itcast.travel.dao.impl;

import cn.itcast.travel.dao.RouteDao;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class RouteDaoImpl implements RouteDao {

    JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

    /**
     * 根据类别id 查询总记录条数
     * @param cid
     * @param rname
     * @return
     */
    @Override
    public int findTotalCount(int cid, String rname) {
//        String sql = "select count(*) from tab_route where cid = ?";
        //定义sql模板
        String sql = "select count(*) from tab_route where 1=1 ";
        StringBuilder sb = new StringBuilder(sql);

        List list = new ArrayList();//存储条件们
        if (cid != 0){
            sb.append(" and cid = ? ");
            list.add(cid);//添加对应的？值
        }
        if (rname != null && rname.length() > 0){
            sb.append("and rname like ? ");
            list.add("%"+rname+"%");
        }
        sql = sb.toString();

        return template.queryForObject(sql,Integer.class,list.toArray());
    }

    /**
     * 查找页面信息
     * @param cid
     * @param rname
     * @param start
     * @param pageSize
     * @return
     */
    @Override
    public List<Route> findByPage(int cid, String rname, int start, int pageSize) {
//        String sql = "select * from tab_route where cid = ? and rname = ? limit ? , ?";
        //定义sql模板
        String sql = "select * from tab_route where 1=1 ";
        StringBuilder sb = new StringBuilder(sql);

        List list = new ArrayList();//存储条件们
        if (cid != 0){
            sb.append(" and cid = ? ");
            list.add(cid);//添加对应的？值
        }
        if (rname != null && rname.length() > 0){
            sb.append("and rname like ? ");
            list.add("%"+rname+"%");
        }
        sb.append(" limit ? , ? ");//分页条件
        sql = sb.toString();

        list.add(start);
        list.add(pageSize);

        return template.query(sql, new BeanPropertyRowMapper<Route>(Route.class), list.toArray());
    }

    /**
     * 根据rid查找一个route对象
     * @param rid
     * @return
     */
    @Override
    public Route findOne(int rid) {
        String sql = "select * from tab_route where rid = ?";
        return template.queryForObject(sql,new BeanPropertyRowMapper<Route>(Route.class),rid);
    }

    //设置更新 tab_route 表的 count 字段
    @Override
    public void setCount(int count, int rid) {
        String sql = "UPDATE tab_route set count = ? where rid = ? ";
        template.update(sql,count,rid);
    }
}
