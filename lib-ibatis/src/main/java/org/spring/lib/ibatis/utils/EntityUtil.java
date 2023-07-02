package org.spring.lib.ibatis.utils;

import org.springframework.util.StringUtils;

/**
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/29
 **/
public class EntityUtil {

    private EntityUtil() {}

    /**
     * 驼峰转下划线
     */
    public static String camel2UnderLine(String camelStr) {
        if (StringUtils.isEmpty(camelStr)) {
            return camelStr;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelStr.length(); i++) {
            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c) && i != 0) {
                result.append('_');
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    /**
     * 获取mapper接口全限定类名
     */
    public static String getMapperNameByMappedStatementId(String mappedStatementId) {
        if (StringUtils.isEmpty(mappedStatementId)) {
            return "";
        }
        return mappedStatementId.substring(0, mappedStatementId.lastIndexOf("."));
    }

}
