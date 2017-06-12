package com.mutant.godutch.widget;

import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.mutant.godutch.utils.Patterns;
import com.mutant.godutch.utils.Utility;

public class PasswordEditText extends android.support.v7.widget.AppCompatEditText implements CustomEditTextInterface {
	private final int maxLength = 20;
	
	public PasswordEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public PasswordEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public PasswordEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		Utility.INSTANCE.setMaxLength(this, maxLength);
	}
	
	public boolean isValid() {
		return Patterns.INSTANCE.getPASSWORD().matcher(this.getText().toString()).matches();
	}
	
	public boolean isEmpty() {
		return TextUtils.isEmpty(this.getText().toString());
	}
	
	@Override
	public void setError(CharSequence error) {
		this.requestFocus();
		super.setError(error);
	}
}