package com.qimai

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Math.*
import kotlin.math.abs

class RelativeExpandCloseLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    init {
    }

    var isCanScroll = true
    private val TAG = "RelativeExpandClose"
    var maxLength = 400
    //确定是否是刷新行为 少于这个点放开就是关闭整个行为 大于这个点小于等于mBehindInitHeight都认为是刷新操作
    var mFirstHeight = 200
    //等于这个节点 震动，大于这个点第二个页面就必须要展示
    var mBehindInitHeight = 300
    var mThreeHeight = 350
    var mBehidBeginShow = 400
    var mHideRefreshDistance = 500
    var mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var lastY: Float = 0F
    lateinit var mFrontView: View
    lateinit var mBehindView: View
    lateinit var mRefresh: LinearLayout
    var isCanAccondScrollChange = true
    var mVibrator = true
    var mIsFirstView = true
    var mSlidingMode = NONE
    var mFlag = false
    override fun onFinishInflate() {
        super.onFinishInflate()
        mFrontView = findViewById(R.id.tv_1)
        mBehindView = findViewById(R.id.tv_2)
        mRefresh = findViewById(R.id.iv_refresh)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {

        if (ev!!.action == MotionEvent.ACTION_DOWN) {
            mVibrator = true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var isIntercept = false
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                isIntercept = false
                mFlag = true

                lastY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                //判断是否要拦截
                var delY = ev.y - lastY


                if (getFrontViewTranslationY() < 0 && mIsFirstView) {
                    isIntercept = false
                } else if (mBehindView.translationY == mBehindInitHeight.toFloat() && delY < 0) {
                    isIntercept = false
                } else if (abs(delY) > mTouchSlop) {
                    isIntercept = true
                    lastY = ev.y
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return isIntercept && isCanScroll
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var isHandle = false
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mSlidingMode = NONE
                Log.d(TAG, "onTouchEvent: fontView y = ${mFrontView.y} top= ${mFrontView.top}")
                Log.d(TAG, "onTouchEvent: behindView y = ${mBehindView.y} top= ${mBehindView.top}")
                lastY = event.y
                isHandle = false
            }
            MotionEvent.ACTION_MOVE -> {
                resetAnimatorDrawable()
                //到这里来就说明可以滑动，直接处理即可
                //(parent as ViewGroup).scrollBy(0, -(event.y - lastY).toInt())
                if (mFrontView.translationY < 0 && mIsFirstView) {
                    startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
                }
                /*if (event.y - lastY < 0 && mFrontView.translationY >=0f && mIsFirstView) {
                    return false
                }*/
                var delY = event.y - lastY

                Log.d(TAG, "onTouchEvent: mFlag= $mFlag delY= $delY")
                if (mFlag && delY > 0) {
                    Log.d(TAG, "onTouchEvent: mSlidingMode glissade")
                    mSlidingMode = glissade
                    mFlag = false
                }
                if (mFlag && delY < 0) {
                    mSlidingMode = pullUp
                    Log.d(TAG, "onTouchEvent: mSlidingMode pullUp ")
                    mFlag = false
                }

                Log.d(TAG, "onTouchEvent: mSlidingMode= $mSlidingMode mFlag= $mFlag delY= $delY")

                //前一页始终可以变化
                mFrontView.translationY += delY

                var mFrontViewTranslationY = getFrontViewTranslationY()

                Log.d(TAG, "onTouchEvent: mFrontViewTranslationY= ${mFrontViewTranslationY}")
                //下滑
                if (mFrontViewTranslationY > 0) {
                    //如果下拉过程中
                    if (mFrontViewTranslationY < 0) {
                        //mFrontView.translationY = 0f
                    }
                    //没有发生交换
                    if (mIsFirstView) {
                        //如果小于等于滑动的高度 根据比例缩放加载框背景
                        if (mFrontViewTranslationY <= mRefresh.height) {
                            mRefresh.translationY += delY
                            var precent =
                                (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                            mRefresh.scaleX = precent
                            mRefresh.scaleY = precent
                        }

                        //如果超过了刷新框的高度，但是小于第二个页面的高度，则把刷新框调整并正确的显示
                        else if (mFrontViewTranslationY > mRefresh.height && mFrontViewTranslationY < mBehindInitHeight) {
                            mRefresh.translationY = 0f
                            mRefresh.scaleX = 1f
                            mRefresh.scaleY = 1f
                            Log.d(
                                TAG,
                                "onTouchEvent: frontViewTranslate > mRefresh.height && frontViewTranslate < mBehindInitHeight"
                            )

                        } else if (mFrontViewTranslationY > mBehindInitHeight.toFloat()) {
                            Log.d(
                                TAG,
                                "onTouchEvent: mFrontViewTranslationY > mBehindInitHeight.toFloat()"
                            )
                            //后一个页面开始移动
                            mBehindView.translationY += -delY
                            //震动
                            Log.d(TAG, "onTouchEvent: test211111")
                            if (mVibrator) {
                                var vibrator: Vibrator =
                                    context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrator.vibrate(20)
                                mVibrator = false
                            }
                            //如果上下移动超过他的高度给他复位
                            if (abs(mRefresh.translationY) > mRefresh.height) {
                                mRefresh.translationY = -mRefresh.height.toFloat()
                            } else {
                                //如果是上移，就按距离缩放
                                mRefresh.translationY += -delY
                                var precent =
                                    (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                                mRefresh.scaleX = precent
                                mRefresh.scaleY = precent
                            }
                        }
                    } else {
                        mRefresh.translationY += delY
                        if (mFrontViewTranslationY <= mRefresh.height) {
                            var precent =
                                (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                            mRefresh.scaleX = precent
                            mRefresh.scaleY = precent
                        } else {
                            mRefresh.scaleX = 1f
                            mRefresh.scaleY = 1f
                        }
                    }
                }
                //上滑
                else if (mFrontViewTranslationY < 0) {

                    if (!mIsFirstView) {
                        mBehindView.translationY += delY
                    } else {

                    }
                }

                //当mRefresh未超过自身高度的时候是可以随着frontView一起移动的
                /*         var frontViewTranslate = getAbsTranslationY()
                         //这里判断如果
                         if (frontViewTranslate <= mRefresh.height) {
                             mRefresh.translationY += delY
                             var precent =
                                 (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                             mRefresh.scaleX = precent
                             mRefresh.scaleY = precent
                         } else if (frontViewTranslate > mRefresh.height && frontViewTranslate < mBehindInitHeight && mIsFirstView) {
                             mRefresh.translationY = 0f
                             mRefresh.scaleX = 1f
                             mRefresh.scaleY = 1f
                             Log.d(
                                 TAG,
                                 "onTouchEvent: frontViewTranslate > mRefresh.height && frontViewTranslate < mBehindInitHeight"
                             )
                         } else if (frontViewTranslate > mBehindInitHeight.toFloat() && mIsFirstView) {
                             if (mIsFirstView) {
                                 //震动
                                 if (mVibrator) {
                                     var vibrator: Vibrator =
                                         context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                     vibrator.vibrate(20)
                                     mVibrator = false
                                 }
                                 //加载框与下一个页面都上移动
                                 if (Math.abs(mRefresh.translationY) > mRefresh.height) {
                                     mRefresh.translationY = -mRefresh.height.toFloat()
                                 } else {
                                     mRefresh.translationY += -delY
                                     var precent =
                                         (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                                     mRefresh.scaleX = precent
                                     mRefresh.scaleY = precent
                                 }
                                 mBehindView.translationY += -delY
                             } else {
                                 mBehindView.translationY += delY
                             }

                         } else {
                             mRefresh.translationY += delY
                         }

                         Log.d(
                             TAG,
                             "onTouchEvent: ACTION_MOVE mRefresh.translationY= ${mRefresh.translationY} mRefresh.height= ${mRefresh.height}"
                         )*/

                //refresh
                /*  if (Math.abs(mFrontView.translationY) <= mRefresh.height) {
                      mRefresh.translationY += delY
                  }

                  if (mRefresh.translationY <= 0) {
                      mRefresh.translationY += delY
                  } else {
                      mRefresh.translationY = 0f
                  }
                  //加载框缩放动画
                  if (mRefresh.translationY <= 0) {
                      var precent =
                          (mRefresh.height + mRefresh.translationY) / mRefresh.height.toFloat()
                      mRefresh.scaleX = precent
                      mRefresh.scaleY = precent
                  }*/
                // refreshReset()
                //加载页面位置不变
                //mRefresh.translationY += event.y - lastY
                /*  if (isCanAccondScrollChange && Math.abs(mRefresh.translationY) > mBehidBeginShow) {
                      mRefresh.translationY = -mRefresh.height.toFloat()
                      mRefresh.alpha = 0f
                  } else {
                      mBehindView.translationY += event.y - lastY
                      var precent =
                          Math.abs(mBehindView.height + mBehindView.translationY) / mBehindView.height
                      setAlphaAnimation(
                          mBehindView,
                          precent
                      )
                  }*/
                /* if (isCanAccondScrollChange) {
                     mRefresh.translationY += delY
                     mBehindView.translationY += delY

                     if (Math.abs(mRefresh.translationY) > mBehidBeginShow) {
                         setAlphaAnimation(
                             mRefresh,
                             (mHideRefreshDistance - Math.abs(mRefresh.translationY)) / mHideRefreshDistance.toFloat()
                         )
                         // mRefresh.alpha = 0f
                         var precent =
                             Math.abs(mBehindView.height + mBehindView.translationY) / mBehindView.height
                         setAlphaAnimation(
                             mBehindView,
                             precent
                         )
                     } else {
                         mRefresh.alpha = 1f
                         mBehindView.alpha = 0f
                     }
                 } else {
                     mRefresh.translationY += delY
                     mRefresh.alpha = 1f
                 }*/
                //在这里处理刷新头
                // mRefresh.translationY += delY
                //隐藏
                //处理第二个
                /*if (refreshIv.height < refreshIv.translationY) {
                    refreshIv.visibility = View.GONE
                } else {
                    refreshIv.translationY = event.y - lastY
                }*/
                /*       if (isCanAccondScrollChange) {
                           mBehindView
                               .translationY += event.y - lastY
                           Log.d(
                               TAG,
                               "onTouchEvent: mBehindView.height = ${mBehindView.height} mBehindView.translationY= ${mBehindView.translationY} (mBehindView.height-mBehindView.translationY)/ mBehindView.height= ${(mBehindView.height - mBehindView.translationY) / mBehindView.height}"
                           )
                           //因为后面的VIEWtranslationY 一直都是<=0
                           var precent =
                               Math.abs(mBehindView.height + mBehindView.translationY) / mBehindView.height

                           setAlphaAnimation(
                               mBehindView,
                               precent
                           )
                           Log.d(
                               TAG,
                               "onTouchEvent: (precent)= ${precent} heght= $height"
                           )
                       }*/

                isHandle = true
                lastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "onTouchEvent:ACTION_UP  mRefresh translateY = ${mRefresh.translationY}")
                var frontdelYY = getFrontViewTranslationY()
                //上拉
                if (frontdelYY < 0) {

                    if (getAbsTranslationY() > mFirstHeight) {
                        startTranslateYAnimator(
                            mFrontView,
                            mFrontView.translationY,
                            mBehindInitHeight.toFloat()
                        )
                        startTranslateYAnimator(mBehindView, mBehindView.translationY, 0f)
                        var tempView = mBehindView
                        mBehindView = mFrontView
                        mFrontView = tempView
                        mIsFirstView = !mIsFirstView

                    }
                }
                //下拉
                else if (frontdelYY > 0) {
                    //如果下拉高度没有超过刷新框的高度 就还原
                    if (frontdelYY < getRefreshHeight()) {
                        //mRefresh.translationY = (-getRefreshHeight()).toFloat()
                        //还原前台view
                        startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
                        //还原刷新view
                        startTranslateYAnimator(
                            mRefresh,
                            mRefresh.translationY,
                            -mRefresh.height.toFloat()
                        )
                        resetBehindView()
                    } else if (frontdelYY >= getRefreshHeight() && (frontdelYY < mBehindInitHeight.toFloat())) {
//加载gif
                        showRefreshGif()
                        //fontView滑到刷新view下面
                        startTranslateYAnimator(
                            mFrontView,
                            mFrontView.translationY,
                            getRefreshHeight()
                        )
                        startTranslateYAnimator(
                            mRefresh,
                            mRefresh.translationY,
                            0f
                        )
                        //停止刷新
                        iv_logo.postDelayed({
                            finishRefresh()
                        }, 2000)
                        resetBehindView()
                    } else if (frontdelYY >= mBehindInitHeight.toFloat()) {
                        if (mIsFirstView) {
                            startTranslateYAnimator(
                                mRefresh,
                                mRefresh.translationY,
                                -mRefresh.height.toFloat()
                            )
                            startTranslateYAnimator(
                                mFrontView,
                                mFrontView.translationY,
                                mFrontView.height.toFloat()
                            )
                            startTranslateYAnimator(mBehindView, mBehindView.translationY, 0f)
                            var tempView = mBehindView
                            mBehindView = mFrontView
                            mFrontView = tempView
                            mIsFirstView = !mIsFirstView


                        } else {
                            startTranslateYAnimator(
                                mRefresh,
                                mRefresh.translationY,
                                0f
                            )
                            //加载动画
                            showRefreshGif()
                            //fontView滑到onePoint
                            startTranslateYAnimator(
                                mFrontView,
                                mFrontView.translationY,
                                mRefresh.height.toFloat()
                            )
                            //停止刷新
                            iv_logo.postDelayed({
                                finishRefresh()
                            }, 2000)
                        }
                    }
                }
                //第一个节点，超过第一个节点意味着可以刷新
                /*      var onePoint = mRefresh.height
                      if (getAbsTranslationY() < onePoint) {
                          //回退到初始状态
                          startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
                          startTranslateYAnimator(
                              mRefresh,
                              mRefresh.translationY,
                              -mRefresh.height.toFloat()
                          )
                          resetBehindView()

                      } else if (getAbsTranslationY() >= onePoint && (getAbsTranslationY() < mBehindInitHeight.toFloat())) {
                          //加载gif
                          showRefreshGif()
                          //fontView滑到onePoint
                          startTranslateYAnimator(
                              mFrontView,
                              mFrontView.translationY,
                              mRefresh.height.toFloat()
                          )
                          //停止刷新
                          iv_logo.postDelayed({
                              finishRefresh()
                          }, 2000)
                      } else if (getAbsTranslationY() >= mBehindInitHeight.toFloat()) {

                          if (mIsFirstView) {
                              startTranslateYAnimator(
                                  mRefresh,
                                  mRefresh.translationY,
                                  -mRefresh.height.toFloat()
                              )
                              startTranslateYAnimator(
                                  mFrontView,
                                  mFrontView.translationY,
                                  mFrontView.height.toFloat()
                              )
                              startTranslateYAnimator(mBehindView, mBehindView.translationY, 0f)
                              var tempView = mBehindView
                              mBehindView = mFrontView
                              mFrontView = tempView
                              mIsFirstView = !mIsFirstView
                          } else {
                              startTranslateYAnimator(
                                  mRefresh,
                                  mRefresh.translationY,
                                  0f
                              )
                              //加载gif
                              showRefreshGif()
                              //fontView滑到onePoint
                              startTranslateYAnimator(
                                  mFrontView,
                                  mFrontView.translationY,
                                  mRefresh.height.toFloat()
                              )
                              //停止刷新
                              iv_logo.postDelayed({
                                  finishRefresh()
                              }, 2000)
                          }
                      }*/


                //判断能不能刷新
                /* if (Math.abs(mFrontView.translationY) <= mBehidBeginShow) {
                     stopRefresh()
                 } else {
                     if (!isCanAccondScrollChange) {
                         startRefresh()
                     } else {
                         startTranslateYAnimator(mBehindView, mBehindView.translationY, 0f)
                         //mBehindView.translationY = 0f
                         mFrontView.translationY = (-mFrontView.height).toFloat()
                         setAlphaAnimation(mBehindView, 1f)
                         isCanAccondScrollChange = false
                         //前后view交换
                         var tempView = mBehindView
                         mBehindView = mFrontView
                         mFrontView = tempView
                     }
                 }*/
                //能刷新
                //不能刷新

                /* if (Math.abs(mFrontView.translationY) > maxLength && isCanAccondScrollChange) {
                     if (isCanAccondScrollChange) {
                         startTranslateYAnimator(mBehindView, mBehindView.translationY, 0f)
                         //mBehindView.translationY = 0f
                         mFrontView.translationY = (-mFrontView.height).toFloat()
                         setAlphaAnimation(mBehindView, 1f)
                         isCanAccondScrollChange = false
                         //前后view交换
                         var tempView = mBehindView
                         mBehindView = mFrontView
                         mFrontView = tempView
                     }
                 } else {
                     startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
                     mBehindView.translationY = -mBehindView.height.toFloat()
                 }*/
                //  startTranslateYAnimator(mRefresh, mRefresh.translationY, mRefresh.height.toFloat())
                //mRefresh.translationY = (-mRefresh.height).toFloat()
                Log.d(TAG, "onTouchEvent: mRefresh.translationY= ${mRefresh.translationY}")
                if (mRefresh.translationY < 0) {
                    var animationDrawable: AnimationDrawable =
                        iv_logo.background as AnimationDrawable
                    if (animationDrawable.isRunning) {
                        //  animationDrawable.stop()
                    }
                }
            }
        }
        return isHandle
    }

    private fun getRefreshHeight(): Float {
        return mRefresh.height.toFloat()
    }

    private fun resetBehindView() {
        if (mIsFirstView) {
            startTranslateYAnimator(
                mBehindView,
                mBehindView.translationY,
                mBehindInitHeight.toFloat()
            )
        } else {
            startTranslateYAnimator(
                mBehindView,
                mBehindView.translationY,
                mBehindView.height.toFloat()
            )
        }
    }

    private fun getFrontViewTranslationY() = mFrontView.translationY

    private fun getAbsTranslationY() = abs(mFrontView.translationY)

    /***
     * 重置帧动画
     * **/
    private fun resetAnimatorDrawable() {
        var animationDrawable: AnimationDrawable = iv_logo.background as AnimationDrawable
        if (animationDrawable.isRunning) {
            animationDrawable.selectDrawable(0)
            animationDrawable.stop()
        }
        animationDrawable.selectDrawable(0)
    }

    private fun finishRefresh() {
        startTranslateYAnimator(mRefresh, mRefresh.translationY, -mRefresh.height.toFloat(),
            object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    //  iv_logo.setImageResource(R.drawable.qm_log)
                    var animationDrawable: AnimationDrawable =
                        iv_logo.background as AnimationDrawable
                    if (animationDrawable.isRunning) {
                        animationDrawable.stop()
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
        startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
    }

    private fun showRefreshGif() {
        /* Glide.with(iv_logo.context)
             .asGif()
             .load(R.drawable.qm_loading)
             .into(iv_logo)*/
        var animationDraw: AnimationDrawable = iv_logo.background as AnimationDrawable
        if (!animationDraw.isRunning) {
            animationDraw.stop()
            animationDraw.selectDrawable(0)
        }
        animationDraw.start()

    }

    private fun refreshReset() {
        if (Math.abs(mRefresh.translationY) > mRefresh.height) {
            mRefresh.translationY = -mRefresh.height.toFloat()
            mRefresh.scaleY = 1f
            mRefresh.scaleX = 1f
        }
    }

    private fun startRefresh() {

    }

    private fun stopRefresh() {
        if (isCanAccondScrollChange) {
            startTranslateYAnimator(
                mBehindView,
                mBehindView.translationY,
                -mBehindView.height.toFloat()
            )
            // mBehindView.translationY = -mBehindView.height.toFloat()
        }
        mRefresh.translationY = -mRefresh.height.toFloat()
        mRefresh.alpha = 1f
        startTranslateYAnimator(mFrontView, mFrontView.translationY, 0f)
        // mFrontView.translationY = 0f

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBehindView.apply {
            Log.d(TAG, "onSizeChanged: measuredHeight= $measuredHeight")
            translationY = mBehindInitHeight.toFloat()
            //setPadding(0,-measuredHeight,0,0)
            //scrollY = measuredHeight
        }
        mFirstHeight = iv_refresh.measuredHeight
        findViewById<View>(R.id.iv_refresh)
            .apply {
                //  translationY = (-measuredHeight).toFloat()
            }
        Log.d(TAG, "onSizeChanged: mRefresh height= ${mRefresh.height}")
        mRefresh.translationY = (-mRefresh.measuredHeight).toFloat()
    }

    companion object {
        //上拉
        val pullUp = 1
        //下滑
        val glissade = 2
        //未知
        val NONE = 3
    }

    fun setAlphaAnimation(view: View, precent: Float) {
        view.alpha = precent
    }

    fun startTranslateYAnimator(
        view: View,
        startTranslateX: Float,
        endTranslateY: Float,
        listener: Animator.AnimatorListener? = null
    ) {
        var animator = ObjectAnimator.ofFloat(view, "translationY", startTranslateX, endTranslateY)
        if (listener != null) {
            animator.addListener(listener)
        }
        animator.start()
    }
}