package com.example.musicpocapp.CustomClient

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Custom WebViewClient that overrides URL loading behavior
 */
class CustomWebViewClient(private val context: Context) : WebViewClient() {

    companion object {
        private const val TAG = "CustomWebViewClient"
    }

    /**
     * Called when a URL is about to be loaded.
     * This method was deprecated in API level 24.
     *
     * @param view The WebView that is initiating the callback
     * @param url The URL to be loaded
     * @return true if the host application wants to handle the URL loading itself, otherwise return false
     */
    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        url?.let {
            Log.d(TAG, "shouldOverrideUrlLoading (deprecated): $it")

            // Example of URL handling logic
            if (it.contains("example.com")) {
                // Handle example.com URLs in a special way
                Log.i(TAG, "Handling example.com URL: $it")
                // Custom handling logic here

                // Return true to indicate we handled the URL
                return true
            }
        }

        // Let WebView handle the URL
        return false
    }

    /**
     * Called when a URL is about to be loaded.
     * This method is for API level 24 and above.
     *
     * @param view The WebView that is initiating the callback
     * @param request The WebResourceRequest for the URL to be loaded
     * @return true if the host application wants to handle the URL loading itself, otherwise return false
     */
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString()
        url?.let {
            Log.d(TAG, "shouldOverrideUrlLoading: $it")

            // Example of URL handling logic
            if (it.contains("example.com")) {
                // Handle example.com URLs in a special way
                Log.i(TAG, "Handling example.com URL: $it")
                // Custom handling logic here

                // Return true to indicate we handled the URL
                return true
            }
        }

        // Let WebView handle the URL
        return false
    }

    /**
     * Called when the WebView starts loading a page
     */
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Log.d(TAG, "Page load started: $url")
    }

    /**
     * Called when the page finishes loading
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Log.d(TAG, "Page load finished: $url")
        view?.evaluateJavascript("""
            (function() {
                // Find all likely download buttons
                var buttons = document.querySelectorAll('a[href*="download"], button:contains("download"), [class*="download"], [id*="download"]');
                
                for(var i=0; i < buttons.length; i++) {
                    buttons[i].addEventListener('click', function(e) {
                        console.log('Download button clicked:', this.href || this.getAttribute('data-url') || 'No direct URL');
                        window.Android.onDownloadButtonClicked(this.href || this.getAttribute('data-url') || 'Unknown URL');
                    }, true);
                }
                
                return 'Monitoring ' + buttons.length + ' potential download buttons';
            })();
        """, null)

    }

    /**
     * Called if there is an error loading a resource
     */
    @Deprecated("Deprecated in Java")
    override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        Log.e(TAG, "Error loading URL: $failingUrl - $description")
    }

    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
        val url = request?.url?.toString()

        // Log all network requests to help with debugging
        Log.d("NetworkDebug", "Request: $url")

        // Specifically watch for the random-cdn API call
        if (url?.contains("media.savetube.me/api/random-cdn") == true) {
            try {
                // Make the request ourselves to see what it returns
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = request.method

                // Add any headers from the original request
                request.requestHeaders.forEach { (key, value) ->
                    connection.setRequestProperty(key, value)
                }

                // Execute the request
                connection.connect()

                // Read the response
                val responseCode = connection.responseCode
                val inputStream = if (responseCode < 400) connection.inputStream else connection.errorStream
                val responseBody = inputStream.bufferedReader().use { it.readText() }

                Log.d("NetworkDebug", "CDN API Response: $responseBody")

                // Try to parse the JSON response
                try {
                    // Assuming the response contains a URL or data needed for download
                    val jsonResponse = JSONObject(responseBody)

                    // The exact field names depend on the API response format
                    // These are guesses based on common patterns
                    val downloadUrl = jsonResponse.optString("url")
                        ?: jsonResponse.optString("download_url")
                        ?: jsonResponse.optString("cdn_url")

                    if (downloadUrl.isNotEmpty()) {
                        Log.d("DownloadDebug", "Extracted download URL: $downloadUrl")

                        // Trigger download with this URL
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            // Use DownloadManager to download the file
                            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            val request = DownloadManager.Request(Uri.parse(downloadUrl))
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "download_${System.currentTimeMillis()}.mp3")
                            downloadManager.enqueue(request)

                            Toast.makeText(context, "Starting download...", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DownloadDebug", "Error parsing API response", e)
                }
            } catch (e: Exception) {
                Log.e("NetworkDebug", "Error intercepting random-cdn request", e)
            }
        }

        // Let the WebView handle the request normally
        return super.shouldInterceptRequest(view, request)
    }

}