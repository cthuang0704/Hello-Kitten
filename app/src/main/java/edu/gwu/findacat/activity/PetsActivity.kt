package edu.gwu.findacat.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import edu.gwu.findacat.*
import edu.gwu.findacat.petfinder.PetItem
import kotlinx.android.synthetic.main.abc_popup_menu_item_layout.*
import kotlinx.android.synthetic.main.activity_pets.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import java.util.*


class PetsActivity : AppCompatActivity(), PetSearchManager.PetSearchCompletionListener,PetsAdapter.OnItemClickListener, LocationDetector.LocationListener{

    private lateinit var petSearchManager: PetSearchManager
    private lateinit var petsAdapter: PetsAdapter
    private lateinit var persistenceManager: PersistenceManager
    private val TAG = "Load Pet"
    private lateinit var petItems: ArrayList<PetItem>
    private var defaultZipCode = 22201


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pets)

        petSearchManager = PetSearchManager(this@PetsActivity)

        val fusedLocationClient: FusedLocationProviderClient

        //add the toolbar
        setSupportActionBar(pets_toolBar)
        val petsActionBar = supportActionBar
        petsActionBar!!.title = "Nearby Cats"

        //add back button
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        persistenceManager = PersistenceManager(this)

        petItems = intent.getParcelableArrayListExtra(MenuActivity.CAT_DATA)

        populateItemList(petItems)


    }

    override fun petsNotLoaded() {
        Log.d(TAG, "API call failed!")
    }

    override fun petsLoaded(petItems: List<PetItem>) {
        finish()
        val intent = Intent(this@PetsActivity, PetsActivity::class.java)
        intent.putParcelableArrayListExtra(MenuActivity.CAT_DATA, ArrayList(petItems))
        //populateItemList(petItems)
    }

    override fun locationFound(location: Location) {
        val geoCode = Geocoder(this, Locale.getDefault())
        val addressList = geoCode.getFromLocation(location.latitude, location.longitude, 1)

        defaultZipCode = addressList.get(0).postalCode.toString().toInt()
    }

    override fun locationNotFound(reason: LocationDetector.FailureReason) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //add menu with icons
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.pets_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Actions of toolbar
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id =item?.itemId
        // back to the previous activity
        if(id == android.R.id.home){
            this.finish()
        }
        // action of "use zip"
        if(id == R.id.use_zip_setting){
            val layoutInflater = LayoutInflater.from(this)
            val promptZipView = layoutInflater.inflate(R.layout.zip_prompts, null)
            val alertDialogBuilder = AlertDialog.Builder(this)

            //set to alertDialogBuilder
            alertDialogBuilder.setView(promptZipView)

            val userInput: EditText =  promptZipView.findViewById(R.id.edit_zip_text)

            //set dialog message
            alertDialogBuilder.setPositiveButton(R.string.zip_search){dialog, which -> searchZip(userInput.toString().toInt())}
            alertDialogBuilder.setNegativeButton(R.string.zip_not_search){dialog, which ->dialog.cancel() }

            // create alert dialog
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            return true
        }
        return super.onOptionsItemSelected(item)
    }


    fun populateItemList(petItems: ArrayList<PetItem>){
        findcat_view.layoutManager = LinearLayoutManager(this)
        findcat_view.adapter = PetsAdapter(petItems, this)
    }

    override fun onItemClick(petItem: PetItem) {
        val detailIntent = Intent(this, PetDetailsActivity::class.java)
        detailIntent.putExtra("detail", petItem)
        startActivity(detailIntent)
    }

    fun searchZip(zipcode: Int){
        doAsync {
            petSearchManager.petSearchCompletionListener = this@PetsActivity
            petSearchManager.searchPets(zipcode)
        }

    }


}
