package com.example.musicpocapp.CustomClient

import android.content.Context
import android.graphics.Bitmap
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout

class CustomWebChromeClient(
    private val context: Context
) : WebChromeClient() {

    companion object {
        private const val TAG = "CustomWebChromeClient"
    }

    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null
    private var originalOrientation: Int = 0
    private var originalSystemUiVisibility: Int = 0

    // This is the key method for handling new windows/tabs
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {
        // Only process if this is triggered by a user gesture (safer)
        if (!isUserGesture) {
            Log.d(TAG, "Blocked non-user initiated window creation")
            return false
        }

        // Create a new WebView
        val newWebView = WebView(context)

        // Set up the new WebView with appropriate settings
        val settings = newWebView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.setSupportMultipleWindows(true)

        // Set the WebViewClient to handle URL loading
        newWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false // Let the WebView load the URL
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d(TAG, "New tab loading: $url")
            }
        }

        // Add the new WebView to your container
//        containerView.addView(newWebView,
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//        )

        // Transfer the WebView to the message (this is required)
        val transport = resultMsg?.obj as? WebView.WebViewTransport
        transport?.webView = newWebView
        resultMsg?.sendToTarget()

        return true
    }

    // Optional: Handle closing the window/tab
    override fun onCloseWindow(window: WebView?) {
        super.onCloseWindow(window)
        Log.d(TAG, "Closing window")

        // Find and remove the WebView from the container
        if (window != null && window.parent != null) {
            (window.parent as? ViewGroup)?.removeView(window)
        }
    }
}