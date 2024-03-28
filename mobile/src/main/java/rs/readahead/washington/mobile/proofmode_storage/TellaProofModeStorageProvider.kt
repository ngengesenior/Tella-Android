package rs.readahead.washington.mobile.proofmode_storage

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import org.witness.proofmode.ProofMode
import org.witness.proofmode.storage.StorageListener
import org.witness.proofmode.storage.StorageProvider
import rs.readahead.washington.mobile.MyApplication
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import java.util.ArrayList

class TellaProofModeStorageProvider(private val context: Context):StorageProvider {
    private val BASE_PROOF_FOLDER = "proofmode/"
    override fun saveStream(hash: String?, identifier: String?, inputStream: InputStream?, storageListener: StorageListener?) {

    }

    override fun saveBytes(hash: String?, identifier: String?, byteArray: ByteArray?, storageListener: StorageListener?) {

    }

    override fun saveText(hash: String?, identifier: String?, data: String?, storageListener: StorageListener?) {
        val file = File(getHashStorageDir(hash!!),identifier!!)
        data?.let {
            writeTextToFile(file,data)
        }
    }

    @Throws(FileNotFoundException::class,SecurityException::class)
    override fun getInputStream(hash: String?, identifier: String?): InputStream = FileInputStream(File(getHashStorageDir(hash!!),identifier!!))


    override fun getOutputStream(hash: String?, identifier: String?): OutputStream = FileOutputStream(File(getHashStorageDir(hash!!),identifier!!))

    override fun proofExists(hash: String?): Boolean {
        return proofIdentifierExists(hash, hash+ProofMode.PROOF_FILE_TAG)
    }

    override fun proofIdentifierExists(hash: String?, identifier: String?): Boolean {
        val dirProof = hash?.let { getHashStorageDir(it)}
        if (dirProof?.exists() == true) {
            return (identifier?.let { File(dirProof,it).exists()} == true)
        }
        return false
    }

    override fun getProofSet(hash: String?): ArrayList<Uri> {
        val libProofSet = arrayListOf<Uri>()
        val dirProof = hash?.let { getHashStorageDir(it) }
        if (dirProof?.exists() == true){
            dirProof.listFiles()?.forEach {
                libProofSet.add(Uri.fromFile(it))
            }
        }
        return libProofSet
    }

    override fun getProofItem(uri: Uri?): InputStream? {
        return if (uri?.scheme.equals("file")){
            FileInputStream(uri?.toFile())
        } else null
    }

    private fun getHashStorageDir(hash:String):File? {

        var proofFileSystem:File? = ProofMode.getProofFileSystem()
        if (proofFileSystem == null) {
            ProofMode.setProofFileSystem(MyApplication.vault.config.root)
            proofFileSystem = ProofMode.getProofFileSystem()

        }
        val parentDir = File(proofFileSystem,BASE_PROOF_FOLDER)
        if (!parentDir.exists()) {
            parentDir.mkdir()
        }
        val fileHashDir = File(parentDir,"$hash/")
        if (!fileHashDir.exists()) if (!fileHashDir.mkdir()) return null
        return fileHashDir
    }

    private fun writeTextToFile(fileOut:File,text:String) {
        try {
            val ps = PrintStream(FileOutputStream(fileOut,true))
            ps.apply {
                println(text)
                flush()
                close()
            }

        }catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }
    @Throws(FileNotFoundException::class,SecurityException::class)
    private fun writeBytesToFile(fileOut:File,data:ByteArray) {
        FileOutputStream(fileOut).write(data)
    }

    @Throws(FileNotFoundException::class,SecurityException::class)
    fun copyStreamToFile(inputStream: InputStream,fileOut:File) {
       // ProofMode.checkAndGeneratePublicKeyAsync()
        val outputStream = FileOutputStream(fileOut)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }
}