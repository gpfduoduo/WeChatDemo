package com.gpfduoduo.wechat.util;

import java.util.Locale;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by gpfduoduo on 2016/6/30.
 */
public class PinyinUtil {

    private static HanyuPinyinOutputFormat spellFormat
            = new HanyuPinyinOutputFormat();


    static {
        spellFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        spellFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }


    public static String ChineseToSpell(String chineseStr)
            throws BadHanyuPinyinOutputFormatCombination {
        StringBuffer result = new StringBuffer();
        for (char c : chineseStr.toCharArray()) {
            if (c > 128) {
                String[] array = PinyinHelper.toHanyuPinyinStringArray(c,
                        spellFormat);
                if (array != null && array.length > 0) {
                    result.append(array[0]);
                }
                else {
                    result.append(" ");
                }
            }
            else {
                result.append(c);
            }
        }
        return result.toString();
    }


    public static String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase(Locale.US);
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }
}
