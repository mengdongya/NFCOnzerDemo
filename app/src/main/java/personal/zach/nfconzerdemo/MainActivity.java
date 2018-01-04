package personal.zach.nfconzerdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.ozner.nfc.NewTestActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_startNfc)
    Button btnStartNfc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_startNfc)
    public void starNfcClick() {
//        startActivity(new Intent(this, NFCWRActivity.class));
        startActivity(new Intent(this, NewTestActivity.class));
    }
}
