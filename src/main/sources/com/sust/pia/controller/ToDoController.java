package com.sust.pia.controller;

import com.alibaba.fastjson.JSONObject;
import com.sust.pia.model.DataList;
import com.sust.pia.model.ToDo;
import com.sust.pia.model.User;
import com.sust.pia.service.IToDoService;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Package com.sust.pia.controller
 * @Description
 * @Author Haodong Zhao
 * @Datetime 2019/7/5 18:43
 */
@Controller
@RequestMapping("/todo")
public class ToDoController {

    @Resource(name = "toDoService")
    IToDoService toDoService;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/5 19:03
     * @Param out
     * @Param response
     * @Return void
     */
    private void writeJSON2Response(Object out, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        try {
            log.debug("SERVER[ToDoController] to Front End: " + out);
            response.getWriter().print(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/5 19:04
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
        log.debug("SERVER[ToDoController] Get user: " + user);
        int size = toDoService.count(user.getId());
        DataList<ToDo> toDoList = new DataList<>();
        List<ToDo> list = toDoService.findAllData(user.getId(), offset, limit, sort, order);
        if (list != null) {
            toDoList.setRows(list);
            toDoList.setTotal(size);
        }
        writeJSON2Response(toDoList.toString(), response);
    }

    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/5 23:47
     * @Param toDo
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/insertData")
    @ResponseBody
    public void insertData(@RequestBody ToDo toDo, HttpServletRequest request,
                           HttpServletResponse response) {
        toDo.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        log.debug("SERVER[ToDoController] Get ToDo: " + toDo.toString());
        JSONObject result = new JSONObject();
        if (toDoService.insert(toDo) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }


    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/6 13:32
     * @Param ids
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/deleteByIds")
    @ResponseBody
    public void deleteByIds(@RequestParam(value = "ids") String ids, HttpServletResponse response) {
        log.debug("SERVER[ToDoController] Get ids: " + ids);
        String[] idArray = ids.split(",");
        JSONObject result = new JSONObject();
        try {
            for (String id : idArray)
                toDoService.delete(Integer.valueOf(id));
            result.put("flag", true);
        } catch (Exception e) {
            result.put("flag", false);
        }
        writeJSON2Response(result, response);
    }

    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/6 13:49
     * @Param toDo
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/updateData")
    @ResponseBody
    public void updateData(@RequestBody ToDo toDo,
                           HttpServletResponse response, HttpServletRequest request) {
        toDo.setUserId(((User) request.getSession().getAttribute("userObj")).getId());
        JSONObject result = new JSONObject();
        if (toDoService.update(toDo) > 0)
            result.put("flag", true);
        else
            result.put("flag", false);
        writeJSON2Response(result, response);
    }


    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/6 15:01
     * @Param startDate
     * @Param endDate
     * @Param request
     * @Param response
     * @Return void
     */
    @PostMapping(value = "/findByDate")
    @ResponseBody
    public void findByTime(@RequestParam(value = "startDate") String startDate,
                           @RequestParam(value = "endDate") String endDate,
                           HttpServletRequest request, HttpServletResponse response) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User user = (User) request.getSession().getAttribute("userObj");
        endDate = endDate.replaceAll("/", "-");
        startDate = startDate.replaceAll("/", "-");
        log.debug("SERVER[ToDoController-findByDate] Get user: " + user);
        DataList<ToDo> toDoList = new DataList<>();
        try {
            List<ToDo> list = toDoService.findByTime(format.parse(startDate), format.parse(endDate), user.getId());
            if (list != null) {
                toDoList.setRows(list);
                toDoList.setTotal(list.size());
            }
        } catch (Exception e) {
            log.error("SERVER[ToDoController]: " + e.toString());

        }

        writeJSON2Response(toDoList.toString(), response);
    }

    /**
     * @Description
     * @Author Haodong Zhao
     * @Date 2019/7/6 15:39
     * @Param keyword
     * @Param response
     * @Param request
     * @Return void
     */
    @PostMapping(value = "/findByKeyWord")
    @ResponseBody
    public void findByKeyWord(@RequestParam(value = "keyword") String keyword,
                              HttpServletResponse response, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("userObj");
        DataList<ToDo> toDoList = new DataList<>();
        try {
            List<ToDo> list = toDoService.findByKeyword(user.getId(), keyword);
            toDoList.setRows(list);
            toDoList.setTotal(list.size());

        } catch (Exception e) {
            log.error("SERVER[ToDoController]: " + e.toString());

        }
        writeJSON2Response(toDoList.toString(), response);


    }


}