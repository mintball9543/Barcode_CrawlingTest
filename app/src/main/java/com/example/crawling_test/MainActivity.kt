package com.example.crawling_test

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jsoup.Jsoup
import org.jsoup.select.Elements

class MainActivity : AppCompatActivity() {
    private lateinit var txtProductName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtProductName = findViewById(R.id.txtProductName)
        setProductInfo("8801094012403")

    }

    // 상품정보 크롤링
    private fun setProductInfo(code: String) {
        suspend fun getResultFromApi(): String {
            // do something
            val url = "https://www.koreannet.or.kr/front/koreannet/gtinSrch.do?gtin=${code}"
            val doc = Jsoup.connect(url).timeout(1000 * 10).get()  //타임아웃 10초
//            val contentData : Elements = doc.select("div.productTit")
//            val productName = contentData.toString().substringAfterLast("&nbsp;").substringBefore("</div>")
            val productName = doc.select("div.nm").text()
            Log.i("CrawlingTest", "productName : $productName")
            var rtnValue : String = ""
            if ( productName.toString().trim() !="" ) {
                rtnValue = productName.toString().trim()
            }
            else {
                rtnValue = "유통물류 DB에 등록되지 않은 코드입니다."
            }
            return rtnValue
        }

        CoroutineScope(IO).launch {
            val resultStr = withTimeoutOrNull(10000) {
                getResultFromApi()
            }

            if (resultStr != null) {
                withContext(Main) {

                    txtProductName.text = resultStr
                    Log.d("CrawlingTest", "Result: $resultStr")

                }
            }
        }
    }
}