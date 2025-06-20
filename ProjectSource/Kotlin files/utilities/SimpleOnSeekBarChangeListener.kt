package com.example.factZAP.utilities

import android.widget.SeekBar


/**
 * A simple abstract implementation of the interface
 * This class provides default implementations for the [onStartTrackingTouch]
 * and [onStopTrackingTouch] methods
 */
abstract class SimpleOnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {

    /**
     * A default implementation of [onStartTrackingTouch] which does nothing.
     *
     * @param seekBar The SeekBar whose state has changed.
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        // default
    }

    /**
     * A default implementation of [onStopTrackingTouch] which does nothing.
     *
     * @param seekBar The SeekBar whose state has changed.
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        // default
    }
}