package com.martin.core.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by DingJinZhu on 2019/12/2.
 * Description:
 */
public class DictionaryUtils {

    //获取中文的首字母
    public static String getFirstLetter(String ChineseLanguage){
        char[] cl_chars = ChineseLanguage.trim().toCharArray();

        String hanyupinyin = "";
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 输出拼音全部大写
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        try {
            String str = String.valueOf(cl_chars[0]);
            if (str.matches("^[\u2E80-\u9FFF]+$")) {// 如果字符是中文,则将中文转为汉语拼音,并取第一个字母    ^[\u2E80-\u9FFF]+$
                String[] chars = PinyinHelper.toHanyuPinyinStringArray(cl_chars[0], defaultFormat) ;
                if(chars==null){
                    hanyupinyin="#";
                }else{
                    hanyupinyin=chars[0].substring(0, 1);
                }
            } else if (str.matches("[0-9]+")) {// 如果字符是数字,取数字
                hanyupinyin+="*";
            } else if (str.matches("[a-zA-Z]+")) {// 如果字符是字母,取字母 [a-zA-Z]+

                hanyupinyin += (cl_chars[0]);
                hanyupinyin = hanyupinyin.toUpperCase();

            } else {// 否则不转换

                hanyupinyin +="#";
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            System.out.println("字符不能转成汉语拼音");
        }
        return hanyupinyin;
    }


}
