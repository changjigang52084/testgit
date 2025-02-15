package com.xunixianshi.vrshow.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;

/**
 * @description：自定义加载对话框
 * @author 刘成伟（wwwlllll@126.com）
 * @date Jul 16, 2014 2:48:53 PM
 */
public class LoadingDialog extends Dialog {
	private static final int CHANGE_TITLE_WHAT = 1;
	private static final int CHNAGE_TITLE_DELAYMILLIS = 300;
	private static final int MAX_SUFFIX_NUMBER = 3;
	private static final char SUFFIX = '.';

	private ImageView iv_route;
	private TextView detail_tv;
	private TextView tv_point;
	private RotateAnimation mAnim;
	private Context mContext;

	private Handler handler = new Handler() {
		private int num = 0;

		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CHANGE_TITLE_WHAT) {
				StringBuilder builder = new StringBuilder();
				if (num >= MAX_SUFFIX_NUMBER) {
					num = 0;
				}
				num++;
				for (int i = 0; i < num; i++) {
					builder.append(SUFFIX);
				}
				tv_point.setText(builder.toString());
				if (isShowing()) {
					handler.sendEmptyMessageDelayed(CHANGE_TITLE_WHAT, CHNAGE_TITLE_DELAYMILLIS);
				} else {
					num = 0;
				}
			}
		};
	};

	public LoadingDialog(Context context) {
		super(context, R.style.Dialog_bocop);
		this.mContext = context;
		init();
	}

	public LoadingDialog(Context context, boolean isTrans) {
		super(context, isTrans ? R.style.Loading_Dialog_trans : R.style.Dialog_bocop);
		this.mContext = context;
		init();
	}

	private void init() {
		setContentView(R.layout.common_dialog_loading_layout);
		iv_route = (ImageView) findViewById(R.id.iv_route);
		detail_tv = (TextView) findViewById(R.id.detail_tv);
		tv_point = (TextView) findViewById(R.id.tv_point);
		initAnim();
		getWindow().setWindowAnimations(R.style.dialog_animstyle);
	}

	private void initAnim() {
//		mAnim = new RotateAnimation(360, 0, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
		mAnim = new RotateAnimation(0, 360, Animation.RESTART, 0.5f, Animation.RESTART, 0.5f);
		mAnim.setDuration(2000);
		mAnim.setRepeatCount(Animation.INFINITE);
		mAnim.setRepeatMode(Animation.RESTART);
		mAnim.setStartTime(Animation.START_ON_FIRST_FRAME);
	}

	@Override
	public void show() {
		iv_route.startAnimation(mAnim);
		handler.sendEmptyMessage(CHANGE_TITLE_WHAT);
		super.show();
	}

	@Override
	public void dismiss() {
		mAnim.cancel();
		super.dismiss();
	}

	@Override
	public void setTitle(CharSequence title) {
		if (TextUtils.isEmpty(title)) {
			detail_tv.setText("正在加载");
		}else {
			detail_tv.setText(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getContext().getString(titleId));
	}

	public static void dismissDialog(LoadingDialog loadingDialog) {
		if (null == loadingDialog) { return; }
		loadingDialog.dismiss();
	}
}
