package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

public interface UserService {
    /**
     * @author: zhangyu
     * @date: 2019-11-27
     * @param: [userName]
     * @return: boolean
     * 功能描述: 判断用户名是否存在
     */
    boolean hasUserName(String userName);

    /**
     * @author: zhangyu
     * @date: 2019-11-27
     * @param: [phone]
     * @return: void
     * 功能描述: 获取验证。并存入redis     
     */
    void getValidCode(String phone);

    /**
     * @author: zhangyu
     * @date: 2019-11-27
     * @param: [phone, validCode]
     * @return: boolean
     * 功能描述: 判读用户输入验证码与redis中验证码是否一致
     */
    boolean checkValidCode(String phone, String validCode);

    /**
     * @author: zhangyu
     * @date: 2019-11-27
     * @param: [user]
     * @return: void
     * 功能描述:    添加用户到数据库，即完成用户注册功能
     */
    void add(TbUser user);
}
