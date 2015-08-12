package com.genius.totop.manager;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.genius.totop.R;
import com.genius.totop.model.DataRes;
import com.genius.totop.model.DatasRes;
import com.genius.totop.model.Version;
import com.genius.totop.utils.Constants;
import com.genius.totop.utils.EncyUtils;
import com.genius.totop.utils.NetApiUtils;

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

    public void checkVersion(boolean isAlert) {
        new UpdateAsyncTask(isAlert).execute();

    }

    private void download(Context context,String apkUrl){
        DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setDestinationInExternalPublicDir(Constants.DOWNLOAD_PATH, Constants.DOWNLOAD_FILE_NAME);
        request.setTitle(context.getString(R.string.app_name));
        request.setMimeType("application/vnd.android.package-archive");
        Constants.DOWNLOAD_ID = downloadManager.enqueue(request);
    }

    class UpdateAsyncTask extends AsyncTask<Void,Void,Version> {

        private boolean isAlert;

        public UpdateAsyncTask(boolean isAlert) {
            this.isAlert = isAlert;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isAlert) {
                ToastUtils.show(mContext, mContext.getString(R.string.str_check_updating), Toast.LENGTH_SHORT);
            }
        }

        @Override
        protected Version doInBackground(Void... params) {

            String ents = EncyUtils.ency(System.currentTimeMillis());
            DataRes<Version> dataRes = null;
            try {
                dataRes = NetApiUtils.service.getVersion(ents);
                if(dataRes != null){
                    return dataRes.data;
                }
            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Version version) {

            if(version == null) return;

            int appVersionCode = PackageUtils.getAppVersionCode(mContext);
            int remoteVersionCode = version.vcode;
            final String apkUrl = version.packageUrl;
            if (appVersionCode < remoteVersionCode) {

                AlertDialog alertDialog = null;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle(mContext.getString(R.string.str_update_title)).setMessage(mContext.getString(R.string.str_update_found)).setCancelable(false);
                builder.setPositiveButton(mContext.getString(R.string.str_update_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                download(mContext, apkUrl);
                                dialog.dismiss();
                            }
                        });

                //强制更新
                if (appVersionCode <= version.lowestVersionCode) {
                    alertDialog = builder.create();
                    //TODO 退出应用
                } else {//普通更新
                    alertDialog = builder.setNegativeButton(mContext.getString(R.string.str_update_no),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    dialog.dismiss();
                                }
                            }).create();
                    alertDialog = builder.create();
                }
                // 显示对话框
                alertDialog.show();
            } else {
                if (isAlert) {
                    ToastUtils.show(mContext, mContext.getString(R.string.str_no_update), Toast.LENGTH_LONG);
                }
            }
            super.onPostExecute(version);
        }
    }

}
