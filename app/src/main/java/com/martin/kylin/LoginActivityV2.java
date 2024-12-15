package com.martin.kylin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;
import com.martin.core.utils.EmptyUtils;
import com.martin.core.utils.ToastUtils;
import com.martin.kylin.config.Constant;


public class LoginActivityV2 extends AppCompatActivity {
    private ImmersionBar immersionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        immersionBar = ImmersionBar.with(this);
        ImmersionBar.with(this)
                .statusBarColor(Constant.firstApp() ? R.color.app_main_color : R.color.app_main_color2)
                .statusBarDarkFont(false).init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);
        initUI();
    }

    private void initUI() {
        EditText account = findViewById(R.id.account);
        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EmptyUtils.isEmpty(account.getText())){
                    ToastUtils.showToastOnce("请输入账号");
                    return;
                }
                gotoMain();
            }
        });
    }

    private void gotoMain(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null) {
            immersionBar.destroy();
            immersionBar = null;
        }
    }
}
