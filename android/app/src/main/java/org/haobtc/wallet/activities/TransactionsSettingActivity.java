package org.haobtc.wallet.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.MyDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransactionsSettingActivity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tet_addNode)
    TextView tetAddNode;
    @BindView(R.id.switch_rbf)
    Switch switchRbf;
    @BindView(R.id.switch_noConfirm)
    Switch switchNoConfirm;
    @BindView(R.id.switch_find)
    Switch switchFind;
    private SharedPreferences.Editor edit;
    private SharedPreferences preferences;
    private MyDialog myDialog;

    public int getLayoutId() {
        return R.layout.transaction_setting;
    }

    public void initView() {

        ButterKnife.bind(this);
        myDialog = MyDialog.showDialog(TransactionsSettingActivity.this);
        preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        init();
    }

    private void init() {
        boolean set_use_change = preferences.getBoolean("set_use_change", false);
        boolean set_unconf = preferences.getBoolean("set_unconf", false);
        boolean set_rbf = preferences.getBoolean("set_rbf", false);
        if (set_rbf) {
            switchRbf.setChecked(true);
        } else {
            switchRbf.setChecked(false);
        }
        if (set_unconf) {
            switchNoConfirm.setChecked(true);
        } else {
            switchNoConfirm.setChecked(false);
        }
        if (set_use_change) {
            switchFind.setChecked(true);
        } else {
            switchFind.setChecked(false);
        }

    }

    @Override
    public void initData() {
        switchChoose();

    }

    private void switchChoose() {
        switchRbf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        Daemon.commands.callAttr("set_rbf", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_rbf", true);
                    edit.apply();
                    mToast(getString(R.string.set_success));
                } else {
                    try {
                        Daemon.commands.callAttr("set_rbf", false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_rbf", false);
                    edit.apply();
                }
            }
        });
        //pay unConfirmed income
        switchNoConfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        Daemon.commands.callAttr("set_unconf", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_unconf", true);
                    edit.apply();
                    mToast(getString(R.string.set_success));
                } else {
                    try {
                        Daemon.commands.callAttr("set_unconf", false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_unconf", false);
                    edit.apply();
                }
            }
        });
        //use Give change adrress
        switchFind.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        Daemon.commands.callAttr("set_use_change", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_use_change", true);
                    edit.apply();
                    mToast(getString(R.string.set_success));
                } else {
                    try {
                        Daemon.commands.callAttr("set_use_change", false);
                    } catch (Exception e) {
                        Log.e("Exception", "Exception++: " + e.getMessage());
                        e.printStackTrace();
                    }
                    edit.putBoolean("set_use_change", false);
                    edit.apply();
                }
            }
        });
    }

    @OnClick({R.id.img_back, R.id.tet_addNode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tet_addNode:
                //Restore default settings
                restoreSetDialog();
                break;
        }
    }

    //Restore default settings
    private void restoreSetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionsSettingActivity.this);
        builder.setMessage(R.string.ifconfirm_recovery);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                builder.create().dismiss();
            }
        });
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myDialog.show();
                handler.sendEmptyMessage(1);

            }
        });
        builder.create().show();
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    restoreSet();
                    break;
            }
        }
    };

    private void restoreSet() {
        try {
            Daemon.commands.callAttr("set_rbf", false);
            Daemon.commands.callAttr("set_unconf", true);
            Daemon.commands.callAttr("set_use_change", false);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        edit.putBoolean("set_use_change", false);
        edit.putBoolean("set_unconf", true);
        edit.apply();
        switchRbf.setChecked(false);
        switchNoConfirm.setChecked(true);
        switchFind.setChecked(false);
        myDialog.dismiss();
        mToast(getString(R.string.recovery_succse));

    }

}
