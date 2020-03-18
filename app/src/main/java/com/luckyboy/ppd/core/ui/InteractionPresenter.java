package com.luckyboy.ppd.core.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSONObject;
import com.luckyboy.libcommon.extension.LiveDataBus;
import com.luckyboy.libcommon.global.AppGlobals;
import com.luckyboy.libcommon.utils.ToastManager;
import com.luckyboy.libnetwork.ApiResponse;
import com.luckyboy.libnetwork.ApiService;
import com.luckyboy.libnetwork.JsonCallback;
import com.luckyboy.ppd.core.model.Comment;
import com.luckyboy.ppd.core.model.Feed;
import com.luckyboy.ppd.core.model.TagList;
import com.luckyboy.ppd.core.model.User;
import com.luckyboy.ppd.login.UserManager;


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
            // 用户已经登录 不用登录接口
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
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                            feed.getUgc().setHasLiked(hasLiked);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
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
        })) {
        } else {
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
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                            feed.getUgc().setHasdiss(hasLiked);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        ToastManager.showToast(response.message);
                    }
                });
    }

    // 打开分享面板
    public static void openShare(Context context, Feed feed) {
        ToastManager.showToast("打开分享面板");
    }

    // 给一个帖子的评论点赞/取消点赞
    public static void toggleCommentLike(LifecycleOwner owner, Comment comment) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleCommentLikeInternal(comment);
            }
        })) {

        } else {
            toggleCommentLikeInternal(comment);
        }
    }

    private static void toggleCommentLikeInternal(Comment comment) {
        ApiService.get(URL_TOGGLE_COMMENT_LIKE)
                .addParam("commentId", comment.commentId)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean hasLiked = response.body.getBooleanValue("hasLiked");
                            comment.ugc.setHasLiked(hasLiked);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        ToastManager.showToast(response.message);
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

    // 删除某个帖子
    public static LiveData<Boolean> deleteFeed(Context context, long itemId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        new AlertDialog.Builder(context)
                .setNegativeButton("取消", ((dialog, which) -> {
                    dialog.dismiss();
                }))
                .setPositiveButton("确定", (dialog, which) -> {
                    dialog.dismiss();
                    deleteFeedInternal(liveData, itemId);
                })
                .setMessage("确定要删除这条评论吗？")
                .create()
                .show();
        return liveData;
    }

    private static void deleteFeedInternal(MutableLiveData<Boolean> liveData, long itemId) {
        ApiService.get("/feeds/deleteFeed")
                .addParam("itemId", itemId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        super.onSuccess(response);
                        if (response.body!=null){
                            boolean success = response.body.getBooleanValue("result");
                            liveData.postValue(success);
                            ToastManager.showToast("删除成功");
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }


    // 删除某个帖子的一个评论
    public static LiveData<Boolean> deleteFeedComment(Context context, long itemId, long commentId) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        new AlertDialog.Builder(context)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 进行删除评论操作
                        deleteFeedCommentInternal(liveData, itemId, commentId);
                    }
                })
                .setMessage("您确定要删除这条评论吗？")
                .create()
                .show();
        return liveData;
    }


    private static void deleteFeedCommentInternal(
            LiveData liveData, long itemId, long commentId) {
        ApiService.get("/comment/deleteComment")
                .addParam("userId", UserManager.get().getUserId())
                .addParam("commentId", commentId)
                .addParam("itemId", itemId)
                .execute(new JsonCallback<com.alibaba.fastjson.JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        if (response.body != null) {
                            boolean result = response.body.getBooleanValue("result");
                            MutableLiveData data = (MutableLiveData) liveData;
                            data.postValue(result);
                            ToastManager.showToast("评论删除成功");
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }

    // 关注/取消关注一个用户
    public static void toggleFollowUser(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFollowUser(feed);
            }
        })) {
        } else {
            toggleFollowUser(feed);
        }
    }

    private static void toggleFollowUser(Feed feed) {
        ApiService.get("/ugc/toggleUserFollow/")
                .addParam("followUserId", UserManager.get().getUserId())
                .addParam("userId", feed.author.userId)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        super.onSuccess(response);
                        boolean hasFollow = response.body.getBooleanValue("hasLiked");
                        feed.getAuthor().setHasFollow(hasFollow);
                        // TODO: 2020-03-18 LiveDatatBus
                        LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                .postValue(feed);
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }


    // 收藏/取消收藏一个帖子
    public static void toggleFeedFavorite(LifecycleOwner owner, Feed feed) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleFeedFavorite(feed);
            }
        })) {

        } else {
            toggleFeedFavorite(feed);
        }
    }

    private static void toggleFeedFavorite(Feed feed) {
        ApiService.get("/ugc/toggleFavorite")
                .addParam("itemId", feed.itemId)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        super.onSuccess(response);
                        if (response.body != null) {
                            boolean hasFavorite = response.body.getBooleanValue("hasFavorite");
                            feed.ugc.setHasFavorite(hasFavorite);
                            LiveDataBus.get().with(DATA_FROM_INTERACTION)
                                    .postValue(feed);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }


    // 关注/取消一个帖子标签
    public static void toggleTagLike(LifecycleOwner owner, TagList tagList) {
        if (!isLogin(owner, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                toggleTagLikeInternal(tagList);
            }
        })) {
        } else {
            toggleTagLikeInternal(tagList);
        }
    }

    private static void toggleTagLikeInternal(TagList tagList) {
        ApiService.get("/tag/toggleTagFollow")
                .addParam("tagId", tagList.tagId)
                .addParam("userId", UserManager.get().getUserId())
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(ApiResponse<JSONObject> response) {
                        super.onSuccess(response);
                        if (response.body != null) {
                            boolean follow = response.body.getBooleanValue("hasFollow");
                            tagList.setHasFollow(follow);
                        }
                    }

                    @Override
                    public void onError(ApiResponse<JSONObject> response) {
                        super.onError(response);
                        ToastManager.showToast(response.message);
                    }
                });
    }


}
