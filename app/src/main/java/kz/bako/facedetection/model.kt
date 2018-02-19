package kz.bako.facedetection

import android.arch.persistence.room.*

/**
 * Created by baglanserikuly on 19.02.2018.
 */
@Dao
interface ImageDao {
	@Query("SELECT * FROM images")
	fun getImages(): List<Images>

	@Insert
	fun insert(images: Images)

	@Update
	fun update(images: Images)

	@Delete
	fun delete(images: Images)
}


@Database(entities = arrayOf(Images::class), version = 1)
abstract class ImageDatabase : RoomDatabase() {
	abstract val imageDao: ImageDao
}


@Entity(tableName = "images")
data class Images(
		@PrimaryKey(autoGenerate = true)
		var id: Int = 0,
		var url_image: String? = null,
		var type: Int? = null)