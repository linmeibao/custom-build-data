package nny.build.data.builder.utils;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Random;

/**
 * 随机数据生成
 *
 * @author shengyong.huang

 * @date 2019/9/5
 */
public class RandomDataUtils {

    /**
     * 随机字符串 19位
     *
     * @return 数字与大小写字母
     */
    public static String randomString() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 19; i++) {
            boolean boo = (int) (Math.random() * 2) == 0;
            if (boo) {
                code.append(String.valueOf((int) (Math.random() * 10)));
            } else {
                int temp = (int) (Math.random() * 2) == 0 ? 65 : 97;
                char ch = (char) (Math.random() * 26 + temp);
                code.append(String.valueOf(ch));
            }
        }
        return code.toString();
    }

    /**
     * 随机时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String randomDateString() {
        Calendar calendar = Calendar.getInstance();
        //注意月份要减去1
        calendar.set(2016, 11, 31);
        calendar.getTime().getTime();
        //根据需求，这里要将时分秒设置为0
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long min = calendar.getTime().getTime();
        ;
        calendar.set(2019, 11, 31);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.getTime().getTime();
        long max = calendar.getTime().getTime();
        //得到大于等于min小于max的double值
        double randomDate = Math.random() * (max - min) + min;
        //将double值舍入为整数，转化成long类型
        calendar.setTimeInMillis(Math.round(randomDate));
//        return calendar.getTime();

        return DateFormatUtils.format(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
    }

//    public static void main(String[] args) {
//        Object object = randomDateString();
//        System.out.println(object);
//    }

    /**
     * 随机金额
     *
     * @return
     */
    public static double randomMoney() {
        BigDecimal bigDecimal = new BigDecimal(Math.random() * (10000 + 1));
        return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 随机名字
     *
     * @return
     */
    public static String randomName() {
        //部分比较常见的姓氏
        String[] Surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛",
                "奚", "范", "鲁", "韦", "昌", "马", "苗", "花", "方", "俞", "任", "袁", "柳", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤",
                "滕", "殷", "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "黄", "和",
                "彭", "郎", "穆", "萧", "尹", "姚", "邵", "汪", "祁", "毛", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊",
                "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "危", "江", "童", "颜", "郭", "梅",
                "盛", "林", "刁", "钟", "徐", "邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "管", "卢", "莫", "经", "房",
                "裘", "缪", "干", "解", "应", "宗", "丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇",
                "邢", "滑", "裴", "陆", "荣", "翁", "荀", "于", "惠", "甄", "曲", "封", "羿", "储", "靳", "井", "段", "富", "巫", "乌", "焦", "巴", "牧", "山",
                "谷", "车", "侯", "全", "班", "仰", "秋", "仲", "伊", "宫", "宁", "仇", "栾", "甘", "厉", "祖", "武", "符", "刘", "景", "詹", "束", "龙", "叶",
                "幸", "司", "韶", "黎", "蓟", "溥", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "赖", "卓", "蔺", "屠", "蒙", "池", "乔", "郁",
                "胥", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却", "桑", "桂", "濮", "牛",
                "边", "扈", "燕", "冀", "浦", "尚", "温", "庄", "晏", "柴", "瞿", "阎", "慕", "连", "习", "艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖",
                "庾", "终", "暨", "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙", "欧", "沃", "利", "蔚", "越", "隆", "师", "巩",
                "厍", "聂", "晁", "勾", "敖", "融", "冷", "辛", "阚", "那", "简", "饶", "空", "曾", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "相", "查",
                "后", "荆", "游", "权", "岳", "帅", "海", "晋", "楚", "闫", "鄢", "涂", "钦", "商", "牟", "佘", "墨", "哈", "年", "阳", "佟", "言", "福", "南",
                "铁", "迟", "漆", "官", "冼", "舜", "楼", "高", "皋", "原", "练", "覃", "木", "麦", "邝", "司马", "上官", "欧阳", "夏侯", "诸葛", "东方",
                "赫连", "皇甫", "尉迟", "公孙", "轩辕", "令狐", "宇文", "长孙", "慕容", "司徒", "呼延", "端木"};
        String name = null;
        int highPos, lowPos;
        Random random = new Random();
        //区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字
        highPos = (176 + Math.abs(random.nextInt(72)));
        random = new Random();
        //位码，0xA0打头，范围第1~94列
        lowPos = 161 + Math.abs(random.nextInt(94));

        byte[] bArr = new byte[2];
        bArr[0] = (new Integer(highPos)).byteValue();
        bArr[1] = (new Integer(lowPos)).byteValue();
        try {
            //区位码组合成汉字
            int index = random.nextInt(Surname.length - 1);
            //new String(bArr, "GB2312")：区位码组合成汉字，获得一个随机的姓氏
            name = Surname[index] + new String(bArr, "GB2312");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * 随机数字 32位
     *
     * @return
     */
    public static String randomNumber() {
        String val = "";
        Random random = new Random();
        //循环4次，4*8=32
        for (int j = 0; j < 4; j++) {
            //一次8位数
            for (int num = 0; num < 8; num++) {
                val += String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }

    /**
     * 随机手机号码
     *
     * @return
     */
    public static String randomMobilePhone() {
        String firstNum = "1";
        String[] secondNumArray = {"3", "4", "5", "7", "8"};

        StringBuffer sb = new StringBuffer();

        Random random = new Random();

        String secondNum = secondNumArray[random.nextInt(secondNumArray.length)];

        sb.append(firstNum);
        sb.append(secondNum);

        Integer thirdNum = 1 + random.nextInt(999999999);

        if (thirdNum.toString().length() <= 9) {

            sb.append(thirdNum);

            for (int i = 1; i <= 9 - thirdNum.toString().length(); i++) {
                sb.append(0);
            }

        } else {

            sb.append(thirdNum.toString());

        }

        return sb.toString();
    }

    public static Integer numberFormat(Integer value){
        String str = String.valueOf(value);
        String sub = String.valueOf(value).substring(0,str.length() -2)+"00";
        return Integer.valueOf(sub);
    }
}
