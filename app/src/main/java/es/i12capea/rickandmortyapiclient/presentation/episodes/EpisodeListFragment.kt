package es.i12capea.rickandmortyapiclient.presentation.episodes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import es.i12capea.rickandmortyapiclient.R
import es.i12capea.rickandmortyapiclient.presentation.common.BaseListAdapter
import es.i12capea.rickandmortyapiclient.presentation.common.displayErrorDialog
import es.i12capea.rickandmortyapiclient.presentation.entities.Episode
import es.i12capea.rickandmortyapiclient.presentation.episodes.state.EpisodesStateEvent
import kotlinx.android.synthetic.main.episode_list_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class EpisodeListFragment (
) : Fragment(){

    private val viewModel : EpisodesViewModel by  activityViewModels()

    @Inject
    lateinit var episodeListAdapter : EpisodeListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        subscribeObservers()
        viewModel.getEpisodeList()?.let {

        } ?: viewModel.setStateEvent(EpisodesStateEvent.GetAllEpisodes())
        //(activity as AppCompatActivity).setSupportActionBar()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.episode_list_fragment, container, false)
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            dataState.data?.let { data ->
                data.episodes?.let {
                    viewModel.addToEpisodeList(it)
                }
            }

            dataState.loading.let {
                if(it){
                    progress_bar.visibility = View.VISIBLE
                }else{
                    progress_bar.visibility = View.INVISIBLE
                }
            }

            dataState.error?.let {
                this.displayErrorDialog(it.desc)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.episodes?.let {
                episodeListAdapter.submitList(it)
            }

            viewState.character?.let {

            }
        })
    }

    private fun initRecyclerView(){
        rv_episodes.apply {
            layoutManager = LinearLayoutManager(this@EpisodeListFragment.context)
            adapter = episodeListAdapter

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == episodeListAdapter.itemCount.minus(1)) {
                        viewModel.setStateEvent(EpisodesStateEvent.GetAllEpisodes(viewModel.getActualPage() + 1 ))
                        Log.d("A", "LastPositionReached")
                    }
                }
            })

        }
    }
}