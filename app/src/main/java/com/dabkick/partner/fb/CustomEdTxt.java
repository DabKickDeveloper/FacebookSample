package com.dabkick.partner.fb;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by developer3 on 10/9/15.
 */
public class CustomEdTxt extends EditText{

    onKeyPreImeListener onKeyPreImeListener = null;

    public CustomEdTxt(Context context) {
        super(context);
    }

    public CustomEdTxt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEdTxt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnKeyPreImeListener(onKeyPreImeListener listener)
    {
        onKeyPreImeListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onKeyPreImeListener != null)
        {
            onKeyPreImeListener.onKeyPreImeListener(keyCode,event);
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public interface onKeyPreImeListener{
        void onKeyPreImeListener(int keyCode, KeyEvent event);
    }
}
