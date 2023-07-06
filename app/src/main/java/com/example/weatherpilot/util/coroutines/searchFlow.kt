package com.example.weatherpilot.util.coroutines

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
fun TextInputLayout.searchFlow(): Flow<String> {
            val queryChanged = callbackFlow {
                val watcher = object : TextWatcher {
                    override fun afterTextChanged(editable: Editable?) {
                        editable?.let {  launch {  send(editable.toString()) } }
                    }

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                }

                editText?.addTextChangedListener(watcher)

                awaitClose { editText?.removeTextChangedListener(watcher) }
            }

            return queryChanged.distinctUntilChanged().debounce(1000)
                .flowOn(Dispatchers.IO)
        }