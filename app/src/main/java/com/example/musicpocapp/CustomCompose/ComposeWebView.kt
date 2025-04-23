package com.example.musicpocapp.CustomCompose

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.webkit.WebView
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.musicpocapp.CustomClient.CustomWebChromeClient
import com.example.musicpocapp.CustomClient.CustomWebViewClient


@Composable
fun ComposeWebView(url: String){
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient= CustomWebViewClient(context)
                webChromeClient= CustomWebChromeClient(context)
                settings.javaScriptEnabled= true
                settings.allowContentAccess = true
                settings.allowFileAccess = true
                settings.useWideViewPort = true
                settings.domStorageEnabled = true  // This is critical - enables localStorage
                settings.databaseEnabled = true
                CookieManager.getInstance().setAcceptCookie(true)

                // Allow popup windows (for ads)
                settings.setSupportMultipleWindows(true)
                settings.javaScriptCanOpenWindowsAutomatically = true

                setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                    // Handle download request here

                    // Option 1: Use Android's Download Manager
                    val request = DownloadManager.Request(Uri.parse(url))
                    request.setMimeType(mimetype)
                    request.addRequestHeader("User-Agent", userAgent)
                    request.setDescription("Downloading file...")

                    // Extract filename from contentDisposition
                    val fileName = URLUtil.guessFileName(url, contentDisposition, mimetype)

                    val regex = Regex("""filename="([^"]+)"""")
                    val matchResult = regex.find(contentDisposition)
                    var songName= ""
                    matchResult?.let {
                        val filename = it.groupValues[1] // "Game Changer-yt.savetube.me.mp3"

                        // Extract the part before "-yt.savetube.me.mp3"
                        songName = filename.split("-yt.savetube.me.mp3")[0]
                    }
                    request.setTitle(songName)

                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)

                    Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show()
                }

                loadUrl(url)

            }

        },
        modifier = Modifier.fillMaxWidth(),
        update = { webview ->
            webview.loadUrl(url)
        }
    )
}