package edu.gwu.findacat.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.squareup.picasso.Picasso
import edu.gwu.findacat.PetSearchManager
import edu.gwu.findacat.PetsAdapter
import edu.gwu.findacat.R
import edu.gwu.findacat.petfinder.PetItem
import kotlinx.android.synthetic.main.abc_activity_chooser_view.view.*
import kotlinx.android.synthetic.main.activity_pet_details.*
import kotlinx.android.synthetic.main.activity_pet_details.view.*
import org.jetbrains.anko.startActivity

class PetDetailsActivity() : AppCompatActivity(),PetSearchManager.PetSearchCompletionListener{

    private var petItem: PetItem ?= null
    lateinit var favoriteList: List<PetItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pet_details)

        petItem = intent.getParcelableExtra("detail")

        //add the toolbar
        setSupportActionBar(details_toolBar)
        val detailActionBar = supportActionBar
        detailActionBar!!.title = "Cat Details"

        //add back button
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()?.setDisplayShowHomeEnabled(true);

        populateDetails(petItem!!)

    }

    //add menu with icons
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.details_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Actions of toolbar
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id =item?.itemId
        val shareName = petItem?.name?.t
        val shareEmail = petItem?.contact?.email?.t
        val sharePhoto = petItem?.media?.photos?.photo?.get(0)?.t
        if(id == android.R.id.home){
            this.finish()
        }

        if(id == R.id.favorite_icon){

            Toast.makeText(this,"Add to favorites", Toast.LENGTH_SHORT).show()
            return true
        }
        if(id == R.id.mail_icon){
            val sendIntent = Intent()
            val sendMessage = getString(R.string.send_message, shareName)
            val contact = arrayOf(shareEmail)
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_EMAIL, contact)
            sendIntent.putExtra(Intent.EXTRA_TEXT, sendMessage)
            sendIntent.type = "text/plain"
            startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send)))
            return true
        }
        if(id == R.id.share_icon){
            val shareIntent = Intent()
            val shareMessage = getString(R.string.share_message, shareName, shareEmail, sharePhoto)
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share)))
            return true
        }

        return super.onOptionsItemSelected(item)
    }



    override fun petsLoaded(petItems: List<PetItem>) {
        TODO("not implemented")
    }

    override fun petsNotLoaded() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    fun populateDetails(petItem: PetItem){
        val url = petItem.media.photos?.photo?.get(0)?.t

        //assign context with PetItem
        Picasso.get().load(url).into(detail_imageView)
        detail_name_textView.text = petItem.name.t
        name_textView.text = getString(R.string.detail_name_textview, petItem.name.t)
        gender_textView.text = getString(R.string.detail_gender_textview,petItem.sex.t)
        breed_textView.text = getString(R.string.detail_breed_textview, petItem.breeds.breed[0].t)
        zip_textView.text = getString(R.string.detail_zip_textview, petItem.contact.zip.t)
        description_textView.text = getString(R.string.detail_description_textview, petItem.description?.t)

    }



}
