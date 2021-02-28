package com.example.pexelsapi

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pexelsapi.adapter.ImageAdapter
import com.example.pexelsapi.retrofit.ApiInterface
import com.example.pexelsapi.retrofit.AppClient
import com.example.pexelsapi.retrofit.response.PhotosItem
import com.example.pexelsapi.retrofit.response.SearchListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val appClient = AppClient.getClient(this@MainActivity)
    private val apiInterface = appClient.create(ApiInterface::class.java)

    var currentPage:Int=1
    val itemCount:Int = 9

    lateinit var searchET:EditText
    lateinit var searchBTN:ImageView
    lateinit var selectedImageFrame:FrameLayout
    lateinit var selectedImageIV:ImageView
    lateinit var closeSelectedView:ImageView
    lateinit var searchedListRV:RecyclerView
    lateinit var pbHeaderProgress:ProgressBar
    lateinit var pageLL:LinearLayout
    lateinit var noItemFound:TextView
    lateinit var pageTXT:TextView
    lateinit var previousIV:ImageView
    lateinit var nextIV:ImageView

    var searchedText: String?= ""

    var photosList: ArrayList<PhotosItem> = ArrayList()
    lateinit var imageAdapter: ImageAdapter

    private fun setUI(){
        searchET = findViewById(R.id.searchET)
        searchBTN = findViewById(R.id.searchBTN)
        selectedImageFrame = findViewById(R.id.selectedImageFrame)
        selectedImageIV = findViewById(R.id.selectedImageIV)
        closeSelectedView = findViewById(R.id.closeSelectedView)
        searchedListRV = findViewById(R.id.searchedListRV)
        pbHeaderProgress = findViewById(R.id.pbHeaderProgress)
        pageLL = findViewById(R.id.pageLL)
        noItemFound = findViewById(R.id.noItemFound)
        pageTXT = findViewById(R.id.pageTXT)
        previousIV = findViewById(R.id.previousIV)
        nextIV = findViewById(R.id.nextIV)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUI()

        searchBTN.setOnClickListener {
            searchedText = searchET.text.toString().trim()
            if (searchedText != ""){
                val view = this.currentFocus
                if (view != null){
                    val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
                setCurrentItem(currentPage)
            }else{
                Toast.makeText(this@MainActivity, "Please enter some text.", Toast.LENGTH_SHORT).show()
                if (!photosList.isNullOrEmpty()){
                    photosList.clear()
                    imageAdapter.notifyDataSetChanged()
                }
            }
        }

        previousIV.setOnClickListener {
            searchedText = searchET.text.toString().trim()
            if (searchedText != ""){
                currentPage = 1
                setCurrentItem(currentPage)
            }else{
                Toast.makeText(this@MainActivity, "Please enter some text.", Toast.LENGTH_SHORT).show()
            }
        }

        nextIV.setOnClickListener {
            searchedText = searchET.text.toString().trim()
            if (searchedText != ""){
                currentPage = 2
                setCurrentItem(currentPage)
            }else{
                Toast.makeText(this@MainActivity, "Please enter some text.", Toast.LENGTH_SHORT).show()
            }
        }

        closeSelectedView.setOnClickListener {
            selectedImageFrame.visibility = View.GONE
            selectedImageIV.setBackgroundResource(0)
        }

    }

    private fun showDialog(){
        pbHeaderProgress.visibility = View.VISIBLE
    }

    private fun hideDialog(){
        pbHeaderProgress.visibility = View.GONE
    }

    private fun setCurrentItem(currentPage:Int=1){
        try {
            showDialog()
            val observable = apiInterface.getSearchList(query=searchedText,per_page=itemCount,page=currentPage)
            observable.enqueue(object: Callback<SearchListResponse>{
                override fun onResponse(
                    call: Call<SearchListResponse>,
                    response: Response<SearchListResponse>
                ) {
                    hideDialog()
                    if (response.isSuccessful){
                        if (!response.body()?.photos.isNullOrEmpty()){
                            photosList.clear()
                            response.body()?.photos?.let { photosList.addAll(it) }
                            imageAdapter = ImageAdapter(
                                this@MainActivity,
                                photosList,
                                object: ImageAdapter.SetOnClickListener{
                                    override fun itemClicked(
                                        position: Int,
                                        photosItem: PhotosItem
                                    ) {
                                            setEnlargedImageView(photosItem)
                                    }

                                }
                            )
                            pageTXT.text = "$currentPage of 2 \n pages"
                            searchedListRV.adapter = imageAdapter
                            listChecker()
                        }
                    }
                }

                override fun onFailure(call: Call<SearchListResponse>, t: Throwable) {
                    hideDialog()
                    t.printStackTrace()
                }

            })
        }catch (e:Exception){
            hideDialog()
            e.printStackTrace()
        }
    }

    private fun setEnlargedImageView(photosItem: PhotosItem) {
        selectedImageFrame.visibility = View.VISIBLE
        selectedImageIV.load(photosItem.src?.large){
            placeholder(R.drawable.ic_launcher_foreground)
        }
    }

    fun listChecker(){
        if (photosList.isNullOrEmpty()){
            searchedListRV.visibility = View.GONE
            noItemFound.visibility = View.VISIBLE
        }else{
            searchedListRV.visibility = View.VISIBLE
            noItemFound.visibility = View.GONE
        }
    }


    fun previousItem(){

    }

    fun nextItem(){

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}