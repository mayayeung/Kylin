package com.martin.core.callback;

/**
 * Created by DingJinZhu on 2023/9/25.
 * Description:
 */
public interface MyCallback<T> {

    void onPrepare();

    void onSuccess(T result);

    void onError();

}
