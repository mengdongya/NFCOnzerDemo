package com.ozner.nfc.CardBean;

/**
 * Created by ozner_67 on 2017/4/19.
 * 邮箱：xinde.zhang@cftcn.com
 */

public class OznerCardType {
    //清除卡
    public static final int Type_Clear = 1;
    //密码卡
    public static final int Type_Cipher = 2;
    //机号卡
    public static final int Type_DeviceNum = 3;
    //测试卡
    public static final int Type_Test = 4;
    //区域代码卡
    public static final int Type_AreaCode = 5;
    //区域代码修改卡
    public static final int Type_ChangeAreaCode = 9;
    //个人卡
    public static final int Type_Personal = 13;
    //通用机型卡
    public static final int Type_DeviceTypeNormal=14;
    //专用机型卡
    public static final int Type_DeviceType= 15;
    //包月卡
    public static final int Type_MonthCard = 16;

    //包月机型卡
    public static final int Type_MonthDeviceType = 19;
}
