package at.spiceburg.roarfit.features.main.history

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import at.spiceburg.roarfit.R
import at.spiceburg.roarfit.features.main.MainActivity
import at.spiceburg.roarfit.features.main.MainViewModel
import at.spiceburg.roarfit.utils.Constants
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = (requireActivity() as MainActivity)
        val sp = activity.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        val jwt: String = sp.getString(Constants.JWT, null)!!

        val adapter = HistoryAdapter(activity)
        list_history.adapter = adapter
        val linearLayoutManager = LinearLayoutManager(activity)
        list_history.layoutManager = linearLayoutManager

        list_history.addOnScrollListener(object :
            EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                Log.d(TAG, "page: $page, totalItem: $totalItemsCount")
                viewModel.getExerciseHistory(jwt, page).observe(viewLifecycleOwner) { res ->
                    when {
                        res.isSuccess() -> {
                            adapter.addMoreExercise(res.data!!, view)
                            activity.progressMain.hide()
                        }
                        res.isLoading() -> {
                            activity.progressMain.show()
                        }
                        else -> {
                            activity.progressMain.hide()
                            activity.handleNetworkError(res.error!!)
                        }
                    }
                }
            }
        })

        viewModel.getExerciseHistory(jwt, 0).observe(viewLifecycleOwner) { res ->
            Log.d(TAG, "first get exercises history called")
            when {
                res.isSuccess() -> {
                    adapter.setExercises(res.data!!)
                    activity.progressMain.hide()
                }
                res.isLoading() -> {
                    activity.progressMain.show()
                }
                else -> {
                    activity.progressMain.hide()
                    activity.handleNetworkError(res.error!!)
                }
            }
        }
    }

    companion object {
        private val TAG = HistoryFragment::class.java.simpleName
    }
}
