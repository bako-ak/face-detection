package kz.bako.facedetection.face.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.vision.face.Face


/**
 * Graphic instance for rendering face position, orientation, and landmarks within an associated
 * graphic overlay view.
 */
class FaceGraphic(overlay: GraphicOverlay) : GraphicOverlay.Graphic(overlay) {

    companion object {
        private val ID_TEXT_SIZE = 40.0f
        private val BOX_STROKE_WIDTH = 5.0f

        private val COLOR_CHOICES = intArrayOf(Color.GREEN, Color.RED)
        private var mCurrentColorIndex = 0
    }


    private val mFacePositionPaint: Paint
    private val mIdPaint: Paint
    private val mBoxPaint: Paint

    @Volatile private var mFace: Face? = null
    private var mFaceId: Int = 0

    init {

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.size
        val selectedColor = COLOR_CHOICES[mCurrentColorIndex]

        mFacePositionPaint = Paint()
        mFacePositionPaint.color = selectedColor

        mIdPaint = Paint()
        mIdPaint.color = selectedColor
        mIdPaint.textSize = ID_TEXT_SIZE

        mBoxPaint = Paint()
        mBoxPaint.color = selectedColor
        mBoxPaint.style = Paint.Style.STROKE
        mBoxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    fun setId(id: Int) {
        mFaceId = id
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    fun updateFace(face: Face) {
        mFace = face
        postInvalidate()
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    override fun draw(canvas: Canvas) {
        val face = mFace ?: return

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.position.x + face.width / 2)
        val y = translateY(face.position.y + face.height / 2)

        // Draws a bounding box around the face.
        val xOffset = scaleX(face.width / 1.8f)
        val yOffset = scaleY(face.height / 1.8f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, mBoxPaint)
    }
}