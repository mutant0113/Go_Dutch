package com.mutant.godutch.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;

import com.mutant.godutch.utils.Utility;

public class UserNameEditText extends android.support.v7.widget.AppCompatEditText implements CustomEditTextInterface {
	private final int maxLength = 20;
	
	public UserNameEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public UserNameEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public UserNameEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT);
		this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		Utility.INSTANCE.setMaxLength(this, maxLength);
		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setError(null);
			}
		});
	}
	
	// 暱稱沒有限制，所以一律回傳true
	public boolean isValid() {
		return true;
	}
	
	public boolean isEmpty() {
		return TextUtils.isEmpty(this.getText().toString().trim());
	}
	
	@Override
	public void setError(CharSequence error) {
		this.requestFocus();
		super.setError(error);
	}
}