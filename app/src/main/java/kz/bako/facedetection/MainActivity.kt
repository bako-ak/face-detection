package kz.bako.facedetection

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import kz.bako.facedetection.face.FaceFragment
import java.util.*

class MainActivity : AppCompatActivity(), MainFragment.Delegate, FaceFragment.Delegat {
	override val faces: MutableList<Bitmap> = arrayListOf()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		pushFragment(MainFragment())
	}


	override fun addFace() {
		pushFragment(FaceFragment())
	}


	override fun completedRecognition(result: Bitmap?) {
		if (result != null) faces.add(result)
		supportFragmentManager.popBackStackImmediate()
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
}

