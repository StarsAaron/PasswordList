package com.aaron.passwordlist.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Aaron on 2016/4/24.
 */
public class MyEditText extends android.support.v7.widget.AppCompatEditText {
    private Context context;

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MyEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        Drawable imgInable = context.getResources().getDrawable(android.R.drawable.ic_menu_view);
        setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable, null);
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        rect.left = rect.right - 50;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if(rect.contains(eventX, eventY))
                setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(rect.contains(eventX, eventY))
                setInputType(InputType.TYPE_CLASS_TEXT);
        }
        return super.onTouchEvent(event);
    }
}
