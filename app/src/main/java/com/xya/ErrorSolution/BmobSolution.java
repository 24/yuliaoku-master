package com.xya.ErrorSolution;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.xya.MainActivity.SearchActivity;

/**
 * Created by ubuntu on 15-1-19.
 */


//错误码列表
//9001
//内容：AppKey is Null, Please initialize BmobSDK.
//含义：AppKey为空，请初始化。
//9002
//内容：Parse data error
//含义：解析返回数据出错
//9003
//内容：upload file error
//含义：上传文件出错
//9004
//内容：upload file failure
//含义：文件上传失败
//9005
//内容：A batch operation can not be more than 50
//含义：批量操作只支持最多50条
//9006
//内容：objectId is null
//含义：objectId为空
//9007
//内容：BmobFile File size must be less than 10M.
//含义：文件大小超过10M
//9008
//内容：BmobFile File does not exist.
//含义：上传文件不存在
//9009
//内容：No cache data.
//含义：没有缓存数据
//9010
//内容：The network is not normal.(Time out)
//含义：网络超时
//9011
//内容：BmobUser does not support batch operations.
//含义：BmobUser类不支持批量操作
//9012
//内容：context is null.
//含义：上下文为空
//9013
//内容： BmobObject Object names(database table name) format is not correct.
//含义：BmobObject（数据表名称）格式不正确
//9014
//含义：第三方账号授权失败
//9015
//含义：其他错误均返回此code
//9016
//内容：The network is not available,please check your network!
//含义：无网络连接，请检查您的手机网络。

public class BmobSolution {

    private  Context cxt;
    private  Application application;
    private  int codeId;

    public BmobSolution(Context cxt,int code,Application application){
        this.cxt = cxt;
        this.codeId = code;
        this.application = application;
    }

    public  void ErrorManager(){
        switch (codeId){
            case 9001:
            case 9002:
            case 9003:
            case 9004:
            case 9005:
            case 9006:
            case 9007:
            case 9008:
            case 9009:
            case 9011:
            case 9012:
            case 9013:
            case 9014:
            case 9015:
                SnackbarManager.show(Snackbar
                        .with(cxt)
                        .text("系统内部错误..."));
                break;

            case 9010:
                SnackbarManager.show(Snackbar
                        .with(cxt)
                        .text("网络连接超时..."));
                break;

            case 9016:
                SnackbarManager.show(Snackbar
                        .with(cxt)
                        .text("无网络连接，请检查您的手机网络")
                        .actionLabel("设置")
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                               application.startActivity(intent);
                            }
                        }));
                break;
            case 9022:
                SnackbarManager.show(Snackbar
                        .with(cxt)
                        .text("服务器也没找到..."));
                break;
            default:
                SnackbarManager.show(Snackbar
                        .with(cxt)
                        .text("其他错误!"));
                break;
        }


    }



}
