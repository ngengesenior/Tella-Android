package rs.readahead.washington.mobile.proofmode.storage

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import com.hzontal.tella_vault.VaultFile
import org.apache.commons.io.IOUtils
import org.witness.proofmode.ProofMode
import org.witness.proofmode.storage.StorageListener
import org.witness.proofmode.storage.StorageProvider
import rs.readahead.washington.mobile.MyApplication
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset

class TellaProofModeStorageProvider(private val context: Context):StorageProvider {
    override fun saveStream(hash: String?, identifier: String?, inputStream: InputStream?, storageListener: StorageListener?) {
        // The hash is the parent and the identifier is the file id
        MyApplication.vault.builder(inputStream)
            .setId(identifier)
            // We need to set mime type
            .build(hash)


    }

    override fun saveBytes(hash: String?, identifier: String?, byteArray: ByteArray?, storageListener: StorageListener?) {
        val bis = ByteArrayInputStream(byteArray)
        saveStream(hash,identifier,bis,storageListener)

    }

    override fun saveText(hash: String?, identifier: String?, data: String?, storageListener: StorageListener?) {
        val inputStream = IOUtils.toInputStream(data, Charset.defaultCharset())
        saveStream(hash,identifier,inputStream,storageListener)
    }

    @Throws(FileNotFoundException::class,SecurityException::class)
    override fun getInputStream(hash: String?, identifier: String?): InputStream? {
        val vaultFile = MyApplication.vault.builder(identifier)
            .build(hash)
        return MyApplication.vault.getStream(vaultFile)
        //return MyApplication.rxVault.getStream(vaultFile)
    }


    override fun getOutputStream(hash: String?, identifier: String?): OutputStream {
        val vaultFile = MyApplication.vault.builder(identifier)
            .build(hash)
       return MyApplication.vault.getOutStream(vaultFile)
    }

    override fun proofExists(hash: String?): Boolean {
        return proofIdentifierExists(hash, hash+ProofMode.PROOF_FILE_TAG)
    }

    @Throws(Exception::class)
    override fun proofIdentifierExists(hash: String?, identifier: String?): Boolean {
        val rootVault:VaultFile = getParentVaultFile(hash) ?: return false
        return MyApplication.vault.list(rootVault)
            .any {
                it.id == identifier
            }

    }

    private fun getProofVaultFiles(hash: String?): List<VaultFile> {
        val rootVault: VaultFile = getParentVaultFile(hash) ?: return emptyList()
        return MyApplication.vault?.list(rootVault) ?: emptyList()
    }

    override fun getProofSet(hash: String?): ArrayList<Uri> {
        val libProofSet = arrayListOf<Uri>()
        val rootVault: VaultFile? = getParentVaultFile(hash)
        getProofVaultFiles(hash)
            .forEach {

            }

        return libProofSet
    }

    override fun getProofItem(uri: Uri?): InputStream? {
        return if (uri?.scheme.equals("file")){
            FileInputStream(uri?.toFile())
        } else null
    }

    private fun getParentVaultFile(hash: String?):VaultFile? {
        //MyApplication.vault.
        return MyApplication.vault.builder(hash).build()
    }

}