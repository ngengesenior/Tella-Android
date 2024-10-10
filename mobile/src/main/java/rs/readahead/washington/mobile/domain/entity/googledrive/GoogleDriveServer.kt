package rs.readahead.washington.mobile.domain.entity.googledrive

import rs.readahead.washington.mobile.domain.entity.Server
import rs.readahead.washington.mobile.domain.entity.ServerType

class GoogleDriveServer @JvmOverloads constructor(
    id: Long = 0,
    var folderName: String = "",
    var folderId: String = "",
    val googleClientId: String = ""
) : Server() {
    init {
        serverType = ServerType.GOOGLE_DRIVE
        setId(id)
        name = "Google Drive"
    }
}