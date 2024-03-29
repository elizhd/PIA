package com.sust.pia.controller;

import com.alibaba.fastjson.JSONObject;
import com.sust.pia.model.Contact;
import com.sust.pia.model.DataList;
import com.sust.pia.model.User;
import com.sust.pia.service.IContactService;
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
 * @Description 联系人管理Controller层
 * @Author Haodong Zhao
 * @Datetime 2019/7/7 0:27
 */
@Controller
@RequestMapping("/contact")
public class ContactController {

    @Resource(name = "contactService")
    IContactService contactService;
    private Logger log = LoggerFactory.getLogger(this.getClass());


    /**
     * @Description 传输JSON数据至前端
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:15
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
     * @Date 2019/7/9 18:15
     * @Param limit
     * @Param offset
     * @Param sort 指定顺序
     * @Param order 排序关键字
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
        int size = contactService.count(user.getId());
        DataList<Contact> contactList = new DataList<>();
        List<Contact> list = contactService.findAllData(user.getId(), offset, limit, sort, order);
        if (list != null) {
            contactList.setRows(list);
            contactList.setTotal(size);
        }
        writeJSON2Response(contactList.toString(), response);
    }


    /**
     * @Description 插入数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:16
     * @Param contact 联系人Contact对象
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/insertData")
    @ResponseBody
    public void insertData(@RequestBody Contact contact, HttpServletRequest request,
                           HttpServletResponse response) {
        contact.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        log.debug("SERVER Get Contact: " + contact.toString());
        JSONObject result = new JSONObject();
        if (contactService.insert(contact) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }


    /**
     * @Description 删除指定id数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:16
     * @Param ids 指定的id
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
                contactService.delete(Integer.valueOf(id));
            result.put("flag", true);
        } catch (Exception e) {
            result.put("flag", false);
        }
        writeJSON2Response(result, response);
    }

    /**
     * @Description 更新数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:16
     * @Param contact 联系人Contact对象
     * @Param response
     * @Param request
     * @Return void
     */
    @PostMapping(value = "/updateData")
    @ResponseBody
    public void updateData(@RequestBody Contact contact,
                           HttpServletResponse response, HttpServletRequest request) {
        contact.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        JSONObject result = new JSONObject();
        if (contactService.update(contact) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }

    /**
     * @Description 查询指定姓名地联系人信息
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:16
     * @Param serachName 指定搜索姓名
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/findByName")
    @ResponseBody
    public void findByName(@RequestParam(value = "searchName") String serachName,
                           HttpServletRequest request, HttpServletResponse response) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = (User) request.getSession().getAttribute("userObj");
        log.debug("SERVER Get user: " + user);
        DataList<Contact> contactList = new DataList<>();
        try {
            List<Contact> list = contactService.findByName(user.getId(), serachName);
            if (list != null) {
                contactList.setRows(list);
                contactList.setTotal(list.size());
            }
        } catch (Exception e) {
            log.error("SERVER " + e.toString());

        }

        writeJSON2Response(contactList.toString(), response);
    }

    /**
     * @Description 根据关键字查找数据
     * @Author Haodong Zhao
     * @Date 2019/7/9 18:16
     * @Param keyword 关键字
     * @Param response
     * @Param request
     * @Return void
     */
    @PostMapping(value = "/findByKeyWord")
    @ResponseBody
    public void findByKeyWord(@RequestParam(value = "keyword") String keyword,
                              HttpServletResponse response, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("userObj");
        DataList<Contact> contactList = new DataList<>();
        try {
            List<Contact> list = contactService.findByKeyword(user.getId(), keyword);
            contactList.setRows(list);
            contactList.setTotal(list.size());

        } catch (Exception e) {
            log.error("SERVER " + e.toString());

        }
        writeJSON2Response(contactList.toString(), response);


    }


}
