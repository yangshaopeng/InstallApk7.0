package com.ysp.installapk;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.download.DownloadTask;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button downloadBtn;
    public static final String downloadPath = "http://appstore.vivo.com.cn/appinfo/downloadApkFile?id=50858";
    public static final String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qq.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Aria.download(this).register();
        downloadBtn = (Button) findViewById(R.id.download_btn);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Aria.download(MainActivity.this).load(downloadPath).setDownloadPath(apkPath).start();
            }
        });
    }

    @Download.onTaskRunning
    public void onTaskRunning(DownloadTask downloadTask) {
        Log.i("yang", "onTaskRunning: " + downloadTask.getPercent());
    }

    @Download.onTaskComplete
    public void onTaskCompleted(DownloadTask downloadTask) {
        Log.i("yang", "onTaskComplete");
        install(apkPath);
        //install();
    }

    /**
     * 7.0之前安装方式
     * 如果在7.0及以上的版本用这种方式安装apk，
     * 则报android.os.FileUriExposedException: file:///storage/emulated/0/qq.apk exposed beyond app through Intent.getData()异常
     */
    public void install() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 适配7.0之前和7.0
     *
     * @param apkPath
     */
    public void install(String apkPath) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File apkFile = new File(apkPath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.ysp.installapk.fileProvider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

}
