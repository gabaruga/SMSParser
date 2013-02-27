package com.pino.smsparser;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class TotalDlg extends android.app.DialogFragment implements OnEditorActionListener {
	private EditText edit;
	
	public TotalDlg() {
		// have to be here
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.total_dlg, container);
		edit = (EditText)view.findViewById(R.id.total);
		getDialog().setTitle("Enter baseline sum");
		
		edit.requestFocus();
		getDialog().getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        edit.setOnEditorActionListener(this);

		return view;
	}

	@Override
	public boolean onEditorAction(TextView edit, int action, KeyEvent event) {
		if (action == EditorInfo.IME_ACTION_DONE) {
			MainActivity activity = (MainActivity) getActivity();
            activity.onFinishTotalDlg(edit.getText().toString());
            this.dismiss();
            return true;
		}
		
		return false;
	}
}
