package com.pinyougou.manager.controller;

import com.pinyougou.pojo.Result;
import com.pinyougou.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by WF on 2019-11-13 10:58
 */
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;         //得到文件路径

    //1.文件上传
    @RequestMapping("upload")
    public Result upload(MultipartFile file) throws Exception {
        try {
            //1.1)得到上传的原始的文件名
            String originalFilename = file.getOriginalFilename();
            //1.2)得到文件的后缀名
            //1.2.1)得到最后一个.的下标
            int index = originalFilename.lastIndexOf(".");
            //1.2.2)得到文件的后缀名
            String extName = originalFilename.substring(index+1);
            //1.3)构造fastDFSClient对象
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fastdfs.conf");
            //1.4)进行文件上传,返回的是url地址
            String url = fastDFSClient.uploadFile(file.getBytes(), extName);
            url = FILE_SERVER_URL + url;
            System.out.println("url = "  + url);
            //1.5)返回
            return new Result(true,url);
        } catch (Exception e) {
            return new Result(false,"上传文件失败！");
        }

    }
}
