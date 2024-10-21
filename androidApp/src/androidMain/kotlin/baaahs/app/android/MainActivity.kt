package baaahs.app.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        startForegroundService(Intent(this, SparkleMotionService::class.java))
        super.onCreate(savedInstanceState)

        goFullscreen()

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

    private fun goFullscreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

//        // Add a listener to update the behavior of the toggle fullscreen button when
//        // the system bars are hidden or revealed.
//        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
//            // You can hide the caption bar even when the other system bars are visible.
//            // To account for this, explicitly check the visibility of navigationBars()
//            // and statusBars() rather than checking the visibility of systemBars().
//            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
//                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
//            ) {
//                binding.toggleFullscreenButton.setOnClickListener {
//                    // Hide both the status bar and the navigation bar.
//                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
//                }
//            } else {
//                binding.toggleFullscreenButton.setOnClickListener {
//                    // Show both the status bar and the navigation bar.
//                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
//                }
//            }
//            ViewCompat.onApplyWindowInsets(view, windowInsets)
//        }
    }
}