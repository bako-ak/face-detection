package kz.bako.facedetection.face


import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kz.bako.facedetection.databinding.FragmentFaceBinding
import kz.bako.facedetection.face.ui.FaceGraphic
import kz.bako.facedetection.face.ui.GraphicOverlay
import java.io.IOException

class FaceFragment : Fragment(), CameraSource.PictureCallback {

	companion object {
		const val ARGS_CAMERA = "camera"
		fun create(camera: Int) = FaceFragment().apply {
			arguments = Bundle().apply {
				putInt(ARGS_CAMERA, camera)
			}
		}

	}

	private lateinit var binding: FragmentFaceBinding
	private lateinit var delegate: Delegat
	private var safeToTakePicture = true
	private var cameraSource: CameraSource? = null


	override fun onAttach(context: Context?) {
		super.onAttach(context)
		delegate = context as Delegat
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		binding = FragmentFaceBinding.inflate(inflater, container, false)

		val camera = arguments.getInt(ARGS_CAMERA, 1)

		binding.takePhoto.setOnClickListener {
			if (safeToTakePicture) {
				cameraSource?.takePicture(null, this@FaceFragment)
				safeToTakePicture = false
			}
		}

		binding.changeCamera.setOnClickListener { delegate.changeCamera(camera) }
		val detector = FaceDetector.Builder(context)
				.setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
				.setProminentFaceOnly(true)
				.setLandmarkType(FaceDetector.ALL_LANDMARKS)
				.build()
		detector.setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory()).build())
		if (!detector.isOperational) {
			return null
		}
		cameraSource = CameraSource.Builder(context, detector)
				.setRequestedPreviewSize(640, 480)
				.setFacing(camera)
				.setRequestedFps(30.0f)
				.setAutoFocusEnabled(true)
				.build()
		return binding.root
	}

	override fun onResume() {
		super.onResume()
		try {
			cameraSource?.let { cameraSource ->
				binding.preview.start(cameraSource, binding.faceOverlay)
			}
		} catch (e: IOException) {
			cameraSource?.release()
			cameraSource = null
		}
	}

	override fun onPause() {
		super.onPause()
		binding.preview.stop()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		cameraSource?.release()
	}

	override fun onPictureTaken(data: ByteArray) {
		cameraSource?.stop()
		delegate.completedRecognition(getImage(context, data))
	}


	private inner class GraphicFaceTrackerFactory : MultiProcessor.Factory<Face> {
		override fun create(face: Face): Tracker<Face> {
			return GraphicFaceTracker(binding.faceOverlay)
		}
	}

	private inner class GraphicFaceTracker(private val mOverlay: GraphicOverlay) : Tracker<Face>() {
		private val mFaceGraphic: FaceGraphic = FaceGraphic(mOverlay)


		override fun onNewItem(faceId: Int, item: Face?) {
			mFaceGraphic.setId(faceId)
		}

		override fun onUpdate(detectionResults: Detector.Detections<Face>?, face: Face) {
			mOverlay.add(mFaceGraphic)
			mFaceGraphic.updateFace(face)
		}

		override fun onMissing(detectionResults: Detector.Detections<Face>) {
			mOverlay.remove(mFaceGraphic)
		}

		override fun onDone() {
			mOverlay.remove(mFaceGraphic)
		}
	}

	interface Delegat {
		fun completedRecognition(faces: Bitmap?)

		fun changeCamera(camera: Int)
	}

}
