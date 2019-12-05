package com.pinyougou.freeMark.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class FreeMarkTest {
    @Test
    public void test() throws IOException, TemplateException {
        //1.得到配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //2.设置配置属性
        //2.1设置默认编码格式
        configuration.setDefaultEncoding("UTF-8");
        //2.2设置将生成文件的保存位置
        configuration.setDirectoryForTemplateLoading(new File("E:\\pinyougou\\freemarkDemo\\src\\main\\resources"));
        //3.得到模板对象
        Template template = configuration.getTemplate("index.ftl");
        FileWriter out = new FileWriter("E:\\item\\test.html");
        HashMap dataModal = new HashMap();
        dataModal.put("user","张三");
        dataModal.put("message","今天天气不要啊");
        List<Map> list = new ArrayList<>();
        HashMap map = new HashMap();
        map.put("name","陈嘉浩");
        map.put("sex","男");
        map.put("age",18);
        HashMap map1 = new HashMap();
        map1.put("name","王银生");
        map1.put("sex","男");
        map1.put("age",19);
        list.add(map);
        list.add(map1);
        dataModal.put("people",list);
        //存放日期类型
        dataModal.put("now",new Date());
        //存放数字类型
        dataModal.put("num",646545645);

        template.process(dataModal,out);
        out.close();
    }
}
