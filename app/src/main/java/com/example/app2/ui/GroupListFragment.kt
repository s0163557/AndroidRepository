package com.example.app2.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app2.R
import com.example.app2.data.Group
import com.example.app2.data.Plane
import com.example.app2.data.Seat
import com.example.app2.data.Student
import com.example.app2.databinding.FragmentGroupListBinding
import com.example.app2.repository.FacultyRepository
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import java.util.stream.Collectors


class GroupListFragment(private val group: Group) : Fragment() {
    private var _binding: FragmentGroupListBinding? = null
    private val binding
        get() = _binding!!

    private lateinit var viewModel: GroupListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvGroupList.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL, false
        )
        binding.rvGroupList.adapter = GroupListAdapter(group.students ?: emptyList())
        viewModel = ViewModelProvider(this).get(GroupListViewModel::class.java)
    }

    private var lastItemView: View? = null

    fun getFutureDays(times: Int, dayOfWeek: DayOfWeek): List<LocalDate>? {
        val list: MutableList<LocalDate> = ArrayList()
        var date = LocalDate.now()

        while (date.dayOfWeek != dayOfWeek) {
            date = date.plusDays(1)
        }

        for (i in 1..times) {
            list.add(date)
            date = date.plusWeeks(1)
        }
        return list
    }

    private inner class GroupHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var student: Student

        fun bind(student: Student) {
            this.student = student
            val s = "Рейс: ${group.name}-${student.ArrivalCity}\n Цена: ${student.Sale}"
            itemView.findViewById<TextView>(R.id.tvElement).text = s
            itemView.findViewById<ConstraintLayout>(R.id.constraintButtons).visibility = View.GONE
            itemView.findViewById<ImageButton>(R.id.ibDelete).setOnClickListener {
                showDeleteDialog(student)
            }
            itemView.findViewById<ImageButton>(R.id.ibEdit).setOnClickListener {
                callbacks?.showStudent(group.id, student)
            }

            itemView.findViewById<ImageButton>(R.id.ibOrder).setOnClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
                builder.setCancelable(true)
                val dialogView = LayoutInflater.from(context).inflate(R.layout.order_input, null)
                builder.setView(dialogView)
                val Flight = dialogView.findViewById<TextView>(R.id.tvFlight)
                Flight.setText(Flight.text.toString() + group.name+"-" + student.ArrivalCity)
                val Sale = dialogView.findViewById<TextView>(R.id.tvSale)
                Sale.setText(Sale.text.toString() + student.Sale)
                val DepTime = dialogView.findViewById<TextView>(R.id.tvDepartureTime)
                DepTime.setText(DepTime.text.toString() + student.DepartureJour + ":" + student.DepartureMinute)
                val ArrTime = dialogView.findViewById<TextView>(R.id.tvArrivalTime)
                ArrTime.setText(ArrTime.text.toString() + student.Arrivalhour + ":" + student.ArrivalMinute)

                val dates = getFutureDays(52, student.DayOfWeek)
                val spDate = dialogView.findViewById<Spinner>(R.id.spDate)
                val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dates!!)
                spDate.adapter = adapter1

                val seatButton = dialogView.findViewById<Button>(R.id.selectSeatBtn)
                seatButton.visibility = View.INVISIBLE
                seatButton.setOnClickListener{

                    var currentPlane = student.planes.find {
                        it.name == student.NameOfPlane &&
                        it.date == spDate.selectedItem.toString()
                    }

                    if (currentPlane == null) {
                        val planeTypes = arrayOf("Airbus A319", "Airbus A320", "Airbus A321")
                        val selectedPlaneTypeIndex = planeTypes.indexOf(student.NameOfPlane)
                        val planeTypeRows = arrayOf(4, 5, 6)[selectedPlaneTypeIndex]
                        currentPlane = Plane(
                            name = student.NameOfPlane,
                            date = spDate.selectedItem.toString(),
                            amRows = planeTypeRows,
                            amCols = 5
                        )
                        FacultyRepository.get().addSeats(currentPlane)
                        student.planes += currentPlane
                    }
                    callbacks?.showSeats(currentPlane)
                   // selectSeatDialog(currentPlane!!)
                }
                builder.setPositiveButton("Выбрать место") { _, _ ->
                    var currentPlane = student.planes.find {
                        it.name == student.NameOfPlane &&
                                it.date == spDate.selectedItem.toString()
                    }

                    if (currentPlane == null) {
                        val planeTypes = arrayOf("Airbus A319", "Airbus A320", "Airbus A321")
                        val selectedPlaneTypeIndex = planeTypes.indexOf(student.NameOfPlane)
                        val planeTypeRows = arrayOf(4, 5, 6)[selectedPlaneTypeIndex]
                        currentPlane = Plane(
                            name = student.NameOfPlane,
                            date = spDate.selectedItem.toString(),
                            amRows = planeTypeRows,
                            amCols = 5
                        )
                        FacultyRepository.get().addSeats(currentPlane!!)
                        student.planes += currentPlane!!
                    }
                    callbacks?.showSeats(currentPlane)
                }
                builder.setNegativeButton(getString(R.string.cancel), null)
                val alert = builder.create()
                alert.show()
            }
        }
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val cl = itemView.findViewById<ConstraintLayout>(R.id.constraintButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.constraintButtons)?.visibility =
                View.GONE
            lastItemView = if (lastItemView == itemView) null else itemView
        }
    }

    private fun selectSeatDialog(flightPlane: Plane) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setTitle("Забронировать место")
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.select_seat, null)
        builder.setView(dialogView)
        //наполнить
        val adapter = SeatAdapter(flightPlane.seats, flightPlane.amCols)
        val gridView = dialogView.findViewById<GridView>(R.id.gvSeats)
        gridView.numColumns = flightPlane.amCols
        gridView.adapter = adapter

        builder.setPositiveButton("Принять") { _, _ ->
            if (adapter.getSelectedSeat(dialogView) != -1) {
                Log.d("A", "ReachedHere " + adapter.getSelectedSeat(dialogView).toString())
                flightPlane.seats.find {
                    it.position == adapter.getSelectedSeat(dialogView) + 1
                }!!.isBooked = true
                Log.d("A", "isBooked= " + flightPlane.seats.find {
                    it.position == adapter.getSelectedSeat(dialogView) + 1
                }!!.isBooked.toString())
            }
        }
        builder.setNegativeButton("Отмена", null)
        val alert = builder.create()
        alert.show()
    }

    class SeatAdapter(private var seats: List<Seat>, private val cols: Int) : BaseAdapter() {

        private var selectedSeatPosition = -1 // индекс выбранного места

        override fun getCount(): Int = seats.size

        override fun getItem(position: Int): Any = seats[position]

        override fun getItemId(position: Int): Long = position.toLong()

        private fun Int.dpToPx(): Int {
            return (this * Resources.getSystem().displayMetrics.density).toInt()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val button = convertView as? Button ?: Button(parent?.context)
            val row = position / cols // индекс строки
            val col = position % cols // индекс колонки
            val seat = seats[row * cols + col] // выберите место на основе индексов строки и колонки
            button.text = seat.name
            button.isEnabled = !seat.isBooked
            button.setBackgroundColor(
                ContextCompat.getColor(
                    parent?.context!!,
                    if (seat.isBooked) R.color.red else R.color.grey
                )
            )
            button.setOnClickListener {
                if (!seat.isBooked) {
                    // установить цвет выбранной кнопки на синий
                    button.setBackgroundColor(
                        ContextCompat.getColor(
                            parent?.context!!,
                            R.color.blue
                        )
                    )


                    if (selectedSeatPosition != -1) {
                        parent.getChildAt(selectedSeatPosition).setBackgroundColor(
                            ContextCompat.getColor(
                                parent?.context!!,
                                R.color.grey
                            )
                        )
                    }
                    selectedSeatPosition = position
                } else {
                    Toast.makeText(parent?.context, "Место занято", Toast.LENGTH_LONG).show()
                }
            }
            val params = AbsListView.LayoutParams(
                35.dpToPx(),
                35.dpToPx()
            )
            button.layoutParams = params
            return button
        }

        fun getSelectedSeat(parent: View): Int {
            return selectedSeatPosition
        }
    }

    private fun showDeleteDialog(student: Student) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить рейс ${group.name}-${student.ArrivalCity} из списка?")
        builder.setPositiveButton("Подтверждение") { _, _ ->
            viewModel.deleteStudent(group.id, student)
        }
        builder.setNegativeButton("Отмена", null)
        val alert = builder.create()
        alert.show()
    }

    private inner class GroupListAdapter(private val items: List<Student>) :
        RecyclerView.Adapter<GroupHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GroupHolder {
            val view = layoutInflater.inflate(
                R.layout.layout_student_listelement, parent, false
            )
            return GroupHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: GroupHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    interface Callbacks {
        fun showStudent(groupID: UUID, _student: Student?)
        fun showSeats(_plane : Plane?)
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

