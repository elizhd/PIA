package com.sust.pia.service;

import com.sust.pia.model.ToDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Package com.sust.pia.service
 * @Description
 * @Author Haodong Zhao
 * @Datetime 2019/7/5 15:14
 */
public interface IMBaseService<T> {
    int insert(T obj);

    int delete(Integer id);

    int update(T obj);

    ToDo findById(Integer id);

    List<T> findAllData(Integer userId, Integer offset,
                    Integer rows, String sort, String order);

    List<T> findAll(Integer userId);

    int count(Integer userId);
}
