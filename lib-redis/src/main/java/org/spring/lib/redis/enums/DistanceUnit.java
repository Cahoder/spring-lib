package org.spring.lib.redis.enums;

/**
 * 距离单位枚举类
 *
 * @author chd (caihongder@gmail.com)
 * @version 1.0
 * @since 2022/9/14
 **/
public enum DistanceUnit {

    /**
     * 米
     */
    METERS("m","米"),
    /**
     * 千米
     */
    KILOMETERS("km","千米"),
    /**
     * 迈
     */
    MILES("mi","迈"),
    /**
     * 尺
     */
    FEET("ft","尺");

    private final String code;
    private final String name;

    DistanceUnit(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
