package com.mutant.godutch.widget;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;

public class EmailEditText extends android.support.v7.widget.AppCompatEditText implements CustomEditTextInterface {
	
	public EmailEditText(Context context) {
		super(context);
		initializeSetting();
	}
	
	public EmailEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initializeSetting();
	}
	
	public EmailEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initializeSetting();
	}
	
	@Override
	public void initializeSetting() {
		this.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		this.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
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
	
	public boolean isValid() {
		return Patterns.EMAIL_ADDRESS.matcher(this.getText().toString().trim()).matches();
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