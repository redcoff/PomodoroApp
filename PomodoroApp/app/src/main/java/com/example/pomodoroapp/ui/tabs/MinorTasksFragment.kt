package com.example.pomodoroapp.ui.tabs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pomodoroapp.R
import com.example.pomodoroapp.adapter.MinorTasksAdapter
import com.example.pomodoroapp.databinding.ActivityMinorTasksBinding
import com.example.pomodoroapp.model.MinorTask
import com.example.pomodoroapp.model.Task
import com.example.pomodoroapp.ui.AddMinorTaskActivity
import com.example.pomodoroapp.viewmodel.MinorTaskViewModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import kotlinx.android.synthetic.main.activity_minor_tasks.view.*

class MinorTasksFragment : Fragment(), MinorTasksAdapter.Interaction {

    private val minorTaskViewModel: MinorTaskViewModel by viewModels()

    private var _binding: ActivityMinorTasksBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var myAdapter: MinorTasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = ActivityMinorTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        myAdapter = MinorTasksAdapter(this)
        with(binding.neodkladne) {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

            minorTaskViewModel.allTasks.value?.let {
                myAdapter.swapData(it)
            }
        }

        minorTaskViewModel.allTasks.observe(viewLifecycleOwner){
            myAdapter.swapData(it)
        }

        binding.fab.setOnClickListener {
            startActivity(Intent(requireContext(), AddMinorTaskActivity::class.java))
        }

        setRecyclerViewItemTouchListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerViewItemTouchListener() {
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder1: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val item = (viewHolder as MinorTasksAdapter.TaskViewHolder).storedItem
//                PoiUtils.PoiList.remove(item)
//                poiListAdapter.swapData(PoiUtils.PoiList)
//                minorTaskViewModel.deletePoi(item)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.neodkladne)
    }

    override fun itemClicked(item: MinorTask) {
        Snackbar.make(binding.root, "Item id=${item.name} clicked", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MinorTasksAdapter()
    }

    //override fun itemClicked(item: Task) {
    //    Snackbar.make(binding.root, "Item id=${item.id} clicked", Snackbar.LENGTH_SHORT).show()
    //}

}

