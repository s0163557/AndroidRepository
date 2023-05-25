package com.example.app2.ui

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColor
import androidx.core.view.size
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.app2.R
import com.example.app2.data.Faculty
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentGroupBinding
import com.example.app2.databinding.LayoutStudentListelementBinding
import com.example.app2.repository.FacultyRepository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.UUID

const val GROUP_TAG = "GroupFragment"

class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.second_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    private var CurrentFaculty : Faculty = Faculty()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myEditFacultyGroup -> {
                if (binding.tabGroup.tabCount != 0) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setCancelable(true)
                    val dialogView =
                        LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)
                    builder.setView(dialogView)
                    val nameInput = dialogView.findViewById(R.id.editName) as EditText
                    var currentGroup = CurrentFaculty?.groups!!.get(tabPosition)
                    nameInput.setText(currentGroup.name)
                    val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView
                    builder.setTitle("Укажите значение")
                    tvInfo.text = getString(R.string.inputGroup)
                    builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
                        val s = nameInput.text.toString()
                        if (s.isNotBlank()) {
                            var currentGroup = CurrentFaculty?.groups!!.get(tabPosition)
                            FacultyRepository.get().editGroup(currentGroup, s)
                        }
                    }
                    builder.setNegativeButton(getString(R.string.cancel), null)
                    val alert = builder.create()
                    alert.show()
                }
                true
            }
            R.id.myDeleteFacultyGroup -> {
                if (binding.tabGroup.tabCount != 0) {
                    if (binding.tabGroup.tabCount != 1) {
                        var currentGroup = CurrentFaculty?.groups!!.get(tabPosition)
                        CurrentFaculty?.groups!!.remove(currentGroup)
                        binding.tabGroup.removeTab(Currenttab!!)
                    } else {
                        var currentGroup = CurrentFaculty?.groups!!.get(tabPosition)
                        CurrentFaculty?.groups!!.remove(currentGroup)
                        binding.tabGroup.removeAllTabs()
                        binding.vpGroup.adapter?.notifyDataSetChanged()
                        binding.faBtnAddStudent.visibility = View.GONE
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private lateinit var _facultyID: UUID
        fun newInstance(facultyID: UUID): GroupFragment {
            _facultyID = facultyID
            return GroupFragment()
        }

        val getFacultyID
            get() = _facultyID
    }

    private lateinit var viewModel: GroupViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(GroupViewModel::class.java)
        viewModel.faculty.observe(viewLifecycleOwner) {
            updateUI(it)
            callbacks?.setTitle(it?.name ?: "")
        }
        viewModel.setFaculty(_facultyID)
    }

    var Currenttab = TabLayout.Tab()
    var tabPosition = 0

    private fun updateUI(faculty: Faculty) {
        binding.tabGroup.clearOnTabSelectedListeners()
        binding.tabGroup.removeAllTabs()
        CurrentFaculty = faculty

        for (i in 0 until (faculty?.groups?.size ?: 0)) {
            binding.tabGroup.addTab(binding.tabGroup.newTab().apply {
                text = i.toString()
            })
        }

        val adapter = GroupPageAdapter(requireActivity(), faculty!!)
        binding.vpGroup.adapter = adapter
        TabLayoutMediator(binding.tabGroup, binding.vpGroup, true, true) { tab, pos ->
            tab.text = faculty?.groups?.get(pos)?.name
        }.attach()

        binding.tabGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
                Currenttab = tab!!
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Currenttab = tab!!
            }
        })

        binding.faBtnAddStudent.visibility =
            if ((faculty?.groups?.size ?: 0) == 0)
                View.GONE
            else {
                binding.faBtnAddStudent.setOnClickListener {
                    callbacks?.showStudent(faculty?.groups!!.get(tabPosition).id)
                }
                View.VISIBLE
            }

        binding.faBtnDelete.visibility = View.GONE
        /*
            if ((faculty?.groups?.size ?: 0) == 0)
                View.GONE
            else {
                binding.faBtnDelete.setOnClickListener {
                    var currentGroup = faculty?.groups!!.get(tabPosition)
                    faculty?.groups!!.remove(currentGroup)
                    binding.tabGroup.removeTab(Currenttab!!)
                }
                View.VISIBLE
            }
*/
        binding.faBtnEdit.visibility =View.GONE
        /*
            if ((faculty?.groups?.size ?: 0) == 0)
                View.GONE
            else {
                binding.faBtnEdit.setOnClickListener {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setCancelable(true)
                    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)
                    builder.setView(dialogView)
                    val nameInput = dialogView.findViewById(R.id.editName) as EditText
                    var currentGroup = faculty?.groups!!.get(tabPosition)
                    nameInput.setText(currentGroup.name)
                    val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView
                    builder.setTitle("Укажите значение")
                    tvInfo.text = getString(R.string.inputGroup)
                    builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
                        val s = nameInput.text.toString()
                        if (s.isNotBlank()) {
                            var currentGroup = faculty?.groups!!.get(tabPosition)
                            FacultyRepository.get().editGroup(currentGroup, s)
                        }
                    }
                    builder.setNegativeButton(getString(R.string.cancel), null)
                    val alert = builder.create()
                    alert.show()
                    alert.show()
                }
                View.VISIBLE
            }
            */
    }

    private inner class GroupPageAdapter(fa: FragmentActivity, private val faculty: Faculty) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return (faculty.groups?.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment {
            return GroupListFragment(faculty.groups?.get(position)!!)
        }
    }

    interface Callbacks {
        fun setTitle(_title: String)
        fun showStudent(groupID: UUID, _student: Student? = null)
    }

    var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }
}

/*
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_group_list, null)
        var departureDate = dialogView.findViewById(R.id.dtpDepartureCalendar2) as DatePicker
        val calendar: Calendar = Calendar.getInstance()
        calendar.setTimeInMillis(System.currentTimeMillis())
        departureDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { datePicker, year, month, dayOfMonth ->
            Toast.makeText(context, "Дата изменилась!", Toast.LENGTH_SHORT).show()
            binding.rvGroupList.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )
            binding.rvGroupList.adapter = GroupListAdapter(group.students ?: emptyList())
            viewModel = ViewModelProvider(this).get(GroupListViewModel::class.java)
        }
 */