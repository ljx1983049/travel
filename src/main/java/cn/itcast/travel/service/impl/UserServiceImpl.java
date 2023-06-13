package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

/**
 * 用户service层，操作用户dao
 */
public class UserServiceImpl implements UserService {

    private UserDao dao = new UserDaoImpl();

    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public boolean regist(User user) {
        //1、根据用户名查询用户信息
        User u = dao.findByUsername(user.getUsername());

        if (u!=null){
            //用户名存在，注册失败
            return false;
        }
        //注册成功
        //设置激活吗，唯一字符串
        user.setCode(UuidUtil.getUuid());
        //设置激活状态
        user.setStatus("N");
        //2、保存用户信息
        dao.save(user);

        //3、激活邮件发送邮件正文  --》 地址:绝对路径：http://localhost/trave/activeUserService   正文：点击激活【黑马旅游网】
        //设置正文
        String content="<a href='http://localhost/travel/user/active?code="+user.getCode()+"'>点击激活【黑马旅游网】</a>";
        //发送邮件
        MailUtils.sendMail(user.getEmail(),content,"激活邮寄");

        return true;
    }

    //激活用户
    @Override
    public boolean findByCode(String code) {
        //1、根据激活码查询用户对象
        User user = dao.findBycode(code);

        if(user != null){
            //2、调用dao的修改激活码状态
            dao.updateStatus(user);
            return true;
        }

        return false;
    }

    /**
     * 登录的方法
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return dao.findByUsernameAndPassword(user.getUsername(),user.getPassword());
    }
}
