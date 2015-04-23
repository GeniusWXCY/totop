package com.totop.manager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.totop.activity.R;
import com.totop.model.Version;

import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ToastUtils;

public class VersionManager {

    private Context mContext;

    private VersionManager(Context context){
        this.mContext = context;
    }

    private static VersionManager mVersionManager = null;

    public static VersionManager getInstance(Context context){
        if(mVersionManager == null){
            mVersionManager = new VersionManager(context);
        }
        return mVersionManager;
    }

    public Version getVersion() {
        Version version = new Version();
        return version;
    }

    public void checkVersion(boolean isAlert) {
        //DownloadManager downloadManager = DownloadManager;
        //DownloadManagerPro downloadManagerPro = new DownloadManagerPro();
        new UpdateAsyncTask(isAlert).execute();

    }

    class UpdateAsyncTask extends AsyncTask<Void,Void,Version>{

        private boolean isAlert;
        public UpdateAsyncTask(boolean isAlert){
            this.isAlert = isAlert;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isAlert) {
                ToastUtils.show(mContext, mContext.getString(R.string.str_check_updating), Toast.LENGTH_LONG);
            }
        }

        @Override
        protected Version doInBackground(Void... params) {
            Version version = getVersion();
            return version;
        }

        @Override
        protected void onPostExecute(Version version) {
            int appVersionCode = PackageUtils.getAppVersionCode(mContext);
            int remoteVersionCode = version.versionCode;
            if (appVersionCode < remoteVersionCode) {

                String packageUrl = version.packageUrl;

                if (appVersionCode <= version.lowestVersionCode) {
                    //强制更新
                } else {
                    //普通更新
                    Dialog dialog = new AlertDialog.Builder(mContext).setTitle(mContext.getString(R.string.str_update_title)).setMessage(mContext.getString(R.string.str_update_found))
                            // 设置内容
                            .setPositiveButton(mContext.getString(R.string.str_update_ok),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton(mContext.getString(R.string.str_update_no),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).create();
                    // 显示对话框
                    dialog.show();

                }
            } else {
                if (isAlert) {
                    ToastUtils.show(mContext, mContext.getString(R.string.str_no_update), Toast.LENGTH_LONG);
                }
            }
            super.onPostExecute(version);
        }
    }

}
