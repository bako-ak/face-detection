package kz.bako.facedetection

import android.arch.persistence.room.Room
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kz.bako.facedetection.face.FaceFragment
import kz.bako.facedetection.face.createFile
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity(), MainFragment.Delegate, FaceFragment.Delegat {

	private val messageDb get() = Room.databaseBuilder(this, ImageDatabase::class.java, "ImageDatabase").build()
	override val imageDao = messageDb.imageDao

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		if (savedInstanceState == null)
			pushFragment(MainFragment())
	}


	override fun addFace() {
		pushFragment(FaceFragment.create(1))
	}


	override fun completedRecognition(result: Bitmap?) {
		if (result != null) {
			SaveTask(result).execute()
			supportFragmentManager.popBackStackImmediate()
		}
	}

	override fun changeCamera(camera: Int) {
		supportFragmentManager.popBackStackImmediate()
		pushFragment(FaceFragment.create(if (camera == 0) 1 else 0))
	}


	private fun pushFragment(fragment: Fragment) {
		val name = UUID.randomUUID().toString()
		supportFragmentManager
				.beginTransaction()
				.addToBackStack(name)
				.replace(R.id.content_container, fragment, name)
				.commit()

	}

	override fun onBackPressed() {
		if (supportFragmentManager.backStackEntryCount <= 1) {
			finish()
			return
		}
		super.onBackPressed()
	}

	private inner class SaveTask(val bitmap: Bitmap) : AsyncTask<Unit, Unit, Unit>() {

		override fun doInBackground(vararg p0: Unit?) {
			val file = createFile(this@MainActivity)
			val fos = FileOutputStream(file)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
			fos.flush()
			fos.close()
			imageDao.insert(images = Images(url_image = file.path))
		}

		override fun onPostExecute(result: Unit?) {
			super.onPostExecute(result)
			pushFragment(MainFragment())
		}
	}
}

