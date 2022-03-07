package util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author demon
 * @create 2022-02-25-13:49
 */
public class hutoolTest {
    @Test
    public void test() throws ParseException {
        int a = 1;

        System.out.println(Convert.toStr(a).getClass());
        System.out.println();

        long[] lb = {1,2,3,4,5};
        String lbStr = Convert.toStr(lb);
        System.out.println(lbStr);

        //日期转换
        String str = "2020-2-2";
        Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(str);
        Date date = Convert.toDate(str);
        System.out.println("pars"+parse);
        System.out.println("date"+date);

        //数组集合转换
        System.out.println("数组集合转换");
        String[]  aaa = {"aaa","dage","erdi","abbb"};

        List<String> strings = Arrays.asList(aaa);
        List<String> objects = (List<String>) Convert.toList(aaa);
        objects.forEach(s -> System.out.println(s));

        BufferedInputStream in = FileUtil.getInputStream("d:/test.txt");
        BufferedOutputStream out = FileUtil.getOutputStream("d:/test2.txt");
        long copySize = IoUtil.copy(in, out, IoUtil.DEFAULT_BUFFER_SIZE);
    }

}
