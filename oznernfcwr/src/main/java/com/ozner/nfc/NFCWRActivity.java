package com.ozner.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NFCWRActivity extends AppCompatActivity {
    private static final String TAG = "NFCWRActivity";
    private final int OperaRead = 0;
    private final int OperaClear = 1;
    private final int OperaPassword = 2;
    private final int OperaDeviceNum = 3;
    private final int OperaTest = 4;
    private final int OperaAreaCode = 5;
    private final int OperaChangeArea = 9;
    private final int OperaDeviceType = 14;
    private final int OperaBoth = 16;
    private NfcAdapter nfcAdapter;
    private String[][] techListsArray;
    private IntentFilter[] intentFiltersArray;
    private PendingIntent pendingIntent;

    private TextView tvCardType;
    private TextView tvCardNumber;
    private TextView tvDeviceType;
    private TextView tvAreaId;
    private TextView tvResult;
    private TextView tvCardFace;
    private TextView tvCardValue;
    private EditText etCardFace;
    private EditText etCardValue;
    private EditText etDeviceType;
    private EditText etAreaCode;
    private EditText etDeviceNum;
    private UIZMutilRadioGroup rgCheckOper;

    private int opera = OperaRead;
    MifareClassicOpera mfcOpera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcwr);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvCardFace = (TextView) findViewById(R.id.tv_cardFace);
        tvCardValue = (TextView) findViewById(R.id.tv_cardValue);
        rgCheckOper = (UIZMutilRadioGroup) findViewById(R.id.rg_chekOper);
        etCardFace = (EditText) findViewById(R.id.et_cardFace);
        etCardValue = (EditText) findViewById(R.id.et_cardValue);
        etAreaCode = (EditText) findViewById(R.id.et_areaCode);
        etDeviceType = (EditText) findViewById(R.id.et_deviceType);
        tvCardNumber = (TextView) findViewById(R.id.tv_cardNumber);
        tvCardType = (TextView) findViewById(R.id.tv_cardType);
        tvAreaId = (TextView) findViewById(R.id.tv_areaId);
        etDeviceNum = (EditText) findViewById(R.id.et_deviceNum);
        tvDeviceType = (TextView) findViewById(R.id.tv_deviceType);

        //NFC相关
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        techListsArray = new String[][]{new String[]{MifareClassic.class.getName(), IsoDep.class.getName()}};
        IntentFilter ndef = new IntentFilter();
        ndef.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        ndef.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{ndef};
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "设备不支持NFC", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (!nfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中启用NFC功能", Toast.LENGTH_SHORT).show();
            return;
        }

        hideInputLayout();

        rgCheckOper.setOnCheckedChangeListener(new UIZMutilRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(UIZMutilRadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rb_read) {
                    hideInputLayout();
                    opera = OperaRead;
                } else if (checkedId == R.id.rb_both) {
                    hideInputLayout();
                    etCardFace.setVisibility(View.VISIBLE);
                    etCardValue.setVisibility(View.VISIBLE);
                    etAreaCode.setVisibility(View.VISIBLE);
                    etDeviceType.setVisibility(View.VISIBLE);
                    opera = OperaBoth;
                } else if (checkedId == R.id.rb_writeClear) {
                    hideInputLayout();
                    opera = OperaClear;
                } else if (checkedId == R.id.rb_writeTest) {
                    hideInputLayout();
                    opera = OperaTest;
                } else if (checkedId == R.id.rb_writePassword) {
                    hideInputLayout();
                    opera = OperaPassword;
                } else if (checkedId == R.id.rb_areaCode) {
                    hideInputLayout();
                    etAreaCode.setVisibility(View.VISIBLE);
                    opera = OperaAreaCode;
                    etAreaCode.setText("9990");
                } else if (checkedId == R.id.rb_changeAreaCode) {
                    hideInputLayout();
                    etAreaCode.setVisibility(View.VISIBLE);
                    opera = OperaChangeArea;
                    etAreaCode.setText("9990");
                } else if (checkedId == R.id.rb_deviceNum) {
                    hideInputLayout();
                    etDeviceNum.setVisibility(View.VISIBLE);
                    opera = OperaDeviceNum;
                    etDeviceNum.setText("80037006");
                } else if (checkedId == R.id.rb_deviceType) {
                    hideInputLayout();
                    etAreaCode.setVisibility(View.VISIBLE);
                    etDeviceType.setVisibility(View.VISIBLE);
                    opera = OperaDeviceType;
                    etDeviceType.setText("32");
                    etAreaCode.setText("9990");
                } else {
                    opera = OperaRead;
                }
                Log.e(TAG, "onCheckedChanged: " + opera);
            }
        });
    }

    private void hideInputLayout() {
        etAreaCode.setVisibility(View.GONE);
        etCardFace.setVisibility(View.GONE);
        etCardValue.setVisibility(View.GONE);
        etDeviceType.setVisibility(View.GONE);
        etDeviceNum.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        if (getIntent().getAction() != null) {
            if (NfcAdapter.ACTION_TECH_DISCOVERED.endsWith(getIntent().getAction())) {
                processIntent(getIntent());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
//        Log.e(TAG, "processIntent: Action:" + intent.getAction());
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        tvCardTip.setText("卡片包月数据(CardID:" + Convert.ByteArrayToHexString(tag.getId()) + ")");
        String[] techList = tag.getTechList();
        if (techList != null && techList.length > 0) {
            for (String tech : techList) {
                Log.e(TAG, "processIntent: " + tech);
            }
        } else {
            Log.e(TAG, "processIntent: 获取techlist失败");
        }
        mifareRWTest(intent);
    }

    private void nfcARWTest(Intent intent) {
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NfcAOpera nfcAOpera = NfcAOpera.get(tag);
            nfcAOpera.connect();

            if (nfcAOpera.authenWithKeyA(1, MifareClassic.KEY_NFC_FORUM) || nfcAOpera.authenWithKeyB(1, MifareClassic.KEY_DEFAULT)) {
                List<byte[]> readData = new ArrayList<>();

                readData.add(nfcAOpera.readBlock(4));
                readData.add(nfcAOpera.readBlock(5));
                readData.add(nfcAOpera.readBlock(6));

//                byte[] block5 = nfcAOpera.readBlock(5);
//                byte[] block6 = nfcAOpera.readBlock(6);


                tvResult.append("\n\n");
                for (int i = 0; i < readData.size(); i++) {
                    Log.e(TAG, "nfcARWTest: block:" + (i + 4) + ":" + Convert.ByteArrayToHexString(readData.get(i)));
                    Log.e(TAG, "nfcARWTest: block:" + (i + 4) + ":" + new String(readData.get(i), "UTF-8"));

                    tvResult.append("nfcARW_block_" + (i + 4) + ":" + new String(readData.get(i), "UTF-8") + "\n");
                }
            }


            if (nfcAOpera.authenWithKeyA(1, MifareClassic.KEY_NFC_FORUM) || nfcAOpera.authenWithKeyB(1, MifareClassic.KEY_DEFAULT)) {
                try {
                    byte[] res = nfcAOpera.personalOption();
                    Log.e(TAG, "nfcARWTest_personal: " + Convert.ByteArrayToHexString(res));
                } catch (Exception ex) {
                    Log.e(TAG, "nfcARWTest_personalOption_Ex: " + ex.getMessage());
                }

                byte[] data = "1234a".getBytes();
                byte[] write = new byte[16];
                for (int j = 0; j < MifareClassic.BLOCK_SIZE; j++) {
                    if (j < MifareClassic.BLOCK_SIZE - data.length)
                        write[j] = 0;
                    else {
                        write[j] = data[j + data.length - MifareClassic.BLOCK_SIZE];
                    }
                }
                Log.e(TAG, "nfcARWTest: write_data:" + Convert.ByteArrayToHexString(write));
                nfcAOpera.writeBlock(6, write);
            }
            nfcAOpera.close();
        } catch (Exception ex) {
            Log.e(TAG, "nfcARWTest_Ex: " + ex.getMessage());
        }
//        byte[] SELECT = {
//                (byte) 0x3F,
//                (byte) 0x00,
//        };
//        try{
//            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            NfcA nfcA = NfcA.get(tag);
//            if(nfcA!=null){
//                nfcA.connect();
//                Log.e(TAG, "nfcARWTest:timeOut: "+nfcA.getTimeout());
//                if(nfcA.isConnected()){
//
//                }
////                Log.e(TAG, "nfcARWTest: selectResult:"+Convert.ByteArrayToHexString(nfcA.transceive(SELECT)));
//            }else {
//                Log.e(TAG, "nfcARWTest: not found NfcA Object");
//            }
//        }catch (Exception ex){
//            Log.e(TAG, "nfcARWTest_Ex: "+ex.getMessage());
//        }
    }

    /**
     * mifareClassic 读写测试
     *
     * @param intent
     */
    private void mifareRWTest(final Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mfcOpera = new MifareClassicOpera(tag, true);
        mfcOpera.connect();
        //读写模式
        if (OperaBoth == opera) {
            try {

                Log.e(TAG, "mifareRWTest: 开始写入");

                String cardFace = etCardFace.getText().toString().trim();
                String cardValue = etCardValue.getText().toString().trim();
                String areaCode = etAreaCode.getText().toString().trim();
                String deviceTye = etDeviceType.getText().toString().trim();
                if (areaCode.length() == 0) {
                    Toast.makeText(NFCWRActivity.this, "区域代码未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (deviceTye.length() == 0) {
                    Toast.makeText(NFCWRActivity.this, "机型未填写", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardFace.length() > 0 && cardFace.length() <= 8) {
                    if (cardValue.length() > 0 && cardValue.length() <= 8) {
                        mfcOpera.writeMonthCard(areaCode, deviceTye, cardFace, cardValue, new MifareClassicOpera.INFCWriteListener() {
                            @Override
                            public void onResult(int state, String errmsg) {

                                if (state == OperationState.Result_Ok) {
                                    Toast.makeText(NFCWRActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                    tvResult.setText("写入成功");
                                } else {
                                    Log.e(TAG, "onResult: state:" + state + " ,errmsg:" + errmsg);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(NFCWRActivity.this, "包月值必须是0—8位整数", Toast.LENGTH_SHORT).show();
                        etCardValue.requestFocus();
                    }
                } else {
                    Toast.makeText(NFCWRActivity.this, "包月卡面值必须是0—8位整数", Toast.LENGTH_SHORT).show();
                    etCardFace.requestFocus();
                }
            } catch (Exception ex) {
                Log.e(TAG, "数据转换异常: " + ex.getMessage());
                tvResult.setText("写入失败：" + ex.getMessage());
            }
        } else if (opera == OperaClear) {
            Log.e(TAG, "mifareRWTest: 写清除卡");
            mfcOpera.writeClearCard(new MifareClassicOpera.INFCWriteListener() {
                @Override
                public void onResult(int state, String errmsg) {
                    Log.e(TAG, "写清除卡onResult: " + state + "->" + errmsg);
                    Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (opera == OperaTest) {
            Log.e(TAG, "mifareRWTest: 写测试卡");
            mfcOpera.writeTestCard(new MifareClassicOpera.INFCWriteListener() {
                @Override
                public void onResult(int state, String errmsg) {
                    Log.e(TAG, "写测试卡onResult: " + state + "->" + errmsg);
                    Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (opera == OperaPassword) {
            mfcOpera.writePasswordCard(new MifareClassicOpera.INFCWriteListener() {
                @Override
                public void onResult(int state, String errmsg) {
                    Log.e(TAG, "写密码卡onResult: " + state + "->" + errmsg);
                    Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                }
            });
        } else if (opera == OperaAreaCode) {
            if (etAreaCode.length() > 0) {
                mfcOpera.writeAreaCodeCard(etAreaCode.getText().toString().trim(), new MifareClassicOpera.INFCWriteListener() {
                    @Override
                    public void onResult(int state, String errmsg) {
                        Log.e(TAG, "写区域代码卡onResult: " + state + "->" + errmsg);
                        Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "写区域代码卡: ");
                Toast.makeText(this, "输入区域代码", Toast.LENGTH_SHORT).show();
            }
        } else if (opera == OperaChangeArea) {
            if (etAreaCode.length() > 0) {

                mfcOpera.writeChangeAreaCodeCard(etAreaCode.getText().toString(), new MifareClassicOpera.INFCWriteListener() {
                    @Override
                    public void onResult(int state, String errmsg) {
                        Log.e(TAG, "写区域代码修改卡onResult: " + state + "->" + errmsg);
                        Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "写区域代码修改卡: ");
                Toast.makeText(this, "输入新的区域代码", Toast.LENGTH_SHORT).show();
            }
        } else if (opera == OperaDeviceNum) {
            if (etDeviceNum.length() > 0) {
                mfcOpera.writeDeviceNumCard(etDeviceNum.getText().toString().trim(), new MifareClassicOpera.INFCWriteListener() {
                    @Override
                    public void onResult(int state, String errmsg) {
                        Log.e(TAG, "写机号卡onResult: " + state + "->" + errmsg);
                        Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e(TAG, "写机号卡: ");
                Toast.makeText(this, "输入机号", Toast.LENGTH_SHORT).show();
            }
        }else if(opera == OperaDeviceType){
            Log.e(TAG, "写机型卡");
            if(etDeviceType.length()==0){
                Log.e(TAG, "机型为空 ");
                Toast.makeText(this, "机型为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if(etAreaCode.length() == 0){
                Log.e(TAG, "区域代码为空");
                Toast.makeText(this, "区域代码为空", Toast.LENGTH_SHORT).show();
                return;
            }
            mfcOpera.writeDeviceTypeCard(etAreaCode.getText().toString().trim(), etDeviceType.getText().toString().trim(), new MifareClassicOpera.INFCWriteListener() {
                @Override
                public void onResult(int state, String errmsg) {
                    Log.e(TAG, "写机型卡onResult: " + state + "->" + errmsg);
                    Toast.makeText(NFCWRActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                }
            });

        }

        mfcOpera.readMonthCard(new MifareClassicOpera.IMonthCardListener() {
            @Override
            public void onResult(MifareClassicOpera.MonthCard monthCard) {
                Log.e(TAG, "读卡onResult:" + monthCard.toString());
                tvResult.setText(monthCard.toString());
                tvCardType.setText(String.valueOf(Integer.parseInt(monthCard.cardType)));
                tvCardNumber.setText(String.valueOf(Long.parseLong(monthCard.cardNumber)));
                try {
                    tvCardFace.setText(String.valueOf(Integer.parseInt(monthCard.cardFaceValue)));
                    tvCardValue.setText(String.valueOf(Integer.parseInt(monthCard.cardValue)));
                    tvDeviceType.setText(String.valueOf(Integer.parseInt(monthCard.deviceType)));
                    tvAreaId.setText(String.valueOf(Integer.parseInt(monthCard.areaId)));
                } catch (Exception ex) {
                    Log.e(TAG, "readMonthCard_Ex: " + ex.getMessage());
                }
            }
        });


//        mfcOpera.readMifareBlock(new int[]{1, 2, 4, 5, 6, 8}, new INFCReadListener() {
//            @Override
//            public void onResult(int state, String errmsg, MifareClassicOpera.MifareCard card) {
//                try {
//                    if (state == OperationState.Result_Ok) {
//
//                        tvResult.setText("cardId:" + card.cardId + "\n");
//                        for (int i = 0; i < card.blockDatas.size(); i++) {
//                            tvResult.append("block_" + card.blockDatas.get(i).blockIndex + ":" + Convert.ByteArrayToHexString(card.blockDatas.get(i).data));
//                            tvResult.append("\n");
//                            tvResult.append("Trans_" + card.blockDatas.get(i).blockIndex + ":" + new String(card.blockDatas.get(i).data));
//                            tvResult.append("\n");
//                        }
//                    } else {
//                        Log.e(TAG, "readMifareBlock_onResult: " + state + "->" + errmsg);
//                    }
//                } catch (Exception ex) {
//                    Log.e(TAG, "readMifareBlock_Ex: " + ex.getMessage());
//                }
//            }
//        });

        mfcOpera.close();
    }

}
