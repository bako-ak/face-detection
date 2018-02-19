package kz.bako.facedetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.list_item.view.*

class MainFragment : Fragment() {

	private lateinit var delegate: Delegate
	private lateinit var adapter: Adapter
	private lateinit var fab: FloatingActionButton
	private var checkImage: Images? = null

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		delegate = context as Delegate
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
	                          savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_main, container, false)
		fab = view.findViewById<FloatingActionButton>(R.id.fab)
		fab.setOnClickListener {
			delegate.addFace()
		}
		val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
		adapter = Adapter()
		recyclerView.adapter = adapter
		if (!checkSelfPermission()) ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 123)
		setHasOptionsMenu(false)
		val async = GetImages(delegate.imageDao) { images ->
			adapter.setData(images)
		}
		async.execute()
		return view
	}

	override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
		inflater?.inflate(R.menu.menu_main, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		return if (item != null && item.itemId == R.id.send_icon) {
			adapter.notifyDataSetChanged()
			fab.visibility = View.GONE
			true
		} else super.onOptionsItemSelected(item)
	}

	private fun checkSelfPermission() = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
			&& ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
			&& ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED


	interface Delegate {
		val imageDao: ImageDao
		fun addFace()
	}


	private inner class Adapter : RecyclerView.Adapter<Adapter.ImageViewHolder>() {
		var images: List<Images> = listOf()

		fun setData(images: List<Images>?) {
			this.images = images ?: listOf()
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
			holder?.bind(images = images[position])
		}

		private inner class ImageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
			val faceImage: ImageView = view.findViewById(R.id.face_image)
			val checkbox: CheckBox = view.findViewById(R.id.checkbox)

			fun bind(images: Images) {
				val bitmap = BitmapFactory.decodeFile(images.url_image)
				faceImage.setImageBitmap(bitmap)
				faceImage.setOnLongClickListener {
					if (checkImage == null) {
						checkImage = images
						checkbox.visibility = View.VISIBLE
						checkbox.isChecked = true
						setHasOptionsMenu(true)
					} else {
						checkbox.isChecked = false
						checkImage = null
						checkbox.checkbox.visibility = View.GONE
						notifyDataSetChanged()
					}

					true
				}
				if (checkImage==null){
					checkbox.isChecked = false
					checkbox.checkbox.visibility = View.GONE
				}
			}
		}
	}

	private inner class GetImages(val imageDao: ImageDao, val callbacks: (List<Images>) -> Unit) : AsyncTask<Unit, Unit, List<Images>>() {
		override fun doInBackground(vararg p0: Unit?): List<Images> {
			return imageDao.getImages()
		}

		override fun onPostExecute(images: List<Images>?) {
			super.onPostExecute(images)
			if (images != null && images.isNotEmpty()) callbacks.invoke(images)
		}

	}
}
