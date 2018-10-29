package edu.gwu.findacat

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import edu.gwu.findacat.activity.PetDetailsActivity
import edu.gwu.findacat.activity.PetsActivity
import edu.gwu.findacat.petfinder.PetItem
import edu.gwu.findacat.petfinder.StringWrapper
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.activityUiThread
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity

class PetsAdapter(val petContent: ArrayList<PetItem>, private val clickListener: OnItemClickListener): RecyclerView.Adapter<PetsAdapter.ViewHolder>(){
    companion object {
        val NAME_DATA_KEY = "nameData"
        val IMAGE_NAME_DATA_KEY = "imageNameData"
        val PHOTO_DATA_KEY = "photoData"
        val GENDER_DATA_KEY = "genderData"
        val BREED_DATA_KEY = "breedData"
        val ZIP_DATA_KEY = "zipData"
        val DESCRIPTION_DATA_KEY ="descriptionData"
        val ID_DATA_KEY = "idData"
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup?.context)
        val cellforPets = layoutInflater.inflate(R.layout.content_pets, viewGroup, false)
        return ViewHolder(cellforPets)
    }

    override fun getItemCount(): Int {
        return petContent.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val petFinderItem = petContent.get(position)
        viewHolder.bind(petFinderItem, clickListener)

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contentImageView : ImageView = view.findViewById(R.id.pets_image)
        val contentTextView : TextView = view.findViewById(R.id.pets_textview)

        fun bind(petItem: PetItem, listener: OnItemClickListener) = with(itemView){
            val imageUrl = petItem.media?.photos?.photo?.get(0)?.t
            val catName = petItem.name.t

            contentTextView.text = catName
            Picasso.get().load(imageUrl).into(contentImageView)

            setOnClickListener(){
                listener.onItemClick(petItem)
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(petItem: PetItem)
    }

}




