package rs.readahead.washington.mobile.domain.entity.reports


data class ResourceTemplate(
    var id: Long = 0,
    var title: String? = "",
    var fileName: String? = "",
    var size: Long,
    var createdAt: String? = ""
)
