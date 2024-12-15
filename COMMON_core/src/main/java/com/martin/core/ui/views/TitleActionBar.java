package com.martin.core.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martin.core.R;
import com.martin.core.utils.ClickUtils;
import com.martin.core.utils.DensityUtils;
import com.martin.core.utils.EmptyUtils;

/**
 * Created by lishuxun on 2018/12/29.
 */

public class TitleActionBar extends RelativeLayout {
    Context context;
    private int bgColor, vType = 1;
    private TextView title, subTitle, leftTitle, rightTitle, leftNotice, rightNotice;
    private View leftPoint, rightPoint;
    private String text;
    private LinearLayout backLinear, moreOpt;
    private LinearLayout middleLinear;//布局八，之江汇需要设置取消居中
    private boolean isImmersionBar = false;
    private View msgTitleView;//消息对应的title
    //布局4
    private View ltRight1, ltRight2, ltRight3, ltRight4, redPoint;// 在最右边
    private ImageView ivRight1, ivRight2, ivRight3;
    private TextView tvNum, tvRight4;
    private Button btn;
    int imgRes1, imgRes2;

    //布局5
    private ImageView ivBack, goLast;
    private TextView tvBack, tvTitleRight;
    private String right_text;


    //布局7
    RoundedImageView ivIcon;

    //布局14
    LinearLayout clearBtn;
    EditText editText;

    //布局16
    private LinearLayout titleLl;
    private ImageView titleIcon;
    private LinearLayout scanCodeLL;

    public TitleActionBar(Context context) {
        super(context);
        this.context = context;
        initView(context);
    }

    public TitleActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleActionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleActionBar);
        vType = array.getInt(R.styleable.TitleActionBar_layout_type, 1);
        text = array.getString(R.styleable.TitleActionBar_title);
        bgColor = array.getColor(R.styleable.TitleActionBar_bgColor, getResources().getColor(R.color.app_main_color));
        imgRes1 = array.getResourceId(R.styleable.TitleActionBar_right_img1, 0);
        imgRes2 = array.getResourceId(R.styleable.TitleActionBar_right_img2, 0);
        right_text = array.getString(R.styleable.TitleActionBar_right_text);
        isImmersionBar = array.getBoolean(R.styleable.TitleActionBar_isImmersionBar, false);
        array.recycle();
        initView(context);
    }

    /*沉淀样式标题栏v7包自动支持，自己弄的话会把之前的页面标题头都坏了
     * */
    private void initView(Context context) {
        removeAllViews();

        setBackgroundColor(bgColor);

        View view = null;
        ViewGroup root = isImmersionBar ? null : this;

        switch (vType) {
            case 1:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type1, root);
                title = view.findViewById(R.id.title);
                title.setText(text);
                ivRight1=view.findViewById(R.id.rightBtn);
                moreOpt = view.findViewById(R.id.more_operate);
                ltRight2 = view.findViewById(R.id.rightBtn2);
                ivRight2 = view.findViewById(R.id.right_btn_img2);
                break;
            case 2:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type2, root);
                backLinear = view.findViewById(R.id.back_linear);
                title = view.findViewById(R.id.page_title);
                title.setText(text);
                break;
            case 3:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type3, root);
                backLinear = view.findViewById(R.id.back_linear);
                title = view.findViewById(R.id.page_title);
                title.setText(text);
                moreOpt = view.findViewById(R.id.more_operate);
                break;
            case 5:
                //左侧返回箭头，文字可不显示，中间标题，右侧文字
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type5, root);
                backLinear = view.findViewById(R.id.back_linear);
                tvBack = view.findViewById(R.id.tv_back);
                title = view.findViewById(R.id.page_title);
                tvTitleRight = view.findViewById(R.id.tv_title_right);
                title.setText(text);
                tvTitleRight.setText(right_text);
                break;
            case 10:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type10, root);
                title = view.findViewById(R.id.page_title);
                title.setText(text);
                break;
            case 11:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type11, root);
                backLinear = view.findViewById(R.id.back_linear);
                title = view.findViewById(R.id.title);
                subTitle = view.findViewById(R.id.expand_tv);
                ltRight1 = view.findViewById(R.id.right_btn);
                ltRight2 = view.findViewById(R.id.right_btn2);
                ivRight1 = view.findViewById(R.id.right_btn_img);
                ivRight2 = view.findViewById(R.id.right_btn_img2);

                if (imgRes1 != 0) {
                    ivRight1.setImageResource(imgRes1);
                }
                if (imgRes2 != 0) {
                    ivRight2.setImageResource(imgRes2);
                }

                break;
            case 12:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type12, root);
                title = view.findViewById(R.id.page_title);
                title.setText(text);
                ltRight1 = view.findViewById(R.id.rightBtn);
                ivRight1 = view.findViewById(R.id.right_btn_img);
                break;

            case 13:
                view = LayoutInflater.from(context).inflate(R.layout.title_action_bar_type13, root);
                backLinear = view.findViewById(R.id.back_linear);
                title = view.findViewById(R.id.title_tv);
                subTitle = view.findViewById(R.id.suv_title_tv);
                title.setText(text);
                moreOpt = view.findViewById(R.id.more_operate);
                break;

        }

        if (isImmersionBar) {
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = statusBarHeight();
            addView(view, params);
        }

    }

    public void setTitleInMiddle(boolean isMiddle) {//针对布局16
        if (null != titleLl) {
            LayoutParams lp = (LayoutParams) titleLl.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, isMiddle ? 1 : 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, isMiddle ? 0 : 1);
            titleLl.setLayoutParams(lp);
        }

    }

    public int statusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        } else {
            result = DensityUtils.dp2px(context, 20);
        }
        return result;
    }


    public void setBackGround(int resId) {
        setBackgroundResource(resId);
    }

    public void setViewType(int vType) {
        this.vType = vType;
        initView(context);
    }


    public TitleActionBar setTitle(String text) {
        if (title != null) {
            title.setText(text);
            if (vType == 16) {//需要计算宽度
                int maxWidth =
                        getResources().getDisplayMetrics().widthPixels //屏幕宽
                                - (ltRight1 != null && (ltRight1.getVisibility() == View.VISIBLE || ltRight1.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x45) : 0)
                                - (ltRight2 != null && (ltRight2.getVisibility() == View.VISIBLE || ltRight2.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight3 != null && (ltRight3.getVisibility() == View.VISIBLE || ltRight3.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x23) : 0); //剩余宽度
                if (tvRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE)) {
                    Paint tvPaint = tvRight4.getPaint();
                    maxWidth -= tvPaint.measureText(tvRight4.getText().toString().trim());
                }
                int arrowWidth = 0;
                if (titleIcon.getVisibility() != View.GONE) {
                    arrowWidth = getResources().getDimensionPixelOffset(R.dimen.x7) + getResources().getDimensionPixelOffset(R.dimen.x13);
                }
                int stWidth = 0;
                if (subTitle.getVisibility() != View.GONE) {
                    Paint paint = subTitle.getPaint();
                    String subTitleTxt = subTitle.getText().toString().trim();
                    if (EmptyUtils.isNotEmpty(subTitleTxt)) {
                        stWidth = (int) paint.measureText(subTitleTxt);
                    }
                }
                Paint paint = title.getPaint();

                int currWidth = 0;
                if (null != text) {
                    currWidth = (int) paint.measureText(text);
                }

                int padding = getResources().getDimensionPixelOffset(R.dimen.x15) //左padding
                        + getResources().getDimensionPixelOffset(R.dimen.x15);//右padding

                int width = Math.min(maxWidth, currWidth + stWidth + arrowWidth + padding);
                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, getResources().getDimensionPixelOffset(R.dimen.top_actionbar_height));
                LayoutParams params = (LayoutParams) titleLl.getLayoutParams();
                params.width = width;
                titleLl.setLayoutParams(params);
            }

        }
        return this;
    }

    public void setSubTitle(String text) {
        if (subTitle != null) {
            subTitle.setText(text);
            if (vType == 16) {
                int maxWidth =
                        getResources().getDisplayMetrics().widthPixels //屏幕宽
                                - (ltRight1 != null && (ltRight1.getVisibility() == View.VISIBLE || ltRight1.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x45) : 0)
                                - (ltRight2 != null && (ltRight2.getVisibility() == View.VISIBLE || ltRight2.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight3 != null && (ltRight3.getVisibility() == View.VISIBLE || ltRight3.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x23) : 0); //剩余宽度
                if (tvRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE)) {
                    Paint tvPaint = tvRight4.getPaint();
                    maxWidth -= tvPaint.measureText(tvRight4.getText().toString().trim());
                }

                int arrowWidth = 0;
                if (titleIcon.getVisibility() != View.GONE) {
                    arrowWidth = getResources().getDimensionPixelOffset(R.dimen.x7) + getResources().getDimensionPixelOffset(R.dimen.x13);
                }
                int stWidth = 0;
                if (subTitle.getVisibility() != View.GONE) {
                    Paint paint = subTitle.getPaint();
                    stWidth = (int) paint.measureText(text);
                }
                Paint paint = title.getPaint();

                int currWidth = (int) paint.measureText(title.getText().toString().trim());

                int padding = getResources().getDimensionPixelOffset(R.dimen.x15) //左padding
                        + getResources().getDimensionPixelOffset(R.dimen.x15);//右padding

                int width = Math.min(maxWidth, currWidth + stWidth + arrowWidth + padding);
                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, getResources().getDimensionPixelOffset(R.dimen.top_actionbar_height));
                LayoutParams params = (LayoutParams) titleLl.getLayoutParams();
                params.width = width;
                titleLl.setLayoutParams(params);
            }

        }
    }


    //是否显示更多
    public TitleActionBar isMoreBtnShow(boolean flag) {
        if (moreOpt != null) {
            moreOpt.setVisibility(flag ? VISIBLE : INVISIBLE);
        }
        return this;
    }

    public View getMoreOptBtn() {
        return moreOpt;
    }


    //点击更多触发
    public TitleActionBar onMoreBtnClick(final OnBtnClickListener listener) {
        if (moreOpt != null && listener != null) {
            ClickUtils.clickView(moreOpt, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(moreOpt);
                }
            });
        }
        return this;
    }

    public TitleActionBar onBackBtnClick(final OnBtnClickListener listener) {
        if (backLinear != null && listener != null) {
            ClickUtils.clickView(backLinear, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(backLinear);
                }
            });
        }

        if (tvBack != null && listener != null) {
            ClickUtils.clickView(tvBack, () -> listener.doNext(tvBack));
        }

        return this;
    }

    public void setMiddleLinear(boolean isMiddle) {
        setMiddleView(middleLinear, isMiddle);
    }

    public void setMsgTitleGone() {
        if (null != msgTitleView) {
            msgTitleView.setVisibility(GONE);
        }
    }

    public void setMiddleTitle(boolean isMiddle) {
        setMiddleView(title, isMiddle);
    }

    public void setMiddleView(View view, boolean isMiddle) {
        if (null != view) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, isMiddle ? 1 : 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, isMiddle ? 0 : 1);
            view.setLayoutParams(lp);
        }
    }

    // 是否显示返回
    public TitleActionBar isBackLinearShow(boolean flag) {
        if (backLinear != null) {
            backLinear.setVisibility(flag ? VISIBLE : INVISIBLE);
        }
        return this;
    }

    //以下是布局4的控件
    public TitleActionBar onRight1BtnClick(final OnBtnClickListener listener) {
        if (ltRight1 != null && listener != null) {
            ClickUtils.clickView(ltRight1, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(ltRight1);
                }
            });
        }
        return this;
    }

    public TitleActionBar onRight2BtnClick(final OnBtnClickListener listener) {
        if (ltRight2 != null && listener != null) {
            ClickUtils.clickView(ltRight2, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(ltRight2);
                }
            });
        }
        return this;
    }

    public TitleActionBar onRight3BtnClick(final OnBtnClickListener listener) {
        if (ltRight3 != null && listener != null) {
            ClickUtils.clickView(ltRight3, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(ltRight3);
                }
            });
        }
        return this;
    }

    //布局16使用
    public TitleActionBar onScanCodeBtnClick(final OnBtnClickListener listener) {
        if (scanCodeLL != null && listener != null) {
            ClickUtils.clickView(scanCodeLL, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(scanCodeLL);
                }
            });
        }
        return this;
    }

    public TitleActionBar onTitleClick(final OnBtnClickListener listener) {
        if (title != null && listener != null) {
            ClickUtils.clickView(title, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(title);
                }
            });
        }
        return this;
    }

    public TitleActionBar onTitleExpandClick(final OnBtnClickListener listener) {
        View v = this.findViewById(R.id.title_c_layout);
        if (v != null) {
            ClickUtils.clickView(v, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(title);
                }
            });
        }
        return this;
    }


    //设置小圆点数字
    public void setNoticeNum(int num) {
        if (tvNum != null) {
            if (num > 0) {
                if (num <= 9) {
                    tvNum.setBackgroundResource(R.drawable.reddot);
                }
                if (num > 9) {
                    tvNum.setBackgroundResource(R.drawable.reddot2);
                }
                if (num > 99) {
                    tvNum.setBackgroundResource(R.drawable.reddot99);
                }
                tvNum.setVisibility(View.VISIBLE);
                tvNum.setText(num > 99 ? "99+" : num + "");
            } else {
                tvNum.setVisibility(View.GONE);
            }
        }
    }

    //以下是布局5的控件
    public void setTvBackText(String text) {
        if (tvBack != null) {
            tvBack.setText(text);
        }
    }

    public TextView getTvBack() {
        return tvBack;
    }

    public void setTvBackVisible(int visible) {
        if (tvBack != null) {
            tvBack.setVisibility(visible);
        }
    }

    public void setIvBackVisible(boolean visible) {
        if (ivBack != null) {
            ivBack.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    public void setTvTitleRightText(String text) {
        if (tvTitleRight != null) {
            tvTitleRight.setText(text);
        }
    }

    public void onTvTitleRightBtnClick(final OnBtnClickListener listener) {
        if (tvTitleRight != null && listener != null) {
            ClickUtils.clickView(tvTitleRight, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(tvTitleRight);
                }
            });
        }
    }

    public void setRedPointVisible(int visible) {
        if (redPoint != null) {
            redPoint.setVisibility(visible);
        }
    }

    public interface OnBtnClickListener {
        void doNext(View view);
    }

    //右起第一个按钮
    public TitleActionBar setRightBtnImageResource(int resId) {
        if (ivRight1 != null) {
            ivRight1.setImageResource(resId);
        }
        return this;
    }

    public TitleActionBar setRightBtnVisible(int visible) {
        if (ltRight1 != null) {
            ltRight1.setVisibility(visible);
        }
        return this;
    }

    //布局16
    public void setScanCodeBtnVisible() {
        if (scanCodeLL != null) {
            scanCodeLL.setVisibility(VISIBLE);
        }
    }

    public void setRightBtn3Visible() {
        if (ltRight3 != null) {
            ltRight3.setVisibility(VISIBLE);
        }
    }

    public void setRightBtn4Visible() {
        if (ltRight4 != null) {
            ltRight4.setVisibility(VISIBLE);
        }
    }

    public TitleActionBar setLtRight4Visibility(boolean visible) {
        if (ltRight4 != null) {
            ltRight4.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public void setRightBtn4Click(final OnBtnClickListener listener) {
        if (ltRight4 != null && listener != null) {
            ClickUtils.clickView(ltRight4, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(ltRight4);
                }
            });
        }
    }

    public void setRightBtn4Content(String content) {
        if (tvRight4 != null) {
            tvRight4.setText(content);
        }
    }

    //右起第二个按钮
    public TitleActionBar setRightBtn2ImageResource(int resId) {
        if (ivRight2 != null) {
            ivRight2.setImageResource(resId);
        }
        return this;
    }

    public TitleActionBar setivRight2Visibility(boolean visible) {
        if (ivRight2 != null) {
            ivRight2.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public TitleActionBar setLtRight1Visibility(boolean visible) {
        if (ltRight1 != null) {
            ltRight1.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    //右起第二个按钮
    public void setRightBtn3ImageResource(int resId) {
        if (ivRight3 != null) {
            ivRight3.setImageResource(resId);
        }
    }

    public TitleActionBar setRightBtn2Visible(int visible) {
        if (ltRight2 != null) {
            ltRight2.setVisibility(visible);
        }
        return this;
    }

    //副标题可见
    public TitleActionBar setSubTitleVisible(int visible) {
        if (subTitle != null) {
            subTitle.setVisibility(visible);
        }
        return this;
    }

    //布局7
    public ImageView getRightIcon() {
        if (ivIcon != null) {
            return ivIcon;
        }
        return null;
    }

    //布局8相关
    public TitleActionBar setLeftTitle(String text) {
        if (leftTitle != null) {
            leftTitle.setText(text);
        }
        return this;
    }

    public TextView getLeftTitleView() {
        return leftTitle;
    }

    public TitleActionBar setLeftNoticeText(String text) {
        if (leftNotice != null) {
            leftNotice.setText(text);
        }
        return this;
    }

    public TitleActionBar setLeftNoticeVisible(int visible) {
        if (leftNotice != null) {
            leftNotice.setVisibility(visible);
        }
        return this;
    }

    public void setLeftNoticeBgRes(int resId) {
        if (leftNotice != null) {
            leftNotice.setBackgroundResource(resId);
        }
    }

    public TitleActionBar setLeftTitleSelected() {
        if (leftTitle != null && rightTitle != null) {
            leftTitle.setSelected(true);
            rightTitle.setSelected(false);
        }
        return this;
    }

    public TitleActionBar setLeftPointVisible(int visible) {
        if (leftPoint != null) {
            leftPoint.setVisibility(visible);
        }
        return this;
    }

    public TitleActionBar setLeftTitleClick(final OnBtnClickListener titleClick) {
        if (leftTitle != null && titleClick != null) {
            ClickUtils.clickView(leftTitle, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    setLeftTitleSelected();
                    titleClick.doNext(leftTitle);
                }
            });
        }
        return this;
    }

    public TitleActionBar setRightTitle(String text) {
        if (rightTitle != null) {
            rightTitle.setText(text);
        }
        return this;
    }

    public TitleActionBar setRightNoticeText(String text) {
        if (rightNotice != null) {
            rightNotice.setText(text);
        }
        return this;
    }

    public void setRightNoticeBgRes(int resId) {
        if (rightNotice != null) {
            rightNotice.setBackgroundResource(resId);
        }
    }

    public TitleActionBar setRightNoticeVisible(int visible) {
        if (rightNotice != null) {
            rightNotice.setVisibility(visible);
        }
        return this;
    }

    public TitleActionBar setRightTitleSelected() {
        if (rightTitle != null && leftTitle != null) {
            rightTitle.setSelected(true);
            leftTitle.setSelected(false);
        }
        return this;
    }

    public TitleActionBar clearRightTitleBackGround() {
        if (rightTitle != null) {
            rightTitle.setCompoundDrawables(null, null, null, null);
            rightTitle.setMinWidth(0);
            rightTitle.setMinimumWidth(0);
        }
        return this;
    }

    public TitleActionBar setRightPointVisible(int visible) {
        if (rightPoint != null) {
            rightPoint.setVisibility(visible);
        }
        return this;
    }

    public TitleActionBar setRightTitleClick(final OnBtnClickListener titleClick) {
        if (rightTitle != null && titleClick != null) {
            ClickUtils.clickView(rightTitle, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    setRightTitleSelected();
                    titleClick.doNext(rightTitle);
                }
            });
        }
        return this;
    }


    public TitleActionBar setBtnText(String text) {
        if (btn != null) {
            btn.setText(text);
        }
        return this;
    }

    public void setRightBtnClickEnable(boolean flag) {
        if (btn == null) return;
        btn.setEnabled(flag);
    }

    public void setRightBtnClick(final OnBtnClickListener listener) {
        if (btn != null && listener != null) {
            ClickUtils.clickView(btn, new ClickUtils.ClickCallBack() {
                @Override
                public void doWhat() {
                    listener.doNext(btn);
                }
            });
        }
    }

    public TextView getTitleView() {
        return title;
    }

    public void setTitleLayoutParam(String title, String num) {
        int maxWidth = context.getResources().getDisplayMetrics().widthPixels - DensityUtils.dp2px(context, 88 * 2 + 10 * 2);
        Paint paint = subTitle.getPaint();
        int width = (int) paint.measureText(num);
        Paint paint2 = this.title.getPaint();
        int width2 = (int) paint2.measureText(title);
        if (width + width2 > maxWidth) {
            this.title.setMaxWidth(maxWidth - width - 1);
        }
    }



    public TitleActionBar onClearBtnClick(final OnBtnClickListener listener) {
        if (clearBtn != null && listener != null) {
            ClickUtils.clickView(clearBtn, () -> {
                clearBtn.setVisibility(INVISIBLE);
                editText.setText("");
                listener.doNext(clearBtn);
            });
        }
        return this;
    }

    public TitleActionBar setGoLastBtnVisible(int visible) {
        if (goLast != null) {
            goLast.setVisibility(visible);
        }
        return this;
    }

    public boolean isGoLastBtnVisible() {
        if (goLast != null) {
            return goLast.getVisibility() == VISIBLE;
        }
        return false;
    }

    public TitleActionBar onGoLastClick(OnBtnClickListener listener) {
        if (goLast != null && listener != null) {
            ClickUtils.clickView(goLast, () -> {
                listener.doNext(goLast);
            });
        }
        return this;
    }


    public TitleActionBar onTitleLLClick(OnBtnClickListener listener) {
        if (titleLl != null && listener != null) {
            ClickUtils.clickView(titleLl, () -> listener.doNext(titleLl));
        }
        return this;
    }

    public TitleActionBar setTitleIconVisible(int visible) {
        if (titleIcon != null) {
            titleIcon.setVisibility(visible);
            if (vType == 16) {//需要计算宽度
                int maxWidth =
                        getResources().getDisplayMetrics().widthPixels //屏幕宽
                                - (ltRight1 != null && (ltRight1.getVisibility() == View.VISIBLE || ltRight1.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x45) : 0)
                                - (ltRight2 != null && (ltRight2.getVisibility() == View.VISIBLE || ltRight2.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight3 != null && (ltRight3.getVisibility() == View.VISIBLE || ltRight3.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x36) : 0)
                                - (ltRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE) ? getResources().getDimensionPixelOffset(R.dimen.x23) : 0); //剩余宽度
                if (tvRight4 != null && (ltRight4.getVisibility() == View.VISIBLE || ltRight4.getVisibility() == View.INVISIBLE)) {
                    Paint tvPaint = tvRight4.getPaint();
                    maxWidth -= tvPaint.measureText(tvRight4.getText().toString().trim());
                }

                int arrowWidth = 0;
                if (visible != View.GONE) {
                    arrowWidth = getResources().getDimensionPixelOffset(R.dimen.x7) + getResources().getDimensionPixelOffset(R.dimen.x13);
                }
                int stWidth = 0;
                if (subTitle.getVisibility() != View.GONE) {
                    Paint paint = subTitle.getPaint();
                    stWidth = (int) paint.measureText(subTitle.getText().toString().trim());
                }
                Paint paint = title.getPaint();
                int currWidth = (int) paint.measureText(title.getText().toString().trim());

                int padding = getResources().getDimensionPixelOffset(R.dimen.x15) //左padding
                        + getResources().getDimensionPixelOffset(R.dimen.x15);//右padding

                int width = Math.min(maxWidth, currWidth + stWidth + arrowWidth + padding);
                //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, getResources().getDimensionPixelOffset(R.dimen.top_actionbar_height));
                LayoutParams params = (LayoutParams) titleLl.getLayoutParams();
                params.width = width;
                titleLl.setLayoutParams(params);
            }

        }
        return this;
    }
}
