package cn.igancao.flinkcdcdemo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

@EqualsAndHashCode(callSuper = true)
@Data
@IndexName("e_sys_district")
public class ESysDistrict extends Base {

    /**
     * 编号
     */
    @IndexId(type = IdType.CUSTOMIZE)
    @IndexField(fieldType = FieldType.LONG)
    private Long id;

    /**
     * 名称
     */
    @IndexField(fieldType = FieldType.TEXT)
    private String name;

    /**
     * 简称
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String jc;

    /**
     * 父节点id
     */
    @IndexField(fieldType = FieldType.LONG)
    private Long parentid;

    /**
     * 第一个字简拼,如北京是b
     */
    @IndexField(fieldType = FieldType.KEYWORD, ignoreCase = true)
    private String initial;

    /**
     * 汉字简拼，如北京是bj
     */
    @IndexField(fieldType = FieldType.KEYWORD, ignoreCase = true)
    private String initials;

    /**
     * 汉字全拼,如北京是beijing
     */
    @IndexField(fieldType = FieldType.KEYWORD, ignoreCase = true)
    private String pinyin;

    @IndexField(fieldType = FieldType.TEXT)
    private String extra;

    /**
     * 行政区后缀(省、市、自治区、镇等)
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String suffix;

    /**
     * 区域编码,街道没有独有的code，均继承父类（区县）的code
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String code;

    /**
     * 城市编码
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String areaCode;

    /**
     * 顺序
     */
    @IndexField(fieldType = FieldType.INTEGER)
    private Integer order;

    /**
     * 行政区划级别.
     * country:国家
     * province:省份（直辖市会在province和city显示）
     * city:市（直辖市会在province和city显示）
     * district:区县。street:街道
     * oversea:海外
     */
    @IndexField(fieldType = FieldType.KEYWORD)
    private String areaLevel;

    /**
     * 同步时间
     */
    @IndexField(fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private String sysTime;

    /**
     * 经度: 高德
     */
    @IndexField(fieldType = FieldType.DOUBLE)
    private Double lng;

    /**
     * 纬度:高德(海南台湾香港澳门及部分边远地区不准确)
     */
    @IndexField(fieldType = FieldType.DOUBLE)
    private Double lat;
}
