package edu.gwu.findacat.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Contacts.SettingsColumns.KEY
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import edu.gwu.findacat.CatFactManager
import edu.gwu.findacat.LocationDetector
import edu.gwu.findacat.LocationDetector.LocationListener
import edu.gwu.findacat.PetSearchManager
import edu.gwu.findacat.R
import edu.gwu.findacat.petfinder.PetItem
import kotlinx.android.synthetic.main.activity_menu.*
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList

class MenuActivity : AppCompatActivity(),CatFactManager.CatFactCompletionListener,
        LocationDetector.LocationListener, PetSearchManager.PetSearchCompletionListener{

    private val TAG = "MenuActivity"
    private val LOCATION_PERMISSION_REQUEST_CODE = 7
    private lateinit var locationDetector :LocationDetector
    private lateinit var catFactManager: CatFactManager
    internal lateinit var findACat_Btn: Button
    internal lateinit var favoriteCats_Btn: Button
    private lateinit var petSearchManager: PetSearchManager
    private var defaultZipCode = 22201

    companion object {
        val CAT_DATA = "cataData"
        val TITLE_KEY = "title"
    }

    //onCreate: the entry point of the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        find_a_cat_btn.setOnClickListener{

            loadPetsData()
        }

        catFactManager = CatFactManager(this, catsfact_textView)
        catFactManager.catFactCompletionListener = this
        catFactManager.searchStrings()

        requestPermissionsIfNecessary()

    }

    //load cat fact text
    override fun catFactLoaded(fact: String) {
        catsfact_textView.text = fact

    }

    override fun catFactNotLoaded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun loadPetsData() {
        doAsync {
            activityUiThread {

                locationDetector = LocationDetector(this@MenuActivity)
                locationDetector.locationListener = this@MenuActivity
                locationDetector.detectLocation()

            }
        }
    }

    override fun locationFound(location: Location) {
        val geocode = Geocoder(this, Locale.getDefault())
        val addressList = geocode.getFromLocation(location.latitude, location.longitude, 1)
        defaultZipCode = addressList.get(0).postalCode.toString().toInt()

        petSearchManager = PetSearchManager(this@MenuActivity)
        petSearchManager.searchPets(defaultZipCode)
        petSearchManager.petSearchCompletionListener = this@MenuActivity

    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        petSearchManager = PetSearchManager(this@MenuActivity)
        petSearchManager.searchPets(defaultZipCode)
        petSearchManager.petSearchCompletionListener = this@MenuActivity


    }

    //send intent to load nearby cats
    override fun petsLoaded(petItems: List<PetItem>) {
        toast("cat loaded")
        val intent = Intent(this@MenuActivity, PetsActivity::class.java)
        intent.putParcelableArrayListExtra(CAT_DATA, ArrayList(petItems))
        startActivity(intent)
    }

    override fun petsNotLoaded() {
        toast("cat not loaded")
        Log.d(TAG,"cats not loaded")
    }


    //get location permission
    private fun requestPermissionsIfNecessary() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                toast(R.string.permissions_granted)
            }
            else {
                toast(R.string.permissions_denied)
            }
        }
    }



}
