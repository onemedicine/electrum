package org.haobtc.wallet.activities.settings;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.PyObject;
import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.activities.jointwallet.CommunicationModeSelector;
import org.haobtc.wallet.utils.Global;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class VersionUpgradeActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.btn_toUpgrade)
    Button btnToUpgrade;
    @BindView(R.id.tet_firmware)
    TextView tetFirmware;
    @BindView(R.id.checkBox_firmware)
    CheckBox checkBoxFirmware;
    @BindView(R.id.tet_bluetooth)
    TextView tetBluetooth;
    @BindView(R.id.checkBox_bluetooth)
    CheckBox checkBoxBluetooth;
    public String pin = "";
    public final static String TAG = VersionUpgradeActivity.class.getSimpleName();
    private int checkWitch = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_version_upgrade;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

    }

    @Override
    public void initData() {
        checkBoxClick();
    }

    private void checkBoxClick() {
        checkBoxFirmware.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxBluetooth.setChecked(false);
                    checkWitch = 1;
                } else {
                    checkWitch = 0;
                }
            }
        });
        checkBoxBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxFirmware.setChecked(false);
                    checkWitch = 2;
                } else {
                    checkWitch = 0;
                }
            }
        });
    }

    @OnClick({R.id.img_back, R.id.btn_toUpgrade})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_toUpgrade:
                CommunicationModeSelector dialog;
                switch (checkWitch) {
                    case 0:
                        mToast(getString(R.string.please_choose_firmware));
                        break;
                    case 1:
                        dialog = new CommunicationModeSelector(TAG, null, "hardware");
                        dialog.show(getSupportFragmentManager(), "");
                        break;
                    case 2:
                        dialog = new CommunicationModeSelector(TAG, null, "ble");
                        dialog.show(getSupportFragmentManager(), "");
                        break;
                }
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction(); // get the action of the coming intent
        if (Objects.equals(action, NfcAdapter.ACTION_NDEF_DISCOVERED) // NDEF type
                || Objects.equals(action, NfcAdapter.ACTION_TECH_DISCOVERED)
                || Objects.requireNonNull(action).equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tags = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            PyObject nfc = Global.py.getModule("trezorlib.transport.nfc");
            PyObject nfcHandler = nfc.get("NFCHandle");
            nfcHandler.put("device", tags);
            Intent intent1 = new Intent(this, UpgradeBixinKEYActivity.class);
            //intent1.putExtra("way", "nfc");
            switch (checkWitch) {
                case 1:
                    intent1.putExtra("tag", 1);
                    break;
                case 2:
                    intent1.putExtra("tag", 2);
                    break;
            }
            startActivity(intent1);
            Intent intent2 = new Intent();
            intent2.setAction(UpgradeBixinKEYActivity.EXECUTE_TASK);
            new Handler().postDelayed(() -> sendBroadcast(intent2), 1000);

        }
    }
}