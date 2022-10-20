package com.cory.dailyquotes.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cory.dailyquotes.DB.PeopleDBHelper
import com.cory.dailyquotes.DB.QuotesDBHelper
import com.cory.dailyquotes.R
import com.cory.dailyquotes.adapters.FetchQuotesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.util.*

class FetchQuotesFragment : Fragment() {

    val client = OkHttpClient()

    private lateinit var floatingActionButton: ExtendedFloatingActionButton

    private val dataList = ArrayList<HashMap<String, String>>()
    val filteredDataList = ArrayList<HashMap<String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fetch_quotes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val materialToolBarFetchedPeople = requireView().findViewById<MaterialToolbar>(R.id.materialToolBarFetchedPeople)
        materialToolBarFetchedPeople.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        if (isOnline(requireContext())) {
            run("https://zenquotes.io/api/quotes/")
        }
        else {
            Toast.makeText(requireContext(), "Check data connection", Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }

        var quotesNotAdded = 0

        floatingActionButton = requireView().findViewById<ExtendedFloatingActionButton>(R.id.addAllFAB)
        floatingActionButton.setOnClickListener {
            quotesNotAdded = 0
            for (i in 0 until filteredDataList.count()) {
                if (PeopleDBHelper(requireContext(), null).getCount() > 0) {
                    if (PeopleDBHelper(
                            requireContext(),
                            null
                        ).checkIfExists(filteredDataList[i]["a"].toString())
                    ) {
                        if (QuotesDBHelper(requireContext(), null).checkIfExistsForASpecificPerson(
                                filteredDataList[i]["q"].toString().replace("'", "''"),
                                PeopleDBHelper(
                                    requireContext(),
                                    null
                                ).getPersonID(filteredDataList[i]["a"].toString())
                            )
                        ) {
                            quotesNotAdded++
                        }
                    } else {
                        if (PeopleDBHelper(
                                requireContext(),
                                null
                            ).checkIfExists(filteredDataList[i]["a"].toString())
                        ) {
                            QuotesDBHelper(requireContext(), null).insertRow(
                                PeopleDBHelper(
                                    requireContext(),
                                    null
                                ).getPersonID(filteredDataList[i]["a"].toString()),
                                filteredDataList[i]["q"].toString(),
                                Date().toString()
                            )
                        } else {
                            GlobalScope.launch(Dispatchers.IO) {
                                try {
                                    val url = URL(filteredDataList[i]["i"].toString())
                                    val con = url.openConnection()
                                    con.doInput = true
                                    con.connect()

                                    val input = con.getInputStream()
                                    val bitmap = BitmapFactory.decodeStream(input)
                                    input.close()
                                    val stream = ByteArrayOutputStream()
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                    val image2 = stream.toByteArray()
                                    PeopleDBHelper(requireContext(), null).insertRow(
                                        filteredDataList[i]["a"].toString(),
                                        "",
                                        image2,
                                        "internet"
                                    )
                                    QuotesDBHelper(
                                        requireContext(),
                                        null
                                    ).insertRow(
                                        PeopleDBHelper(
                                            requireContext(),
                                            null
                                        ).getPersonID(filteredDataList[i]["a"].toString()),
                                        filteredDataList[i]["q"].toString(),
                                        Date().toString()
                                    )
                                } catch (e: FileNotFoundException) {
                                    PeopleDBHelper(requireContext(), null).insertRow(
                                        filteredDataList[i]["a"].toString(),
                                        "",
                                        ByteArray(0),
                                        "internet"
                                    )
                                    QuotesDBHelper(
                                        requireContext(),
                                        null
                                    ).insertRow(
                                        PeopleDBHelper(
                                            requireContext(),
                                            null
                                        ).getPersonID(filteredDataList[i]["a"].toString()),
                                        filteredDataList[i]["q"].toString(),
                                        Date().toString()
                                    )
                                }
                            }
                        }
                    }
                }
                else {
                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val url = URL(filteredDataList[i]["i"].toString())
                            val con = url.openConnection()
                            con.doInput = true
                            con.connect()

                            val input = con.getInputStream()
                            val bitmap = BitmapFactory.decodeStream(input)
                            input.close()
                            val stream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                            val image2 = stream.toByteArray()
                            PeopleDBHelper(requireContext(), null).insertRow(
                                filteredDataList[i]["a"].toString(),
                                "",
                                image2,
                                "internet"
                            )
                            QuotesDBHelper(
                                requireContext(),
                                null
                            ).insertRow(
                                PeopleDBHelper(
                                    requireContext(),
                                    null
                                ).getPersonID(filteredDataList[i]["a"].toString()),
                                filteredDataList[i]["q"].toString(),
                                Date().toString()
                            )
                        } catch (e: FileNotFoundException) {
                            PeopleDBHelper(requireContext(), null).insertRow(
                                filteredDataList[i]["a"].toString(),
                                "",
                                ByteArray(0),
                                "internet"
                            )
                            QuotesDBHelper(
                                requireContext(),
                                null
                            ).insertRow(
                                PeopleDBHelper(
                                    requireContext(),
                                    null
                                ).getPersonID(filteredDataList[i]["a"].toString()),
                                filteredDataList[i]["q"].toString(),
                                Date().toString()
                            )
                        }
                    }
                }
            }
            if (quotesNotAdded > 0) {
                Toast.makeText(
                    requireContext(),
                    "$quotesNotAdded quotes already exists",
                    Toast.LENGTH_SHORT
                ).show()
            }
            floatingActionButton.hide()
        }
    }

    fun run(url: String) {

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(requireContext(), "Some error occurred while fetching quotes", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()!!.string()

                val jsonContact = JSONArray(body)

                val size = jsonContact.length()

                for (i in 0 until size) {
                    val jsonObjectDetail: JSONObject = jsonContact.getJSONObject(i)

                    if (jsonObjectDetail.get("q").toString().length <= 50) {
                        val arrayListDetails = HashMap<String, String>()
                        arrayListDetails["q"] = jsonObjectDetail.get("q").toString()
                        arrayListDetails["a"] = (jsonObjectDetail.get("a").toString())
                        arrayListDetails["i"] = "https://zenquotes.io/img/${arrayListDetails["a"]!!.replace(",", "").replace("-", "--").replace(" ", "-").replace(".", "_").lowercase()}.jpg"
                        dataList.add(arrayListDetails)

                    }
                }
                GlobalScope.launch(Dispatchers.Main) {

                    for (i in 0 until dataList.count()) {
                        if (!flatContains(filteredDataList, dataList[i]["a"])) {
                            filteredDataList.add(dataList[i])
                        }
                    }

                    val recyclerView = requireView().findViewById<RecyclerView>(R.id.fetchedPeopleRecyclerView)
                    recyclerView.adapter = FetchQuotesAdapter(requireContext(), filteredDataList)
                    recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
                    floatingActionButton.show()
                    requireView().findViewById<ProgressBar>(R.id.fetchedPeopleProgressBar).visibility = View.GONE
                }
            }
        })
    }

    fun flatContains(
        collections: ArrayList<HashMap<String, String>>,
        value: Any?
    ): Boolean {
        for (collection in collections) {
            if (collection.contains(value)) {
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
}