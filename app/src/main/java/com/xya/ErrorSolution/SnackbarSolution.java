package com.xya.ErrorSolution;

import android.content.Context;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

/**
 * Created by ubuntu on 15-1-22.
 */
public class SnackbarSolution {
    private Context context;
    /*
    * codeid  1:
    * 用户名邮箱空
    * codeid 2:
    * 密码为空
    * codeid 3:
    * 用户名不正确密码不正确
    * codeid 4:
    * 两次密码不一致
    * codeid 5:
    * 邮箱格式不正确
    * codeid 6:
    * 密码长度要大于等于８
    * codeid 7:
    * 用户名邮箱以存在
    * codeid  8:
    * 获取头像超时
    * codeid  9:
    * 请输入笔记
    * codeid 10:
    * 添加到单词本成功
    * */

    public SnackbarSolution(Context context) {
        this.context = context;
    }

    public void SnackbarSolution(int codeid) {

        String warn = "";
        switch (codeid) {
            case 1:
                warn = "用户名/邮箱不能为空!";
                break;
            case 2:
                warn = "密码不能为空";
                break;
            case 3:
                warn = "用户不存在或密码错误";
                break;
            case 4:
                warn = "两次密码不一致";
                break;
            case 5:
                warn = "邮箱格式不正确";
                break;
            case 6:
                warn = "密码长度要大于等于８";
                break;
            case 7:
                warn = "用户名或邮箱已被占用,请重写填写或直接登陆...";
                break;
            case 8:
                warn = "获取头像超时";
                break;
            case 9:
                warn = "请输入笔记";
                break;
            case 10:
                warn = "添加到单词本成功";
                break;
            default:
                warn = "其他错误";
                break;
        }
        SnackbarManager.show(Snackbar.with(context)
                .text(warn));
    }


}
