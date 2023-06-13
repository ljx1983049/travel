package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 管理用户的类
 */
@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private UserService service = new UserServiceImpl();

    /**
     * 注册用户的功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void register(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        //获取验证码
        String reg_check = request.getParameter("check");
        //获取session里的验证码
        HttpSession session = request.getSession();
        String sess_check = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");

        if (sess_check == null || !sess_check.equalsIgnoreCase(reg_check)){
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");

            //将info对象装换为json
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);

            //将json数据写回客户端
            //设置ContentType
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);

            return;
        }

        //获取参数
        Map<String, String[]> map = request.getParameterMap();

        //封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //调用service完成注册
        boolean flag = service.regist(user);

        ResultInfo info = new ResultInfo();
        //响应结果
        if(flag){
            //注册成功
            info.setFlag(true);
        }else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败!该用户名以存在");
        }

        //将info对象装换为json
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

        //将json数据写回客户端
        //设置ContentType
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }

    /**
     * 用户使用邮箱激活的功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取激活码
        String code = request.getParameter("code");
        //根据激活码查询用户
        boolean flag = service.findByCode(code);

        //判断标记
        String msg = null;
        if (flag){
            //激活成功
            msg = "激活成功，请<a href='../login.html'>登录</a>";
        }else {
            //激活失败
            msg = "激活失败，请联系管理员";
        }

        response.setContentType("text/html;charset=utf-8");
        response.getWriter().write(msg);
    }

    /**
     * 用户登录功能
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        //获取用户名和密码
        Map<String, String[]> map = request.getParameterMap();
        //封装user对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //调用service查询
        User u = service.login(user);

        ResultInfo info = new ResultInfo();
        if (u == null){
            //登录失败
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误");
        }

        if(u != null && !"Y".equals(u.getStatus())){
            //用户没有激活
            info.setFlag(false);
            info.setErrorMsg("您尚未激活，请在邮箱激活！");
        }

        if(u != null && "Y".equals(u.getStatus())){
            //登录成功
            info.setFlag(true);
            //将用户对象存入session中，方便返回用户姓名
            request.getSession().setAttribute("user",u);
        }

        //响应数据
        ObjectMapper mapper = new ObjectMapper();

        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),info);
    }

    /**
     * 查询用户姓名显示到欢迎用户标题
     * @param request
     * @param response
     */
    public void findName(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //从session获取用户
        Object user = request.getSession().getAttribute("user");
        //将用户写回客户端
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),user);
    }

    /**
     * 退出的功能
     * @param request
     * @param response
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //销毁session
        request.getSession().invalidate();

        //跳转登录界面
        response.sendRedirect(request.getContextPath()+"/login.html");
    }


}
