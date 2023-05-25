package com.example.app2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.example.app2.data.Faculty
import com.example.app2.data.Plane
import com.example.app2.data.Student
import com.example.app2.repository.FacultyRepository
import com.example.app2.ui.*
import java.util.*

class MainActivity : AppCompatActivity(),
    FacultyFragment.Callbacks,
    GroupFragment.Callbacks,
    GroupListFragment.Callbacks{
    private var myNewFaculty: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacultyRepository.newInstance()
        FacultyRepository.get().loadData(this)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameMain, FacultyFragment.newInstance(), FACULTY_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                }
                else {
                    finish()
                }
            }
        })

    }

    override fun onStop() {
        super.onStop()
        FacultyRepository.get().saveData(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        myNewFaculty = menu?.findItem(R.id.myNewFacultyGroup)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.myNewFacultyGroup -> {
                val myFragment = supportFragmentManager.findFragmentByTag(GROUP_TAG)
                if(myFragment == null) {
                    showDateNameInputDialog()
                }
                else
                    showNameInputDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDateNameInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.date_name_input, null)
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editName2) as EditText
        val yearInput = dialogView.findViewById(R.id.dtpCalendar2) as DatePicker
        builder.setTitle("Укажите значение")

        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            val s = nameInput.text.toString()
            val year = yearInput.year

            if (year > 2023)
            {
                Toast.makeText(this, "Год не должен превышать текущий!", Toast.LENGTH_SHORT).show()
            }
            else {
                val Date = Date(yearInput.year, yearInput.month, yearInput.dayOfMonth)
                var _faculty = Faculty(s, Date)
                if (s.isNotBlank() && year != null) {
                    FacultyRepository.get().newFaculty(_faculty)
                }
            }
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        val alert = builder.create()
        alert.show()
    }

    private fun showNameInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.name_input, null)
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editName) as EditText
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView
        builder.setTitle("Укажите значение")
        tvInfo.text = getString(R.string.inputGroup)
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            val s = nameInput.text.toString()
            if (s.isNotBlank()) {
                FacultyRepository.get().newGroup(GroupFragment.getFacultyID, s)
            }
        }
        builder.setNegativeButton(getString(R.string.cancel), null)
        val alert = builder.create()
        alert.show()
    }

    override fun setTitle(_title: String) {
        title = _title
    }

    override fun showStudent(groupID: UUID,_student: Student?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameMain, StudentFragment.newInstance(groupID,_student), STUDENT_TAG)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun showGroupFragment(facultyID: UUID) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameMain, GroupFragment.newInstance(facultyID), GROUP_TAG)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun showSeats(_plane : Plane?) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameMain, SeatsFragment.newInstance(_plane), _plane!!.name)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }
}