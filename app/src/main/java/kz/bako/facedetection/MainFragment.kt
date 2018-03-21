package kz.bako.facedetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class MainFragment : Fragment() {

    private lateinit var delegate: Delegate

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        delegate = context as Delegate
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            delegate.addFace()
        }

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = Adapter()
        adapter.setData(delegate.faces)
        recyclerView.adapter = adapter
        if (!checkSelfPermission()) ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 123)
        return view
    }

    private fun checkSelfPermission() = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED


    interface Delegate {
        fun addFace()
        val faces: MutableList<Bitmap>
    }


    private inner class Adapter : RecyclerView.Adapter<Adapter.ImageViewHolder>() {
        var images: List<Bitmap> = listOf()

        fun setData(images: List<Bitmap>) {
            this.images = images
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
            return ImageViewHolder(view = view)
        }

        override fun getItemCount(): Int {
            return images.size
        }

        override fun onBindViewHolder(holder: ImageViewHolder?, position: Int) {
            holder?.bind(bitmap = images[position])
        }

        private inner class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            fun bind(bitmap: Bitmap) {
                view.findViewById<ImageView>(R.id.face_image).setImageBitmap(bitmap)
            }
        }
    }
}
