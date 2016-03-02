package com.testerhome.nativeandroid.fragments;

import android.content.res.AssetManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.testerhome.nativeandroid.R;

import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import us.feras.mdv.MarkdownView;

/**
 * Created by vclub on 15/10/25.
 */
public class MarkdownFragment extends BaseFragment {

    @Bind(R.id.markdown_topic_body)
    MarkdownView mTopicBody;

    @Override
    protected void setupView() {
        mTopicBody.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

                CustomTabsIntent customTabsIntent = builder.build();

                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
//                return super.shouldOverrideUrlLoading(view, url);
                return true;
            }
        });
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_markdown;
    }

    public void showWebContent(String htmlBody) {
        String prompt = "";
        AssetManager assetManager = getActivity().getResources().getAssets();

        try {
            InputStream inputStream;

            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SettingsFragment.KEY_PREF_THEME, false)) {
                Log.e("theme", "is dark theme");
                inputStream = assetManager.open("dark_template.html");
            } else {
                Log.e("theme", "is light theme");
                inputStream = assetManager.open("h5_template.html");
            }

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            prompt = new String(b);
            prompt = prompt.concat(htmlBody.replace("<img src=\"/photo/",
                    "<img src=\"https://testerhome.com/photo/")).concat("</body></html>");
            inputStream.close();
        } catch (IOException e) {
            Log.e("", "Counldn't open updrage-alter.html", e);
        }

        mTopicBody.setBackgroundColor(0);
        mTopicBody.loadDataWithBaseURL(null, prompt, "text/html", "utf-8", null);

    }
}
