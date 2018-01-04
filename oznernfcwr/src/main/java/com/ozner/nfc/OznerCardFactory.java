package com.ozner.nfc;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.ozner.nfc.CardBean.AreaCodeCard;
import com.ozner.nfc.CardBean.CipherCard;
import com.ozner.nfc.CardBean.ClearCard;
import com.ozner.nfc.CardBean.DataBlock;
import com.ozner.nfc.CardBean.DeviceNumberCard;
import com.ozner.nfc.CardBean.DeviceTypeCard;
import com.ozner.nfc.CardBean.MonthCard;
import com.ozner.nfc.CardBean.PersonalCard;

/**
 * Created by ozner_67 on 2017/4/18.
 * 邮箱：xinde.zhang@cftcn.com
 * 转换写入数据
 */

public class OznerCardFactory {
    /**
     * 包月卡
     *
     * @param monthCard
     *
     * @return
     */
    public static List<DataBlock> card2List(MonthCard monthCard) {
        List<DataBlock> monthDatas = new ArrayList<>();
        if (monthCard != null) {
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = monthCard.cardType.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block1);

//            DataBlock block2= new DataBlock();
//            block2.blockIndex = 2;
//            block2.data = monthCard.cardNumber.getBytes(Charset.forName("US-ASCII"));
//            monthDatas.add(block2);

            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = monthCard.cardFaceValue.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block4);

            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = monthCard.cardValue.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block5);

            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = monthCard.areaCode.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block6);

            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = monthCard.deviceType.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block8);

            DataBlock block9 = new DataBlock();
            block9.blockIndex = 9;
            block9.data = monthCard.secAreaCode.getBytes(Charset.forName("US-ASCII"));
            monthDatas.add(block9);
        }
        return monthDatas;
    }


    /**
     * 机型卡
     *
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(DeviceTypeCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);

            //区域代码
            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = card.areaCode.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);
            //机型
            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = card.deviceType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);









            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block4);

            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block5);

        }
        return dataBlocks;
    }


    /**
     * 修改区域代码卡
     *
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(AreaCodeCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);
            //区域代码
            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = card.areaCode.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block4);

            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block5);

            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);

            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);

        }
        return dataBlocks;
    }


    /**
     * 测试卡,清除卡
     *
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(ClearCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);



            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block4);

            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block5);

            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);

            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);
        }
        return dataBlocks;
    }

    /**
     * 机号卡
     *
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(DeviceNumberCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);
            //机号
            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = card.deviceNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block4);



            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block5);

            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);

            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);
        }
        return dataBlocks;
    }

    /**
     * 密码卡
     *
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(CipherCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);
            //keyA
            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = card.keyA;
            dataBlocks.add(block4);
            //keyB
            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = card.keyB;
            dataBlocks.add(block5);



            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);

            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);
        }
        return dataBlocks;
    }

    /**
     * 个人卡
     * @param card
     *
     * @return
     */
    public static List<DataBlock> card2List(PersonalCard card) {
        List<DataBlock> dataBlocks = new ArrayList<>();
        if (card != null) {
            //卡类型
            DataBlock block1 = new DataBlock();
            block1.blockIndex = 1;
            block1.data = card.cardType.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block1);
            //卡号
            DataBlock block2 = new DataBlock();
            block2.blockIndex = 2;
            block2.data = card.cardNumber.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block2);
            //充水面值
            DataBlock block4 = new DataBlock();
            block4.blockIndex = 4;
            block4.data = card.cardFace.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block4);
            //充水值（毫升单位）上限100桶
            DataBlock block5 = new DataBlock();
            block5.blockIndex = 5;
            block5.data = card.cardValue.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block5);
            //区域代码
            DataBlock block6 = new DataBlock();
            block6.blockIndex = 6;
            block6.data = card.areaCode.getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block6);



            DataBlock block8 = new DataBlock();
            block8.blockIndex = 8;
            block8.data = "".getBytes(Charset.forName("US-ASCII"));
            dataBlocks.add(block8);
        }
        return dataBlocks;
    }

}
