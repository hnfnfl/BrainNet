package com.jaylangkung.korem

import android.content.Intent
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.jaylangkung.korem.databinding.ActivityWebviewBinding

class WebviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding

    companion object {
        var webviewUrlPost = String()
        var webviewJudul = String()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    startActivity(Intent(this@WebviewActivity, MainActivity::class.java))
                    finish()
                }
            }
        })

        binding.apply {
            webView.apply {
                webViewClient = WebViewClient()
                loadUrl(webviewUrlPost)
                settings.javaScriptEnabled = true
                settings.setSupportZoom(true)
            }

            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            webviewJudulPost.text = webviewJudul
        }
    }
}