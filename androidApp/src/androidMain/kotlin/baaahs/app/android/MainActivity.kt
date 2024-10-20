package baaahs.app.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startForegroundService(Intent(this, SparkleMotionService::class.java))
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)

        val myWebView: WebView = findViewById(R.id.webView)
        val webSettings: WebSettings = myWebView.settings
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl("http://localhost:8004/")

        findViewById<Button>(R.id.reload).setOnClickListener {
            myWebView.reload()
        }
    }
}