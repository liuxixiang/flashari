package com.lxh.flashari.common.base;

import com.hannesdorfmann.mosby3.mvp.MvpActivity;
import com.hannesdorfmann.mosby3.mvp.MvpNullObjectBasePresenter;

public abstract class BaseActivity<V extends BaseMvpView, P extends MvpNullObjectBasePresenter<V>> extends MvpActivity<V,P> {


}
