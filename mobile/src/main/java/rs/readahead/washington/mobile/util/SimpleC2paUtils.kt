package rs.readahead.washington.mobile.util

import android.content.Context
import android.location.Location
import android.os.Build
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.PictureFormat
import info.guardianproject.simple_c2pa.ApplicationInfo
import info.guardianproject.simple_c2pa.Certificate
import info.guardianproject.simple_c2pa.ContentCredentials
import info.guardianproject.simple_c2pa.ExifData
import info.guardianproject.simple_c2pa.FileData
import info.guardianproject.simple_c2pa.createContentCredentialsCertificate
import info.guardianproject.simple_c2pa.createRootCertificate
import rs.readahead.washington.mobile.R
import java.io.File
import java.util.UUID

fun Context.getVersionName(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName ?: "Unknown"
    } catch (e: Exception) {
        e.printStackTrace()
        "Unknown"
    }
}

val Context.appInfo: ApplicationInfo
    get() = ApplicationInfo(getString(R.string.app_name), getVersionName(), null)

fun createCCCertificate(): Certificate {
    val rootCert = createRootCertificate("Tella", 10u)
    return createContentCredentialsCertificate(rootCert, "Tella", 10u)
}

object SimpleC2paUtils {
    fun generateC2pa(file: File, context: Context): Boolean {
        return try {
            if (!file.exists()) {
                throw IllegalArgumentException("File does not exist: ${file.absolutePath}")
            }

            val contentCert = createCCCertificate()
            val fileData = FileData(file.absolutePath, null, file.name)
            val cc = ContentCredentials(contentCert, fileData, context.appInfo)
            cc.apply {
                addCreatedAssertion()
                addRestrictedAiTrainingAssertions()
            }
            cc.embedManifest(file.absolutePath)
            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun generateC2pa(videoResult: VideoResult, context: Context): File? {
        val location = videoResult.location
        val exifData = getExifFromLocation(location)
        val file = videoResult.file
        val contentCert = createCCCertificate()
        val fileData = FileData(file.absolutePath, null, file.name)
        val cc = ContentCredentials(contentCert, fileData, context.appInfo)
        cc.apply {
            addCreatedAssertion()
            addRestrictedAiTrainingAssertions()
            addExifAssertion(exifData)
        }
        cc.embedManifest(file.absolutePath)
        return file
    }


    fun generateC2pa(byteArray: ByteArray, context: Context): Boolean {
        return try {
            val contentCert = createCCCertificate()
            val fileData = FileData(bytes = byteArray, fileName = null, path = null)
            val cc = ContentCredentials(contentCert, fileData, context.appInfo)
            cc.apply {
                addCreatedAssertion()
                addRestrictedAiTrainingAssertions()
            }
            true

        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Given a picture result, attempt to embed c2pa into the file by converting the data to file,embedding the c2pa,
     * and returning the bytes
     * @param pictureResult the picture result
     * @param context the context
     */

    fun generateC2pa(
        pictureResult: PictureResult,
        context: Context
    ): ByteArray? {
        val location = pictureResult.location
        val exifData = getExifFromLocation(location)
        val name = UUID.randomUUID().toString()
        val extension = when (pictureResult.format) {
            PictureFormat.JPEG -> "jpg"
            PictureFormat.DNG -> "dng"
            else -> "jpg"
        }
        var bytes: ByteArray? = pictureResult.data
        try {
            val file = File(context.cacheDir, "$name.$extension")
            pictureResult.toFile(file) { fileResult ->
                if (fileResult != null) {
                    val fileData = FileData(fileResult.absolutePath, null, fileResult.name)
                    val contentCert = createCCCertificate()
                    val cc = ContentCredentials(contentCert, fileData, context.appInfo)
                    cc.apply {
                        addCreatedAssertion()
                        addRestrictedAiTrainingAssertions()
                        addExifAssertion(exifData)
                    }
                    // Embed the C2PA
                    cc.embedManifest(fileResult.absolutePath)
                    // Read the file back in to get the bytes
                    bytes = fileResult.readBytes()
                }
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
            bytes = pictureResult.data
        }
        return bytes
    }

    fun getExifFromLocation(location: Location?): ExifData {
        val lat: Double? = location?.latitude
        val lon: Double? = location?.longitude
        val altitude: Double? = location?.altitude
        val bearing = location?.bearing
        val speed = location?.speed
        val timestamp = location?.time
        return ExifData(
            latitude = lat?.toString(),
            longitude = lon?.toString(),
            altitude = altitude?.toString(),
            speed = speed?.toString(),
            timestamp = timestamp?.toString(),
            altitudeRef = null,
            lensMake = null,
            model = Build.MODEL,
            gpsVersionId = null,
            destinationBearingRef = null,
            destinationBearing = bearing?.toString(),
            speedRef = null,
            colorSpace = null,
            exposureTime = null,
            fNumber = null,
            direction = null,
            lensModel = null,
            make = Build.MANUFACTURER,
            positioningError = null,
            lensSpecification = null,
            directionRef = null,
            digitalZoomRatio = null
        )
    }


}