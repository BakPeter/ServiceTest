package com.bpapps.servicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.bpapps.servicetest.services.MyBoundedService;
import com.bpapps.servicetest.services.MyIntentService;
import com.bpapps.servicetest.services.MyService;

public class MainViewFragment extends Fragment {
    private static final String TAG = "TAG.MainViewFragment";

    private AppCompatEditText etDataToService;
    private AppCompatButton btnStartService;
    private AppCompatButton btnStopService;
    private AppCompatButton btnStartIntentService;
    private AppCompatButton btnStopIntentService;
    private ProgressBar progressBar;
    private AppCompatButton btnBoundedService;
    private AppCompatTextView tvProgress;

    private MyBoundedService mService;
    private boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBoundedService.MyBinder binder = (MyBoundedService.MyBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.getProgress().observe(getViewLifecycleOwner(), new Observer<Integer>() {
                @Override
                public void onChanged(Integer progress) {
                    progressUpdated(progress);
                }
            });
//            mService.registerProgressChangedListener(new MyBoundedService.IOnProgressChangeListener() {
//                @Override
//                public void onProgressChanged(int newProgress) {
//                    progressUpdated(newProgress);
//                }
//            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void progressUpdated(Integer progress) {
        progressBar.setProgress(progress);
        tvProgress.setText(progress + "%");
    }


    public MainViewFragment() {
        // Required empty public constructor
    }


    public static MainViewFragment newInstance(String param1, String param2) {
        return new MainViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etDataToService = view.findViewById(R.id.etDataToService);

        //service
        btnStartService = view.findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etDataToService.getText().toString();

                Log.d(TAG, "onClick: btnStartService, msg = " + msg);
                Intent intent = new Intent(requireContext(), MyService.class);
                intent.putExtra(MyService.EXTRA_DATA, msg);
                requireActivity().startService(intent);
            }
        });

        btnStopService = view.findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnStopService");

                Intent intent = new Intent(requireContext(), MyService.class);
                requireActivity().stopService(intent);
            }
        });

        //intent service
        btnStartIntentService = view.findViewById(R.id.btnStartIntentService);
        btnStartIntentService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etDataToService.getText().toString();

                Log.d(TAG, "onClick: btnStartIntentService, msg = " + msg);
                Intent intent = new Intent(requireContext(), MyIntentService.class);
                intent.putExtra(MyIntentService.EXTRA_DATA, msg);
                requireActivity().startService(intent);
            }
        });

        btnStopIntentService = view.findViewById(R.id.btnStopIntentService);
        btnStopIntentService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnStopIntentService");

                MyIntentService.stopService();
//                Intent intent = new Intent(requireContext(), MyService.class);
//                requireActivity().stopService(intent);
            }
        });

        //bounded service, LiveData
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        btnBoundedService = view.findViewById(R.id.btnBoundedService);
        btnBoundedService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: btnBoundedService");
                mService.startLongRunningTask();
            }
        });

        tvProgress = view.findViewById(R.id.tvProgress);
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(requireActivity().getApplicationContext(), MyBoundedService.class);
        requireActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unbindService(mConnection);
    }
}