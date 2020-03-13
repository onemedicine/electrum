package org.haobtc.wallet.activities.onlywallet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;

import org.haobtc.wallet.R;
import org.haobtc.wallet.activities.base.BaseActivity;
import org.haobtc.wallet.utils.Daemon;
import org.haobtc.wallet.utils.MyDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AppWalletSetPassActivity extends BaseActivity {

    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.btn_setPin)
    Button btnSetPin;
    @BindView(R.id.edt_Pass1)
    EditText edtPass1;
    @BindView(R.id.edt_Pass2)
    EditText edtPass2;
    private String strName;
    private SharedPreferences.Editor edit;
    private String strSeed;
    private String strpyObject;
    private MyDialog myDialog;
    private int defaultName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_app_wallet_set_pass;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        SharedPreferences preferences = getSharedPreferences("Preferences", MODE_PRIVATE);
        edit = preferences.edit();
        strSeed = preferences.getString("strSeed", "");
        defaultName = preferences.getInt("defaultName", 0);
        Intent intent = getIntent();
        strName = intent.getStringExtra("strName");
        myDialog = MyDialog.showDialog(AppWalletSetPassActivity.this);

    }

    @Override
    public void initData() {

    }


    @OnClick({R.id.img_back, R.id.btn_setPin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.btn_setPin:
                myDialog.show();
                String strPass1 = edtPass1.getText().toString();
                String strPass2 = edtPass2.getText().toString();
                if (TextUtils.isEmpty(strPass1)) {
                    mToast(getResources().getString(R.string.set_pass));
                    myDialog.dismiss();
                    return;
                }
                if (TextUtils.isEmpty(strPass2)) {
                    mToast(getResources().getString(R.string.set_pass_second));
                    myDialog.dismiss();
                    return;
                }
                if (!strPass1.equals(strPass2)) {
                    mToast(getResources().getString(R.string.two_different_pass));
                    myDialog.dismiss();
                    return;
                }
//                boolean passType = isPassType(strPass1);
//                if (!passType) {
//                    mToast(getResources().getString(R.string.passtype_wrong));
//                    myDialog.dismiss();
//                    return;
//                }
                if (!TextUtils.isEmpty(strSeed)) {
                    try {
                        Daemon.commands.callAttr("create", strName, strPass1, new Kwarg("seed", strSeed));
                        //load wallet
//                        try {
//                            Daemon.commands.callAttr("load_wallet", strName);
//                            Daemon.commands.callAttr("select_wallet", strName);
//                            Log.i("skjhdjdjhhhhhhhhhj", "111111111: ");
//                        } catch (Exception e) {
//                            Log.i("skjhdjdjhhhhhhhhhj", "222222222: ");
//                            e.printStackTrace();
//                            return;
//                        }
                        myDialog.dismiss();
                        Intent intent = new Intent(AppWalletSetPassActivity.this, RemeberMnemonicWordActivity.class);
                        intent.putExtra("strSeed", strSeed);
                        intent.putExtra("strName", strName);
                        intent.putExtra("strPass1", strPass1);
                        startActivity(intent);
                    } catch (Exception e) {
                        myDialog.dismiss();
                        e.printStackTrace();
                        if (e.getMessage().contains("path is exist")) {
                            mToast(getResources().getString(R.string.changewalletname));
                        }
                        return;
                        //local taste noodle trial level soda mobile orchard amazing bean gossip library
                    }
                } else {
                    try {
                        PyObject pyObject = Daemon.commands.callAttr("create", strName, strPass1);
                        strpyObject = pyObject.toString();
                        if (!TextUtils.isEmpty(strpyObject)) {
                            //load wallet
//                            try {
//                                Daemon.commands.callAttr("load_wallet", strName);
//                                Daemon.commands.callAttr("select_wallet", strName);
//                                Log.i("skjhdjdjhhhhhhhhhj", "3333333333: ");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                                myDialog.dismiss();
//                                Log.i("skjhdjdjhhhhhhhhhj", "444444444: ");
//                                return;
//                            }
                            edit.putString("strSeed", strpyObject);
                            edit.apply();
                            myDialog.dismiss();
                            Intent intent = new Intent(AppWalletSetPassActivity.this, RemeberMnemonicWordActivity.class);
                            intent.putExtra("strSeed", strpyObject);
                            intent.putExtra("strName", strName);
                            intent.putExtra("strPass1", strPass1);
                            startActivity(intent);
                        } else {
                            myDialog.dismiss();
                        }
                    } catch (Exception e) {
                        myDialog.dismiss();
                        e.printStackTrace();
                        if (e.getMessage().contains("path is exist")) {
                            mToast(getResources().getString(R.string.changewalletname));
                        }
                        return;
                    }
                }
                int walletNameNum = defaultName + 1;
                edit.putInt("defaultName", walletNameNum);
                edit.apply();

                break;
        }
    }

    //judge mobile is wrong or right
    public boolean isPassType(String mobiles) {
        Pattern p = Pattern
                .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

}