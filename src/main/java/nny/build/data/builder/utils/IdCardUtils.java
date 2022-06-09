package nny.build.data.builder.utils;

import org.apache.commons.lang3.RandomUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/****
 * 随机生成身份证号
 *
 *
 */
public class IdCardUtils {

    /***
     *
     * 随机生成身份证
     * @return
     */
    public static String getIdNo() {/*随机生成生日 1~99岁*/
        boolean male = RandomUtils.nextBoolean();
        return getIdNo(male);
    }

    /****
     * 随机生成身份证，确定性别
     * @param male
     * @return
     */
    public static String getIdNo(boolean male) {/*随机生成生日 1~99岁*/
        long begin = System.currentTimeMillis() - 3153600000000L;/*100年内*/
        long end = System.currentTimeMillis() - 31536000000L; /*1年内*/
        long rtn = begin + (long) (Math.random() * (end - begin));
        Date date = new Date(rtn);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String birth = simpleDateFormat.format(date);
        return getIdNo(birth, male);
    }

    /****
     * 随机生成身份证号，性别和出生年月确定
     * @param birth
     * @param male
     * @return
     */
    public static String getIdNo(String birth, boolean male) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        int value = random.nextInt(Cities.CITIES.length);
        sb.append(Cities.CITIES[value]).append(birth);
        // TODO BUG 999 + 1有问题
        value = random.nextInt(998) + 1;
        if (male && value % 2 == 0) value++;
        if (!male && value % 2 == 1) {
            value++;
        }
        if (value >= 100) {
            sb.append(value);
        } else if (value >= 10) {
            sb.append('0').append(value);
        } else {
            sb.append("00").append(value);
        }
        sb.append(calcTrailingNumber(sb));
        return sb.toString();
    }

    private static final int[] calcC = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] calcR = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /*
     * <p>18位身份证验证</p>
     * 根据〖中华人民共和国国家标准 GB 11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
     * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 第十八位数字(校验码)的计算方法为：
     * 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 2.将这17位数字和系数相乘的结果相加。
     * 3.用加出来和除以11，看余数是多少？
     * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2。
     * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
     */
    private static char calcTrailingNumber(StringBuilder sb) {
        int[] n = new int[17];
        int result = 0;
        for (int i = 0; i < n.length; i++) {
            n[i] = Integer.parseInt(String.valueOf(sb.charAt(i)));
        }
        for (int i = 0; i < n.length; i++) {
            result += calcC[i] * n[i];
        }
        return calcR[result % 11];
    }

    public static void main(String[] args) {
//        long a = System.currentTimeMillis();
//        System.out.println(getIdNo("19790306", true));
//        System.out.println(getIdNo("20100112", false));
//        System.out.println(getIdNo(true));
//        System.out.println(getIdNo(false));
//        a = System.currentTimeMillis() - a;
//        System.out.println(a);
//        for (int i = 0; i < 100000; i++) {
//            String id = getIdNo();
//            if (id.length() > 18){
//                System.out.println(id);
//            }
//        }
   }
}