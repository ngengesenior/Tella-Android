package rs.readahead.washington.mobile.proofmode.storage

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.net.toFile
import com.hzontal.tella_vault.VaultException
import com.hzontal.tella_vault.VaultFile
import org.apache.commons.io.IOUtils
import org.witness.proofmode.ProofMode
import org.witness.proofmode.storage.StorageListener
import org.witness.proofmode.storage.StorageProvider
import rs.readahead.washington.mobile.MyApplication
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.Locale

class TellaProofModeStorageProvider(private val context: Context):StorageProvider {


    override fun saveStream(hash: String?, identifier: String?, inputStream: InputStream?, storageListener: StorageListener?) {
        // The hash is the parent and the identifier is the file id
        val theId = identifier
        val theHash = hash
        var vaultFile =
        MyApplication
            .vault
            .builder(inputStream)
            .setId(identifier)
            // We need to set mime type
            .build(hash)

        storageListener?.saveSuccessful(vaultFile.id)

    }

    override fun saveBytes(hash: String?, identifier: String?, byteArray: ByteArray?, storageListener: StorageListener?) {
        val bis = ByteArrayInputStream(byteArray)
        saveStream(hash,identifier,bis,storageListener)

    }

    override fun saveText(hash: String?, identifier: String?, data: String?, storageListener: StorageListener?) {
        val inputStream = IOUtils.toInputStream(data, Charset.defaultCharset())
        saveStream(hash,identifier,inputStream,storageListener)
    }


    override fun getInputStream(hash: String?, identifier: String?): InputStream? {

        try
        {
            val vaultFile = getVaultFile(hash, identifier)
            return MyApplication.vault.getStream(vaultFile)
        }
        catch (_: VaultException)
        {
            return null
        }

    }


    override fun getOutputStream(hash: String?, identifier: String?): OutputStream? {
        try
        {
            val vaultFile = MyApplication.vault.builder(identifier)
                .build(hash)
           return MyApplication.vault.getOutStream(vaultFile)
        }
        catch (_: VaultException)
        {
            return null
        }
    }

    override fun proofExists(hash: String?): Boolean {
        return proofIdentifierExists(hash, hash+ProofMode.PROOF_FILE_TAG)
    }

    @Throws(Exception::class)
    override fun proofIdentifierExists(hash: String?, identifier: String?): Boolean {
        val theHash = hash?.lowercase()
        val id = identifier?.lowercase()

        val vaultFile = getVaultFile(theHash, id)
        val fileActual = MyApplication.vault.getFile(vaultFile)
        return fileActual.exists()

    }

    private fun getVaultFile (hash: String?, identifier: String?) : VaultFile {
        val vaultFile = VaultFile(hash, identifier)

        vaultFile.mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(identifier);
        if (vaultFile.mimeType.isNullOrEmpty())
            vaultFile.mimeType = "application/octet-stream"

        return vaultFile
    }

    private fun getProofVaultFiles(hash: String?): List<VaultFile> {
        var theHash = hash
        //MyApplication.rxVault.list()
        return MyApplication.vault?.list(VaultFile(hash)) ?: emptyList()
    }
    override fun getProofSet(hash: String?): ArrayList<Uri> {
        val libProofSet = arrayListOf<Uri>()
        getProofVaultFiles(hash)
            .forEach {
                libProofSet.add(Uri.parse(it.path))
            }

        return libProofSet
    }

    override fun getProofItem(uri: Uri?): InputStream? {
        return if (uri?.scheme.equals("file")){
            FileInputStream(uri?.toFile())
        } else null
    }



}