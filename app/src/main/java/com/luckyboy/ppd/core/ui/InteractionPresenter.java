package com.luckyboy.ppd.core.ui;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.ppd.core.model.Comment;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;

import org.json.JSONObject;

public class InteractionPresenter {

    private static final String TAG = "InteractionPresenter";

    public static final String DATA_FROM_INTERACTION = "data_from_interaction";

    private static final String URL_TOGGLE_FEED_LIKE = "/ugc/toggleFeedLike";

    private static final String URL_TOGGLE_FEED_DISS = "/ugc/dissFeed";

    private static final String URL_SHARE = "/ugc/increaseShareCount";

    private static final String URL_TOGGLE_COMMENT_LIKE = "/ugc/toggleCommentLike";

    // 给一个帖子点赞/取消点赞 它和给帖子点踩一踩是互斥的
    public static void toggleFeedLike(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedLikeInternal(feed);
            }
        })) {

        } else {
            // 用户已经登录 嗲用接口
            toggleFeedLikeInternal(feed);
        }
    }

    private static void toggleFeedLikeInternal(Feed feed) {
        ApiService.get(URL_TOGGLE_FEED_LIKE)
                .addParam("userId", UserManager.get().getUserId())
                .addParam("itemId", feed.itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            Log.e(TAG, "onSuccess: " + response.body);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        ToastManager.showToast(response.message);
                    }
                });
    }


    private static boolean isLogin(LifecycleOwner owner, Observer<User> observer) {
        if (UserManager.get().isLogin()) {
            return true;
        } else {
            LiveData<User> liveData = UserManager.get().login(AppGlobals.getInstance());
            if (owner == null) {
                liveData.observeForever(loginObserver(observer, liveData));
            } else {
                liveData.observe(owner, loginObserver(observer, liveData));
            }
            return false;
        }
    }

    // 给一个帖子点踩一踩/取消踩一踩 它和贴子点赞是互斥的
    public static void toggleFeedDiss(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
              toggleFeedLikeInternal(feed);
            }
        })){

        }else{
            toggleFeedDissInternal(feed);
        }

    }

    private static void toggleFeedDissInternal(Feed feed) {
         ApiService.get(URL_TOGGLE_FEED_DISS)
                 .addParam("userId", UserManager.get().getUserId())
                 .addParam("itemId", feed.itemId)
                 .execute(new JsonCallback<JSONObject>() {
                     @Override
                     public void onSuccess(ApiResponse<JSONObject> response) {
                         if (response.body!=null){

                         }
                         Log.e(TAG, "onSuccess: ");
                     }

                     @Override
                     public void onError(ApiResponse<JSONObject> response) {
                         ToastManager.showToast(response.message);
                     }
                 });
    }

    // 打开分享面板
    public static void openShare(Context context, Feed feed){
        ToastManager.showToast("打开分享面板");
    }

    // 给一个帖子的评论点赞/取消点赞
    public static void toggleCommentLike(LifecycleOwner owner, Comment comment){
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleCommentLikeInternal(comment);
            }
        })){

        }else{
            toggleCommentLikeInternal(comment);
        }
    }

    private static void toggleCommentLikeInternal(Comment comment) {
        ApiService.get(URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId", comment.id)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {

                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {

                    }
                });
    }



    // 第一参数 observer 是观察是否用户登录的状态的
    private static Observer<User> loginObserver(Observer<User> observer, LiveData<User> liveData) {
        return new Observer<User>() {
            @Override
            public void onChanged(User user) {
                liveData.removeObserver(this);
                if (user != null && observer != null) {
                    observer.onChanged(user);
                }
            }
        };
    }


}
