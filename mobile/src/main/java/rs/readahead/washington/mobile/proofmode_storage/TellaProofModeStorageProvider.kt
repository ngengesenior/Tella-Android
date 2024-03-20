package rs.readahead.washington.mobile.proofmode_storage

import android.net.Uri
import org.witness.proofmode.storage.StorageListener
import org.witness.proofmode.storage.StorageProvider
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList

class TellaProofModeStorageProvider:StorageProvider {
    override fun saveStream(hash: String?, identifier: String?, inputStream: InputStream?, storageListener: StorageListener?) {
        TODO("Not yet implemented")
    }

    override fun saveBytes(hash: String?, identifier: String?, byteArray: ByteArray?, storageListener: StorageListener?) {
        TODO("Not yet implemented")
    }

    override fun saveText(hash: String?, identifier: String?, text: String?, storageListener: StorageListener?) {
        TODO("Not yet implemented")
    }

    override fun getInputStream(hash: String?, identifier: String?): InputStream {
        TODO("Not yet implemented")
    }

    override fun getOutputStream(hash: String?, identifier: String?): OutputStream {
        TODO("Not yet implemented")
    }

    override fun proofExists(hash: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun proofIdentifierExists(hash: String?, identifier: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getProofSet(hash: String?): ArrayList<Uri> {
        TODO("Not yet implemented")
    }

    override fun getProofItem(hash: Uri?): InputStream {
        TODO("Not yet implemented")
    }
}