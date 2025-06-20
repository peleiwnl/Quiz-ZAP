package com.example.factZAP.utilities

import android.widget.AdapterView

/**
 * A simple abstract implementation of the interface.
 * This class provides a default implementation for the onNothingSelected method
 */
abstract class SimpleOnItemSelectedListener : AdapterView.OnItemSelectedListener {

    /**
     * A default implementation of [onNothingSelected] which does nothing.
     *
     * @param parent The AdapterView where the selection was made.
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        // default
    }
}