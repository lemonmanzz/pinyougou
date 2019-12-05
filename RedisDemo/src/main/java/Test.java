import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-redis.xml")
public class Test {
    @Autowired
    private RedisTemplate redisTemplate;

    @org.junit.Test
    public void test1(){
        Object o = redisTemplate.boundHashOps("zz").get("yy");
        System.out.println(o.toString());
    }
}
