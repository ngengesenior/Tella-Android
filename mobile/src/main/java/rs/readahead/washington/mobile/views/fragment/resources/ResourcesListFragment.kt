package rs.readahead.washington.mobile.views.fragment.resources

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import rs.readahead.washington.mobile.databinding.FragmentResourcesListBinding
import rs.readahead.washington.mobile.views.base_ui.BaseBindingFragment
import rs.readahead.washington.mobile.views.fragment.reports.ReportsViewModel

@AndroidEntryPoint
class ResourcesListFragment :
BaseBindingFragment<FragmentResourcesListBinding>(FragmentResourcesListBinding::inflate){

    private val model: ReportsViewModel by viewModels()

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
        model.listTemplates()
    }

    private fun initObservers() {
        with(model) {

            templates.observe(viewLifecycleOwner, {
                if (it.size == 1) {
                    binding?.textViewEmpty!!.isVisible = true
                    binding!!.templatesRecyclerView.isVisible = false
                } else {
                    binding!!.textViewEmpty.isVisible = false
                    binding!!.templatesRecyclerView.isVisible = true
                    uwaziTemplatesAdapter.setEntityTemplates(it)
                }
            })

        }
    }
    private fun initView() {
        binding.resourcesRecyclerView?.apply {
            layoutManager = LinearLayoutManager(baseActivity)
            adapter = uwaziTemplatesAdapter
        }
    }


}