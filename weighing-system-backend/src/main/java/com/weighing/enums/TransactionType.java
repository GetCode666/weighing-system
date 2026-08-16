package com.weighing.enums;

public enum TransactionType {
    /**
     * 收货过磅 (Inbound Weighing)
     * 指供应商送货入库时的毛重称量，通常后续会结合皮重计算净重
     */
    RECEIVING_WEIGHING("RECEIVING_WEIGHING","收货过磅"),
    /**
     * 发货过磅 (Outbound Weighing)
     * 指客户提货出库时的皮重称量，通常后续会结合毛重计算净重
     */
    SHIPPING_WEIGHING("SHIPPING_WEIGHING","发货过磅"),
    /**皮重去皮 */
    TARE_WEIGHING("TARE_WEIGHING","皮重去皮"),
   /**
    * 净值确认
    * 指客户提货出库时的净重称量，通常后续会结合毛重计算净重 直接记录或确认货物的净重（适用于已知皮重或标准包装场景）  */
   NET_WEIGHING_CONFIRM("NET_WEIGHING_CONFIRM","净值确认"),

    /**
     * 复磅/二次过磅 (Re-weighing)
     * 对已有称重记录的车辆进行再次称量，用于校验或争议处理
     */
    RE_WEIGHING("RE_WEIGHING", "复磅"),

    /**
     * 内部转运 (Internal Transfer)
     * 厂区或仓库内部的物料移动称量
     */
    INTERNAL_TRANSFER("INTERNAL_TRANSFER", "内部转运"),

    /**
     * 退货过磅 (Return Weighing)
     * 客户退货或供应商退料时的称量
     */
    RETURN_WEIGHING("RETURN_WEIGHING", "退货过磅");


    private final String code;
    private final String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据 code 获取枚举实例
     *
     * @param code 类型代码
     * @return 对应的枚举实例，若未找到则返回 null
     */

    public static TransactionType fromCode(String code){
       if (code==null){
           return null;
       }
       for(TransactionType type : TransactionType.values()){
           if (type.getCode().equalsIgnoreCase(code)) {
               return type;
           }
       }
       return null;
    }
}
