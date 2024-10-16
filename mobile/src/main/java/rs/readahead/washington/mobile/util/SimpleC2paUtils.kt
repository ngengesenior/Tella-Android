package rs.readahead.washington.mobile.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.io.File
import info.guardianproject.simple_c2pa.*
import rs.readahead.washington.mobile.R

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
     get() = ApplicationInfo(getString(R.string.app_name), getVersionName(),null)
object SimpleC2paUtils {
     fun generateC2pa(file: File,context: Context): Boolean {
          return try {
               if (!file.exists()) {
                    throw IllegalArgumentException("File does not exist: ${file.absolutePath}")
               }
               val rootCert = createRootCertificate(null, null)
               val contentCert = createContentCredentialsCertificate(rootCert, null, null)
               val fileData = FileData(file.absolutePath, null, file.name)
               val cc = ContentCredentials(contentCert, fileData, context.appInfo)
               cc.apply {
                    addCreatedAssertion()
                    addRestrictedAiTrainingAssertions()
                    addPlacedAssertion()
               }
               true

          }catch (e:Exception){
               e.printStackTrace()
               false
          }
     }


     fun generateC2pa(byteArray: ByteArray, context: Context): Boolean {
          return try {
               val rootCert = createRootCertificate(null, null)
               val contentCert = createContentCredentialsCertificate(rootCert, null, null)
               val fileData = FileData(bytes = byteArray, fileName = null, path = null)
               val cc = ContentCredentials(contentCert, fileData, context.appInfo)
               cc.apply {
                    addCreatedAssertion()
                    addRestrictedAiTrainingAssertions()
                    addPlacedAssertion()
               }
               true

          }catch (e: Exception){
               e.printStackTrace()
               false
          }
     }


}