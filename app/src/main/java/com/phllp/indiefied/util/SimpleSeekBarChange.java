package com.phllp.indiefied.util;

import android.widget.SeekBar;

public class SimpleSeekBarChange implements SeekBar.OnSeekBarChangeListener {

    @FunctionalInterface
    public interface OnStopTracking {
        void onStop(int progress);
    }

    private final OnStopTracking callback;

    public SimpleSeekBarChange(OnStopTracking callback) {
        this.callback = callback;
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) { }

    @Override public void onStartTrackingTouch(SeekBar seekBar) { }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        if (callback != null) {
            callback.onStop(seekBar.getProgress());
        }
    }
}
