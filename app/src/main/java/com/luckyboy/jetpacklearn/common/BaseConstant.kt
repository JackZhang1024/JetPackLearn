package com.luckyboy.jetpacklearn.common

object BaseConstant {


    // 传递的参数
    const val ARG_NAME = "ARG_NAME"
    // 用户名
    const val USER_NAME ="USER_NAME"
    // 用户密码
    const val USER_PWD = "USER_PWD"

    // 数据库名字
    const val TABLE_RREFS = "jetpack"
    const val IS_FIRST_LAUNCH = "IS_FIRST_LAUNCH"
    const val SP_USER_ID = "SP_USER_ID"
    const val SP_USER_NAME = "SP_USER_NAME"

    // 传递的参数
    const val ARGS_NAME = "ARGS_NAME"
    const val ARGS_EMAIL = "ARGS_EMAIL"

    // 单个页面大小
    const val SINGLE_PAGE_SIZE = 10

    // MeModel
    const val KEY_IMAGE_URI = "KEY_IMAGE_URI"

    // DetailActivity 传输的数据
    const val DETAIL_SHOE_ID = "DETAIL_SHOE_ID"

    // Other Keys
    const val OUTPUT_PATH = "blur_filter_outputs"
    const val TAG_OUTPUT = "OUTPUT"

    // 操作图片的任务名称
    const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

    const val DELAY_TIME_MILLS:Long = 3000

    // 通知相关
    val VERBOSE_NOTIFICATION_CHANNEL_NAME:CharSequence =
        "Verbose WorkManager Notifications"
    const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
    val NOTIFICATION_TITLE:CharSequence =
        "WorkRequest Starting"
    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
    const val NOTIFICATION_ID = 1

}