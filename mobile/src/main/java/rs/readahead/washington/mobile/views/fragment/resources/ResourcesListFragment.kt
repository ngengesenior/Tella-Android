package rs.readahead.washington.mobile.views.fragment.resources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import rs.readahead.washington.mobile.databinding.FragmentResourcesListBinding
import rs.readahead.washington.mobile.domain.entity.reports.TellaReportServer
import rs.readahead.washington.mobile.views.base_ui.BaseBindingFragment

@AndroidEntryPoint
class ResourcesListFragment :
    BaseBindingFragment<FragmentResourcesListBinding>(FragmentResourcesListBinding::inflate) {

    private val model: ResourcesViewModel by viewModels()
    private lateinit var selectedServer: TellaReportServer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            initView()
        }
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        model.getResources()
    }

    private fun initObservers() {
        with(model) {
            serversList.observe(viewLifecycleOwner, {
            })
        }
    }

    private fun initView() {
        binding.resourcesRecyclerView.apply {
            layoutManager = LinearLayoutManager(baseActivity)
            // adapter =
        }
    }


}