package rs.readahead.washington.mobile.data.resources.repository

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rs.readahead.washington.mobile.data.database.DataSource
import rs.readahead.washington.mobile.data.entity.reports.ProjectSlugResourceResponse
import rs.readahead.washington.mobile.data.entity.reports.mapper.mapToDomainModel
import rs.readahead.washington.mobile.data.reports.remote.ReportsApiService
import rs.readahead.washington.mobile.data.reports.utils.ParamsNetwork.URL_PROJECTS
import rs.readahead.washington.mobile.data.resources.remote.ResourcesApiService
import rs.readahead.washington.mobile.data.resources.utils.ParamsNetwork.URL_RESOURCE
import rs.readahead.washington.mobile.domain.entity.reports.TellaReportServer
import rs.readahead.washington.mobile.domain.repository.resources.ResourcesRepository
import rs.readahead.washington.mobile.util.StringUtils
import javax.inject.Inject


class ResourcesRepositoryImp @Inject internal constructor(
    private val apiService: ResourcesApiService,
    private val dataSource: DataSource
) : ResourcesRepository {

    private val disposables = CompositeDisposable()

    /**
     * Gets list of resources from the server.
     *
     * @param server Project server connection to get resources from.
     */
    override fun getResourcesResult(server: TellaReportServer): Single<List<ProjectSlugResourceResponse>> {
        val url = server.url + URL_RESOURCE + URL_PROJECTS
        val url1 = StringUtils.append(
            '/',
            url,
            "?projectId[]=${server.projectId}"
        )
        return apiService.getResources(url1, access_token = server.accessToken)
            .subscribeOn(Schedulers.io())
            .map { results ->

                val slugs = mutableListOf<ProjectSlugResourceResponse>()
                results.forEach {
                    slugs.add(it.mapToDomainModel())
                }

                return@map slugs
            }
    }

    override fun getDisposable() = disposables

    override fun cleanup() {
        disposables.clear()
    }
}