package com.sust.pia.controller;

import com.alibaba.fastjson.JSONObject;
import com.sust.pia.model.Pass;
import com.sust.pia.model.DataList;
import com.sust.pia.model.User;
import com.sust.pia.service.IPassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @Package com.sust.pia.controller
 * @Description 密码管理Controller层
 * @Author Haodong Zhao
 * @Datetime 2019/7/7 14:03
 */
@Controller
@RequestMapping("/pass")
public class PassController {

    @Resource(name = "passService")
    IPassService passService;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * @Description 传输JSON数据至前端
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:20
     * @Param out JSON数据
     * @Param response
     * @Return void
     */
    private void writeJSON2Response(Object out, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            log.debug("SERVER to Front End: " + out);
            response.getWriter().print(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description 分页查询数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:20
     * @Param limit
     * @Param offset
     * @Param sort
     * @Param order
     * @Param request
     * @Param response
     * @Return void
     */
    @GetMapping(value = "/getAllInfo")
    @ResponseBody
    public void getAllInfo(@RequestParam(value = "limit", defaultValue = "10") Integer limit,
                           @RequestParam(value = "offset", defaultValue = "1") Integer offset,
                           @RequestParam(value = "sort") String sort,
                           @RequestParam(value = "order") String order,
                           HttpServletRequest request, HttpServletResponse response) {
        User user = (User) request.getSession().getAttribute("userObj");
        log.debug("SERVER Get user: " + user);
        int size = passService.count(user.getId());
        DataList<Pass> passList = new DataList<>();
        List<Pass> list = passService.findAllData(user.getId(), offset, limit, sort, order);
        if (list != null) {
            passList.setRows(list);
            passList.setTotal(size);
        }
        writeJSON2Response(passList.toString(), response);
    }


    /**
     * @Description 插入数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:20
     * @Param pass 密码Pass对象
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/insertData")
    @ResponseBody
    public void insertData(@RequestBody Pass pass, HttpServletRequest request,
                           HttpServletResponse response) {
        pass.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        log.debug("SERVER Get Pass: " + pass.toString());
        JSONObject result = new JSONObject();
        if (passService.insert(pass) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }


    /**
     * @Description 根据id删除数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:21
     * @Param ids 指定地id
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/deleteByIds")
    @ResponseBody
    public void deleteByIds(@RequestParam(value = "ids") String ids, HttpServletResponse response) {
        log.debug("SERVER Get ids: " + ids);
        JSONObject result = new JSONObject();
        String[] idArray = ids.split(",");
        try {
            for (String id : idArray)
                passService.delete(Integer.valueOf(id));
            result.put("flag", true);
        } catch (Exception e) {
            result.put("flag", false);
        }
        writeJSON2Response(result, response);
    }


    /**
     * @Description 更新数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:21
     * @Param pass 密码Pass对象
     * @Param response
     * @Param request
     * @Return void
     */
    @PostMapping(value = "/updateData")
    @ResponseBody
    public void updateData(@RequestBody Pass pass,
                           HttpServletResponse response, HttpServletRequest request) {
        pass.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        JSONObject result = new JSONObject();
        if (passService.update(pass) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }


    /**
     * @Description 根据网站名查询密码
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:21
     * @Param serachWebsite 搜索的网站名
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/findByWebsite")
    @ResponseBody
    public void findByName(@RequestParam(value = "searchWebsite") String serachWebsite,
                           HttpServletRequest request, HttpServletResponse response) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = (User) request.getSession().getAttribute("userObj");
        log.debug("SERVER Get user: " + user);
        DataList<Pass> passList = new DataList<>();
        try {
            List<Pass> list = passService.findByWebsite(user.getId(), serachWebsite);
            if (list != null) {
                passList.setRows(list);
                passList.setTotal(list.size());
            }
        } catch (Exception e) {
            log.error("SERVER " + e.toString());

        }

        writeJSON2Response(passList.toString(), response);
    }

}
