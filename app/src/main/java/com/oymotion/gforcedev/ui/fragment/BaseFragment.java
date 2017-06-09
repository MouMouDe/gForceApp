package com.oymotion.gforcedev.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.oymotion.gforcedev.R;
import com.oymotion.gforcedev.activity.OtherActivity;


/***
 * BaseFragment
 * Created by MouMou
 */

public abstract class BaseFragment extends Fragment {

    protected OtherActivity mOtherActivity;
    private View mViewRoot;
    public SharedPreferences sp;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOtherActivity = (OtherActivity) activity;
        sp = mOtherActivity.getSharedPreferences("config", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mViewRoot == null) {
            mViewRoot = createView(inflater, container, savedInstanceState);
        }
        return mViewRoot;
    }

    public void swichToChildFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getTag());
        }
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }

    abstract protected View createView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mViewRoot != null) {
            ViewParent parent = mViewRoot.getParent();
            if (parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) parent;
                viewGroup.removeView(mViewRoot);

            }
        }
    }

    public void Finish() {
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().finish();
    }

    public void initView(View view, int name) {
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText(name);
        view.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Finish();
            }
        });
    }
}
