package org.spring.lib.ibatis.utils;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.UnknownTypeHandler;
import org.spring.lib.ibatis.entity.Column;

/**
 * SQL拼装工具类
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2023/6/30
 **/
public class SqlUtil {

    private SqlUtil() { }

    public static final String ITEM = "item";
    public static final String ITEM_POINT = ITEM + ".";

    /**
     * 新增英文逗号
     */
    public static String addComma(String str) {
        return str + ",";
    }

    /**
     * @return 返回形如 #{property, jdbcType=VARCHAR} 字符串
     */
    public static String wrapProperty(Column column) {
        return wrapProperty(column, "");
    }

    /**
     * @return 形如 column=#{property, jdbcType=VARCHAR} 字符串
     */
    public static String eqStr(Column column) {
        return column.getColumnName() + " = " + wrapProperty(column);
    }

    /**
     * @return 返回形如 #{item.property, jdbcType=VARCHAR} 字符串
     */
    public static String itemWrapProperty(Column column) {
        return wrapProperty(column, ITEM_POINT);
    }

    /**
     * @return 形如 column=#{item.property, jdbcType=VARCHAR} 字符串
     */
    public static String itemEqStr(Column column) {
        return column.getColumnName() + " = " + itemWrapProperty(column);
    }

    /**
     * if标签包含
     *
     * @param testStr 测试!=null 的字符串
     * @param inStr   在<if></if>内部的字符串
     * @return <if test="testStr != null"> inStr </if>
     */
    public static String ifNotNull(String testStr, String inStr) {
        return String.format("\n <if test=\"%s != null\"> %s </if>\n", testStr, inStr);
    }

    /**
     * if标签包含（inStr会加上 ","）
     *
     * @param testStr 测试!=null 的字符串
     * @param inStr   在< if>< /if>内部的字符串
     * @return < if test="testStr != null"> inStr </if>
     */
    public static String ifNotNullWithComma(String testStr, String inStr) {
        return ifNotNull(testStr, addComma(inStr));
    }

    /**
     * @return < foreach collection="list" item="item" open="open" separator="separator" close="close">
     */
    public static String foreachListItemStart(String open, String separator, String close) {
        return String.format("\n<foreach collection=\"list\" item=\"" + ITEM + "\" open=\"%s\" separator=\"%s\" close=\"%s\">\n",
                open, separator, close);
    }

    public static String forEachEnd() {
        return "\n</foreach>\n";
    }

    public static String trimStart(String prefix, String suffix, String suffixOverrides) {
        return String.format("\n<trim prefix=\"%s\" suffix=\"%s\" suffixOverrides=\"%s\" >\n", prefix, suffix, suffixOverrides);
    }

    public static String trimEnd() {
        return "\n</trim>\n";
    }

    public static String setStart() {
        return "\n<set>\n";
    }

    public static String setEnd() {
        return "\n</set>\n";
    }

    public static String whereStart() {
        return "\n<where>\n";
    }

    public static String whereEnd() {
        return "\n</where>\n";
    }

    private static String wrapProperty(Column column, String inStrPrefix) {
        String inStr = column.getFieldName();
        if (column.getJdbcType() != null && column.getJdbcType() != JdbcType.UNDEFINED) {
            inStr += ", jdbcType=" + column.getJdbcType().name();
        }
        if (column.getTypeHandler() != null && column.getTypeHandler().getClass() != UnknownTypeHandler.class) {
            inStr += ", typeHandler=" + column.getTypeHandler().getClass().getName();
        }
        return String.format("#{%s}", inStrPrefix + inStr);
    }

    public static String trimN(String sql) {
        String str = sql.replace("\n\n", "\n");
        if (str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

}
