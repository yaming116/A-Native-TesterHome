package com.testerhome.nativeandroid.views;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;

import butterknife.Bind;

/**
 * Created by vclub on 15/10/22.
 */
public class AboutActivity extends BackBaseActivity {

    @Bind(R.id.tv_app_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setCustomTitle("关于");

        tvVersion.setText(getResources().getString(R.string.app_name)
                .concat("\nV")
                .concat(getAppInfo()));
    }

    private String getAppInfo() {

        PackageInfo info = getPackageInfo(this);

        return String.format("%s build %s", info.versionName, info.versionCode);
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            // 当前应用的版本名
            String versionName = info.versionName;
            // 当前版本的版本号
            int versionCode = info.versionCode;
            // 当前版本的包
            String packageNames = info.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
}
