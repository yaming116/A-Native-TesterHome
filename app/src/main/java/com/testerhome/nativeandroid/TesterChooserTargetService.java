package com.testerhome.nativeandroid;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.os.Build;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bin Li on 2015/10/29.
 */
@TargetApi(Build.VERSION_CODES.M)
public class TesterChooserTargetService extends ChooserTargetService {

    @Override
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {

//        ComponentName componentName = new ComponentName(getPackageName(), SendToWXActivity.class.getCanonicalName());
//
//        List<ChooserTarget> targets = new ArrayList<>();
//
//        String title = "微信朋友圈";
//        Icon icon = Icon.createWithResource(this, R.drawable.icon_res_download_moments);
//
//        float score = 1;
//
//        Bundle extras = new Bundle();
//        extras.putString("type", "circle");
//
//
//        targets.add(new ChooserTarget(
//                title,
//                icon,
//                score,
//                componentName,
//                extras));
        return new ArrayList<>();
    }
}
