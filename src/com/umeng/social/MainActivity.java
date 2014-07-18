package com.umeng.social;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.laiwang.controller.UMLWHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.yixin.controller.UMYXHandler;

/**
 * 
 * @author mrsimple
 *
 */
public class MainActivity extends Activity {

	/**
	 * 
	 */
	View mShareButton;

	/**
	 * 
	 */
	UMSocialService mController = UMServiceFactory
			.getUMSocialService("myshare");

	Map<String, SHARE_MEDIA> mPlatformsMap = new HashMap<String, SHARE_MEDIA>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		findViewById(R.id.button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showCustomUI(true);
			}
		});

		findViewById(R.id.post_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showCustomUI(false);
					}
				});

		initSocialSDK();
		initPlatformMap();
	}

	/**
	 * 初始化SDK，添加一些平台
	 */
	private void initSocialSDK() {
		// 添加QQ平台
		UMQQSsoHandler qqHandler = new UMQQSsoHandler(MainActivity.this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qqHandler.addToSocialSDK();

		// 添加QQ空间平台
		QZoneSsoHandler qzoneHandler = new QZoneSsoHandler(MainActivity.this,
				"100424468", "c7394704798a158208a74ab60104f0ba");
		qzoneHandler.addToSocialSDK();

		// 添加易信平台
		UMYXHandler yixinHandler = new UMYXHandler(MainActivity.this,
				"yxc0614e80c9304c11b0391514d09f13bf");
		yixinHandler.addToSocialSDK();

		// 添加来往平台
		UMLWHandler laiwangHandler = new UMLWHandler(MainActivity.this,
				"laiwangd497e70d4", "d497e70d4c3e4efeab1381476bac4c5e");
		laiwangHandler.addToSocialSDK();

		// 设置文字分享内容
		mController.setShareContent("这是文字分享内容");
		// 图片分享内容
		mController.setShareMedia(new UMImage(MainActivity.this,
				R.drawable.umeng_socialize_qq_on));

	}

	/**
	 * 初始化平台map
	 */
	private void initPlatformMap() {
		mPlatformsMap.put("新浪微博", SHARE_MEDIA.SINA);
		mPlatformsMap.put("QQ", SHARE_MEDIA.QQ);
		mPlatformsMap.put("QQ空间", SHARE_MEDIA.QZONE);
		mPlatformsMap.put("易信", SHARE_MEDIA.YIXIN);
		mPlatformsMap.put("来往", SHARE_MEDIA.LAIWANG);
		mPlatformsMap.put("人人网", SHARE_MEDIA.RENREN);
	}

	/**
	 * 显示您的自定义界面，当用户点击一个平台时，直接调用directShare或者postShare来分享.
	 */
	private void showCustomUI(final boolean isDirectShare) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
				MainActivity.this);
		dialogBuilder.setTitle("自定义UI");
		// 新浪、QQ、QQ空间、易信、来往、豆瓣、人人平台
		final CharSequence[] items = { "新浪微博", "QQ", "QQ空间", "易信", "来往", "人人网" };
		dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// 获取用户点击的平台
				SHARE_MEDIA platform = mPlatformsMap.get(items[which]);
				if (isDirectShare) {
					// 调用直接分享
					mController.directShare(MainActivity.this, platform,
							mShareListener);
				} else {
					// 调用直接分享, 但是在分享前用户可以编辑要分享的内容
					mController.postShare(MainActivity.this, platform,
							mShareListener);
				}
			} // end of onClick
		});

		dialogBuilder.create().show();
	}

	/**
	 * 分享监听器
	 */
	SnsPostListener mShareListener = new SnsPostListener() {

		@Override
		public void onStart() {

		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int stCode,
				SocializeEntity entity) {
			if (stCode == 200) {
				Toast.makeText(MainActivity.this, "分享成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this,
						"分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				resultCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
}
