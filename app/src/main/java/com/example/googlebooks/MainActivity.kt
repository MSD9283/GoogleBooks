package com.example.googlebooks

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton: Button = findViewById(R.id.buttonSearch)
        searchButton.setOnClickListener {
            MyAsyncTask().execute();
        }

    }
    inner class MyAsyncTask : AsyncTask<Void, Int, Void>() {

        var img : String = ""

        override fun onPreExecute() {
        }

        override fun doInBackground(vararg param: Void?): Void? {

            val text : EditText = findViewById(R.id.editTextTextPersonName)
            val search = text.text
            //検索キーワードでWebAPIコール
            var url = "https://www.googleapis.com/books/v1/volumes?q=$search"

            var resp = this.startGetRequest(url)
            //WebViewに差し込み
            val jsonText = resp
            val adapter = Moshi.Builder().build().adapter(GoogleBooksJson::class.java)
            val booksJson = adapter.fromJson(jsonText)

            booksJson?.items?.forEach{
                try{
                    img += "<img src = '" + it.volumeInfo.imageLinks.thumbnail + "'/>"
                }catch(ex :java.lang.Exception){

                }
            }

            return null
        }

        override fun onProgressUpdate(vararg values: Int?) {
        }

        override fun onPostExecute(result: Void?) {
            val webBr : WebView = findViewById(R.id.webView)
            webBr.loadData(img,"text/html", "utf-8")


        }

        fun startGetRequest(url : String) :String{
            // HttpURLConnectionの作成
            val url = URL(url)
            val connection = url.openConnection() as HttpURLConnection
            var resp :String = ""
            try {
                // ミリ秒単位でタイムアウトを設定
                connection.connectTimeout = 1000000
                connection.readTimeout = 1000000

                // Responseの読み出し
                val statusCode = connection.responseCode
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    resp = readStream(connection.inputStream)
                }
            } catch (exception: Exception) {
                Log.d("レスポンスデータ : ", exception.toString())
                Log.d("レスポンスデータ : ", exception.stackTraceToString())
            } finally {
                connection.disconnect()
            }
            return resp
        }


        private fun readStream(inputStream: InputStream) : String{
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val responseBody = bufferedReader.use { it.readText() }
            bufferedReader.close()
            Log.d("レスポンスデータ : ", responseBody)

            return  responseBody

        }

    }

}
//region JSON Entity
data class GoogleBooksJson(
    val items: List<Item>,
    val kind: String,
    val totalItems: String
)

data class Item(
    val accessInfo: AccessInfo,
    val etag: String,
    val id: String,
    val kind: String,
    val saleInfo: SaleInfo,
    val searchInfo: SearchInfo,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

data class AccessInfo(
    val accessViewStatus: String,
    val country: String,
    val embeddable: Boolean,
    val epub: Epub,
    val pdf: Pdf,
    val publicDomain: Boolean,
    val quoteSharingAllowed: Boolean,
    val textToSpeechPermission: String,
    val viewability: String,
    val webReaderLink: String
)

data class SaleInfo(
    val buyLink: String,
    val country: String,
    val isEbook: Boolean,
    val listPrice: ListPrice,
    val offers: List<Offer>,
    val retailPrice: RetailPriceX,
    val saleability: String
)

data class SearchInfo(
    val textSnippet: String
)

data class VolumeInfo(
    val allowAnonLogging: Boolean,
    val authors: List<String>,
    val canonicalVolumeLink: String,
    val categories: List<String>,
    val contentVersion: String,
    val description: String,
    val imageLinks: ImageLinks,
    val industryIdentifiers: List<IndustryIdentifier>,
    val infoLink: String,
    val language: String,
    val maturityRating: String,
    val pageCount: String,
    val panelizationSummary: PanelizationSummary,
    val previewLink: String,
    val printType: String,
    val publishedDate: String,
    val publisher: String,
    val readingModes: ReadingModes,
    val subtitle: String,
    val title: String
)

data class Epub(
    val acsTokenLink: String,
    val downloadLink: String,
    val isAvailable: Boolean
)

data class Pdf(
    val acsTokenLink: String,
    val downloadLink: String,
    val isAvailable: Boolean
)

data class ListPrice(
    val amount: Int,
    val currencyCode: String
)

data class Offer(
    val finskyOfferType: String,
    val listPrice: ListPriceX,
    val retailPrice: RetailPrice
)

data class RetailPriceX(
    val amount: Int,
    val currencyCode: String
)

data class ListPriceX(
    val amountInMicros: String,
    val currencyCode: String
)

data class RetailPrice(
    val amountInMicros: String,
    val currencyCode: String
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)

data class IndustryIdentifier(
    val identifier: String,
    val type: String
)

data class PanelizationSummary(
    val containsEpubBubbles: Boolean,
    val containsImageBubbles: Boolean
)

data class ReadingModes(
    val image: Boolean,
    val text: Boolean
)
//endregion
