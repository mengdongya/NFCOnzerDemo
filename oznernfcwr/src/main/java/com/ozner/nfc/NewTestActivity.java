package com.ozner.nfc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ozner.nfc.CardBean.OznerCard;
import com.ozner.nfc.CardBean.OznerCardType;


public class NewTestActivity extends AppCompatActivity {
    private static final String TAG = "NewTestActivity";
    private final int OperaRead = 0;
    private final int OperaClear = 1;
    private final int OperaPassword = 2;
    private final int OperaDeviceNum = 3;
    private final int OperaTest = 4;
    private final int OperaAreaCode = 5;
    private final int OperaWhiteCard = 6;
    private final int OperaChangeArea = 9;
    private final int OperaPersonal = 13;
    private final int OperaDeviceType = 14;
    private final int OperaBoth = 16;
    private final int OperaMonthDeviceType = 19;

    private TextView tvCardType;
    private TextView tvCardNumber;
    private TextView tvDeviceType;
    private TextView tvAreaId;
    private TextView tvSecAreaId;
    private TextView tvResult;
    private TextView tvCardFace;
    private TextView tvCardValue;
    private EditText etCardFace;
    private EditText etCardValue;
    private EditText etDeviceType;
    private EditText etAreaCode;
    private EditText etSecAreaCode;
    private EditText etDeviceNum;
    private EditText etCardFacePer;
    private EditText etCardValuePer;
    private TextView tvWriteTip;
    private UIZMutilRadioGroup rgCheckOper;

    OznerNfcManager oznerNfcManager;
    private int opera = OperaRead;

    ActionBar actionBar;

    Handler mHandler = new Handler();

    private boolean isCipherCard = false;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcwr);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvCardFace = (TextView) findViewById(R.id.tv_cardFace);
        tvCardValue = (TextView) findViewById(R.id.tv_cardValue);
        rgCheckOper = (UIZMutilRadioGroup) findViewById(R.id.rg_chekOper);
        etCardFace = (EditText) findViewById(R.id.et_cardFace);
        etCardValue = (EditText) findViewById(R.id.et_cardValue);
        etAreaCode = (EditText) findViewById(R.id.et_areaCode);
        etSecAreaCode = (EditText) findViewById(R.id.et_sec_areaCode);
        etDeviceType = (EditText) findViewById(R.id.et_deviceType);
        tvCardNumber = (TextView) findViewById(R.id.tv_cardNumber);
        tvCardType = (TextView) findViewById(R.id.tv_cardType);
        tvAreaId = (TextView) findViewById(R.id.tv_areaId);
        tvSecAreaId = (TextView) findViewById(R.id.tv_secAreaCode);
        etDeviceNum = (EditText) findViewById(R.id.et_deviceNum);
        tvDeviceType = (TextView) findViewById(R.id.tv_deviceType);
        etCardFacePer = (EditText) findViewById(R.id.et_cardFacePer);
        etCardValuePer = (EditText) findViewById(R.id.et_cardValuePer);
        tvWriteTip = (TextView) findViewById(R.id.tv_writeTip);

        oznerNfcManager = new OznerNfcManager(this);
        oznerNfcManager.onCreate();
        oznerNfcManager.setOznerNfcOpera(new MOnzerNfcOpera());


        dialog = new ProgressDialog(this);
        dialog.setMessage("正在读写卡");
        actionBar = getSupportActionBar();

        hideInputLayout();
        rgCheckOper.setOnCheckedChangeListener(new UIZMutilRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(UIZMutilRadioGroup group, @IdRes int checkedId) {
                hideInputLayout();
                if (checkedId == R.id.rb_read) {
                    opera = OperaRead;
                } else if (checkedId == R.id.rb_whiteType) {
                    opera = OperaWhiteCard;
                } else if (checkedId == R.id.rb_both) {
                    tvWriteTip.setVisibility(View.VISIBLE);
                    etCardFace.setVisibility(View.VISIBLE);
                    etCardValue.setVisibility(View.VISIBLE);
                    etAreaCode.setVisibility(View.VISIBLE);
                    etSecAreaCode.setVisibility(View.VISIBLE);
                    etDeviceType.setVisibility(View.VISIBLE);
                    opera = OperaBoth;
                } else if (checkedId == R.id.rb_writeClear) {
                    opera = OperaClear;
                } else if (checkedId == R.id.rb_writeTest) {
                    opera = OperaTest;
                } else if (checkedId == R.id.rb_writePassword) {
                    opera = OperaPassword;
                } else if (checkedId == R.id.rb_areaCode) {
                    tvWriteTip.setVisibility(View.VISIBLE);
                    etAreaCode.setVisibility(View.VISIBLE);
                    opera = OperaAreaCode;
                    etAreaCode.setText("9990");
                } else if (checkedId == R.id.rb_changeAreaCode) {
                    tvWriteTip.setVisibility(View.VISIBLE);
                    etAreaCode.setVisibility(View.VISIBLE);
                    opera = OperaChangeArea;
                    etAreaCode.setText("9990");
                } else if (checkedId == R.id.rb_deviceNum) {
                    etDeviceNum.setVisibility(View.VISIBLE);
                    tvWriteTip.setVisibility(View.VISIBLE);
                    opera = OperaDeviceNum;
                    etDeviceNum.setText("37006");
                } else if (checkedId == R.id.rb_deviceType) {
                    etAreaCode.setVisibility(View.VISIBLE);
                    etDeviceType.setVisibility(View.VISIBLE);
                    tvWriteTip.setVisibility(View.VISIBLE);
                    opera = OperaDeviceType;
                    etDeviceType.setText("32");
                    etAreaCode.setVisibility(View.VISIBLE);
                    etAreaCode.setText("9990");
                } else if (checkedId == R.id.rb_personl) {
                    opera = OperaPersonal;
                    tvWriteTip.setVisibility(View.VISIBLE);
                    etCardFacePer.setVisibility(View.VISIBLE);
                    etCardValuePer.setVisibility(View.VISIBLE);
                    etAreaCode.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.rb_monthType) {
                    opera = OperaMonthDeviceType;
                    etDeviceType.setVisibility(View.VISIBLE);
                    tvWriteTip.setVisibility(View.VISIBLE);
                } else {
                    opera = OperaRead;
                }
                Log.e(TAG, "onCheckedChanged: " + opera);
            }
        });

        if (!oznerNfcManager.hasNfc()) {
            Toast.makeText(this, "不支持NFC功能", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!oznerNfcManager.isNfcEnable()) {
            new AlertDialog.Builder(this, android.app.AlertDialog.THEME_HOLO_LIGHT)
                    .setMessage("打开NFC")
                    .setPositiveButton("好", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            oznerNfcManager.openNfc();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NewTestActivity.this.finish();
                        }
                    }).show();
        }

    }

    private void hideInputLayout() {
        tvWriteTip.setVisibility(View.GONE);
        etAreaCode.setVisibility(View.GONE);
        etCardFace.setVisibility(View.GONE);
        etCardValue.setVisibility(View.GONE);
        etDeviceType.setVisibility(View.GONE);
        etDeviceNum.setVisibility(View.GONE);
        etCardValuePer.setVisibility(View.GONE);
        etCardFacePer.setVisibility(View.GONE);
        etSecAreaCode.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (oznerNfcManager != null) {
            try {
                oznerNfcManager.onReusme();
            } catch (Exception ex) {
                Log.e(TAG, "onResume_Ex: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!OznerNfcManager.isDeviceSupport(intent)) {
            Toast.makeText(this, "设备不支持", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Log.e(TAG, "onNewIntent: 设备支持");
        }

        if (oznerNfcManager != null) {
            try {
                oznerNfcManager.onNewIntent(intent);
                if (oznerNfcManager.getNfcTag() != null)
                    actionBar.setTitle("CardID:" + Convert.ByteArrayToHexString(oznerNfcManager.getNfcTag().getId()));
            } catch (Exception ex) {
                Log.e(TAG, "onNewIntent_Ex: " + ex.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        if (oznerNfcManager != null) {
            oznerNfcManager.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }


    private class MOnzerNfcOpera implements OznerNfcManager.IOnzerNfcOpera {

        @Override
        public void dealWork() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    tvResult.setText("正在读写卡");
                    dialog.show();
                }
            });
            isCipherCard = false;
            if (opera == OperaWhiteCard) {
                oznerNfcManager.clearMonthCard(new OznerNfcManager.INfcWriterListener() {
                    @Override
                    public void onResult(int state, String errmsg) {
                        if (state == OperationState.Result_Ok) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                    tvResult.setText("写为白卡成功");
                                }
                            });

                        } else {
                            Log.e(TAG, "onResult: state:" + state + " ,errmsg:" + errmsg);
                        }
                    }
                });
            } else if (OperaBoth == opera) {//写包月卡模式
                try {
                    String cardFace = etCardFace.getText().toString().trim();
                    String cardValue = etCardValue.getText().toString().trim();
                    String areaCode = etAreaCode.getText().toString().trim();
                    String secAreaCode = etSecAreaCode.getText().toString().trim();
                    String deviceTye = etDeviceType.getText().toString().trim();
                    if (areaCode.length() == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewTestActivity.this, "区域代码未填写", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return;
                    }
                    if (deviceTye.length() == 0) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewTestActivity.this, "机型未填写", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return;
                    }
                    if (cardFace.length() > 0 && cardFace.length() <= 8) {
                        if (cardValue.length() > 0 && cardValue.length() <= 8) {
                            oznerNfcManager.writeMonthCard(cardFace, cardValue, areaCode, deviceTye, secAreaCode, new OznerNfcManager.INfcWriterListener() {

                                @Override
                                public void onResult(int state, String errmsg) {

                                    if (state == OperationState.Result_Ok) {

                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (dialog.isShowing()) {
                                                    dialog.dismiss();
                                                }
                                                Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                                tvResult.setText("写入成功");
                                            }
                                        });

                                    } else {
                                        Log.e(TAG, "onResult: state:" + state + " ,errmsg:" + errmsg);
                                    }
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewTestActivity.this, "包月值必须是0—8位整数", Toast.LENGTH_SHORT).show();
                                    etCardValue.requestFocus();
                                }
                            });

                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewTestActivity.this, "包月卡面值必须是0—8位整数", Toast.LENGTH_SHORT).show();
                                etCardFace.requestFocus();
                            }
                        });

                    }
                } catch (final Exception ex) {
                    Log.e(TAG, "数据转换异常: " + ex.getMessage());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText("写入失败：" + ex.getMessage());
                        }
                    });

                }
            } else if (opera == OperaClear) {
                oznerNfcManager.writeClearCard(new OznerNfcManager.INfcWriterListener() {
                    @Override
                    public void onResult(int state, final String errmsg) {
                        if (state == OperationState.Result_Ok) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                    tvResult.setText("写入成功");
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewTestActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Log.e(TAG, "写清除卡onResult: " + state + "->" + errmsg);
                    }
                });
            } else if (opera == OperaTest) {
                oznerNfcManager.writeTestCard(new OznerNfcManager.INfcWriterListener() {
                    @Override
                    public void onResult(int state, final String errmsg) {
                        if (state == OperationState.Result_Ok) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (dialog.isShowing()) {
                                        dialog.dismiss();
                                    }
                                    Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                    tvResult.setText("写入成功");
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewTestActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        Log.e(TAG, "写测试卡onResult: " + state + "->" + errmsg);
                    }
                });
            } else if (opera == OperaPassword) {
                isCipherCard = true;
                oznerNfcManager.writeCipherCard(new OznerNfcManager.INfcWriterListener() {
                    @Override
                    public void onResult(int state, String errmsg) {
                        Log.e(TAG, "写密码卡onResult: " + state + "->" + errmsg);
                    }
                });
            } else if (opera == OperaAreaCode) {
                if (etAreaCode.length() > 0) {
                    oznerNfcManager.writeAreaCodeCard(etAreaCode.getText().toString().trim(), new OznerNfcManager.INfcWriterListener() {
                        @Override
                        public void onResult(int state, String errmsg) {
                            Log.e(TAG, "写区域代码卡onResult: " + state + "->" + errmsg);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "输入区域代码", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } else if (opera == OperaChangeArea) {
                if (etAreaCode.length() > 0) {

                    oznerNfcManager.writeChangeAreaCodeCard(etAreaCode.getText().toString(), new OznerNfcManager.INfcWriterListener() {
                        @Override
                        public void onResult(int state, String errmsg) {
                            Log.e(TAG, "写区域代码修改卡onResult: " + state + "->" + errmsg);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "输入新的区域代码", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (opera == OperaDeviceNum) {
                if (etDeviceNum.length() > 0) {
                    oznerNfcManager.writeDeviceNumCard(etDeviceNum.getText().toString().trim(), new OznerNfcManager.INfcWriterListener() {
                        @Override
                        public void onResult(int state, String errmsg) {
                            Log.e(TAG, "写机号卡onResult: " + state + "->" + errmsg);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "输入机号", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (opera == OperaPersonal) {
                if (etAreaCode.length() > 0) {
                    if (etCardFacePer.length() > 0) {

                        if (etCardValuePer.length() > 0) {
                            oznerNfcManager.writePersonalCard(etCardFacePer.getText().toString().trim()
                                    , etCardValuePer.getText().toString().trim()
                                    , etAreaCode.getText().toString().trim()
                                    , new OznerNfcManager.INfcWriterListener() {
                                        @Override
                                        public void onResult(int state, String errmsg) {
                                            Log.e(TAG, "写个人卡onResult: " + state + "->" + errmsg);
                                        }
                                    });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(NewTestActivity.this, "充水值为空", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(NewTestActivity.this, "充水面值为空", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "区域代码为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (opera == OperaDeviceType) {
                if (etDeviceType.length() > 0) {
                    oznerNfcManager.writeDeviceTypeCard(etAreaCode.getText().toString().trim(), etDeviceType.getText().toString().trim(), new OznerNfcManager.INfcWriterListener() {
                        @Override
                        public void onResult(int state, final String errmsg) {
                            if (state == OperationState.Result_Ok) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                        tvResult.setText("写入成功");
                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewTestActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Log.e(TAG, "写机型卡onResult: " + state + "->" + errmsg);
//                        Toast.makeText(NewTestActivity.this, state + "->" + errmsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "机型为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (opera == OperaMonthDeviceType) {
                if (etDeviceType.length() > 0) {
                    oznerNfcManager.writeMonthDeviceType(etDeviceType.getText().toString().trim(), new OznerNfcManager.INfcWriterListener() {
                        @Override
                        public void onResult(int state, final String errmsg) {
                            if (state == OperationState.Result_Ok) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        Toast.makeText(NewTestActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                        tvResult.setText("写入成功");
                                    }
                                });
                            } else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(NewTestActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Log.e(TAG, "写包月机型卡onResult: " + state + "->" + errmsg);
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewTestActivity.this, "机型为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

//            oznerNfcManager.readAllBlocksData(new OznerNfcManager.INfcReaderListener() {
//                @Override
//                public void onResult(int i, String s, final OznerCard oznerCard) {
//                    if (i == OperationState.Result_Ok) {
//                        final String cardtype = new String((oznerCard.blockDatas.get(Integer.valueOf(1))).data);
//                        try {
//                            if ("00000000000000-1".equals(cardtype)) {
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (dialog.isShowing()) {
//                                            dialog.cancel();
//                                        }
//                                        tvResult.setText("白卡，无卡面值等信息");
//                                    }
//                                });
////                                Message message = new Message();
////                                message.obj = "白卡，无卡面值等信息";
////                                message.what = ReadCardInfo;
////                                handler.sendMessage(message);
//                            } else {
//
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        String cardinfoText = "卡类型:" + getCardType(Integer.valueOf(cardtype)) +
//                                                "; 卡号:" + oznerCard.cardId +
//                                                "; 卡面值:" + String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(4))).data))) +
//                                                "; 包月值:" + String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(5))).data))) +
//                                                "; 区域代码:" + String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(6))).data))) +
//                                                "; 二级区域代码" + String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(9))).data))) +
//                                                "; 机型:" + String.valueOf("A" + (Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(8))).data)) - 10));
//
//                                        if (dialog.isShowing()) {
//                                            dialog.cancel();
//                                        }
//                                        if (isCipherCard) {
//                                            tvResult.setText("");
//                                        } else {
//                                            tvResult.setText(cardinfoText);
//                                        }
//                                        tvResult.append("\n读写卡完成");
//
//                                        int cardType = Integer.parseInt(cardtype);
//
//                                        tvCardType.setText(String.valueOf(Integer.parseInt(cardtype)) + "(" + getCardType(cardType) + ")");
//                                        tvCardNumber.setText(oznerCard.cardId);
//                                        try {
//                                            if (isCipherCard) {
//                                                tvCardFace.setText("KeyA");
//                                                tvCardValue.setText("KeyB");
//                                            } else {
//                                                tvCardFace.setText(String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(4))).data))));
//                                                tvCardValue.setText(String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(5))).data))));
//                                            }
//                                            tvDeviceType.setText(String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(8))).data))));
//                                            tvAreaId.setText(String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(6))).data))));
//                                            tvSecAreaId.setText(String.valueOf(Integer.valueOf(new String((oznerCard.blockDatas.get(Integer.valueOf(9))).data))));
//                                        } catch (Exception ex) {
//                                            Log.e(TAG, "readMonthCard_Ex: " + ex.getMessage());
//                                        }
//
//                                    }
//                                });
//
////                                Message message = new Message();
////                                message.obj = cardinfoText;
////                                message.what = ReadCardInfo;
////                                handler.sendMessage(message);
//                            }
//
//                        } catch (Exception ex) {
//                            Log.e(TAG, "onResult_Ex: " + ex.getMessage());
//                        }
//
//                    }
//                }
//            });


            /*oznerNfcManager.readMonthCard(new OznerNfcManager.IMonthCardListener() {
                @Override
                public void onResult(int state, String errmsg, final MonthCard monthCard) {
//                    Log.e(TAG, "读卡onResult:\n" + monthCard.toString());
                    Log.e(TAG, "读卡onResult:");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.cancel();
                            }
                            if (isCipherCard) {
                                tvResult.setText("");
                            } else {
                                tvResult.setText(monthCard.toString());
                            }
                            tvResult.append("\n读写卡完成");

                            int cardType = Integer.parseInt(monthCard.cardType);


                            tvCardType.setText(String.valueOf(Integer.parseInt(monthCard.cardType)) + "(" + getCardType(cardType) + ")");
                            tvCardNumber.setText(String.valueOf(Long.parseLong(monthCard.cardNumber)));
                            try {
                                if (isCipherCard) {
                                    tvCardFace.setText("KeyA");
                                    tvCardValue.setText("KeyB");
                                } else {
                                    tvCardFace.setText(String.valueOf(Integer.parseInt(monthCard.cardFaceValue)));
                                    tvCardValue.setText(String.valueOf(Integer.parseInt(monthCard.cardValue)));
                                }
                                tvDeviceType.setText(String.valueOf(Integer.parseInt(monthCard.deviceType)));
                                tvAreaId.setText(String.valueOf(Integer.parseInt(monthCard.areaCode)));
                            } catch (Exception ex) {
                                Log.e(TAG, "readMonthCard_Ex: " + ex.getMessage());
                            }
                        }
                    });


                }
            });*/
        }
    }


    private String getCardType(int cardType) {
        String tempType;
        switch (cardType) {
            case OznerCardType.Type_Clear:
                tempType = "清除卡";
                break;
            case OznerCardType.Type_AreaCode:
                tempType = "区域代码卡";
                break;
            case OznerCardType.Type_ChangeAreaCode:
                tempType = "区域代码修改卡";
                break;
            case OznerCardType.Type_Cipher:
                tempType = "密码卡";
                break;
            case OznerCardType.Type_DeviceNum:
                tempType = "机号卡";
                break;
            case OznerCardType.Type_DeviceType:
                tempType = "专用机型卡";
                break;
            case OznerCardType.Type_DeviceTypeNormal:
                tempType = "通用机型卡";
                break;
            case OznerCardType.Type_MonthCard:
                tempType = "包月卡";
                break;
            case OznerCardType.Type_Test:
                tempType = "测试卡";
                break;
            case OznerCardType.Type_MonthDeviceType:
                tempType = "包月机型卡";
                break;
            case OznerCardType.Type_Personal:
                tempType = "个人卡";
                break;
            default:
                tempType = "未知";
        }
        return tempType;
    }
}
